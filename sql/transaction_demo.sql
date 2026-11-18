-- ============================================================================
-- Jogging Club Management System (JCMS)
-- Transaction Demonstrations & Concurrency Control (transaction_demo.sql)
-- Target DBMS: MySQL 8.0+
-- Storage Engine: InnoDB (Row-level locking, ACID compliance)
-- ============================================================================
-- NOTE FOR MYSQL WORKBENCH USERS:
-- MySQL does not allow procedural flow control (such as IF...THEN...ELSE)
-- in naked SQL scripts outside stored procedures.
--
-- This script provides two execution approaches:
--   APPROACH A: Production-ready Stored Procedures encapsulating ACID logic,
--               row-level locking (SELECT ... FOR UPDATE), capacity validation,
--               duplicate prevention, and automated COMMIT/ROLLBACK.
--   APPROACH B: Manual, interactive transaction scripts you can highlight and
--               run line-by-line in MySQL Workbench to observe transaction states.
-- ============================================================================

USE jcms_db;

-- ============================================================================
-- SECTION 1: STORED PROCEDURES (BUSINESS TRANSACTION LOGIC)
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_register_member;
DROP PROCEDURE IF EXISTS sp_cancel_registration;

DELIMITER $$

-- ----------------------------------------------------------------------------
-- PROCEDURE: sp_register_member
-- Purpose: Handles new registration, capacity check, and re-registration (reactivation).
-- Concurrency: Row-level lock on the session row (FOR UPDATE) prevents race conditions.
-- ----------------------------------------------------------------------------
CREATE PROCEDURE sp_register_member(
    IN  p_member_id     INT,
    IN  p_session_id    INT,
    OUT p_result_code   VARCHAR(50),
    OUT p_result_message VARCHAR(255)
)
proc_body: BEGIN
    DECLARE v_capacity       INT;
    DECLARE v_session_status VARCHAR(20);
    DECLARE v_active_count   INT DEFAULT 0;
    DECLARE v_reg_id         INT DEFAULT NULL;
    DECLARE v_reg_status     VARCHAR(20) DEFAULT NULL;

    -- Error handler for unexpected SQL exceptions
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = 'ERROR_SQL_EXCEPTION';
        SET p_result_message = 'An unexpected database error occurred. Transaction rolled back.';
    END;

    -- Start ACID Transaction
    START TRANSACTION;

    -- 1. Lock the session record using FOR UPDATE to prevent concurrent overbooking
    SELECT capacity, status 
    INTO v_capacity, v_session_status
    FROM sessions
    WHERE session_id = p_session_id
    FOR UPDATE;

    -- Validate session existence
    IF v_capacity IS NULL THEN
        ROLLBACK;
        SET p_result_code = 'SESSION_NOT_FOUND';
        SET p_result_message = CONCAT('Session ID ', p_session_id, ' does not exist.');
        LEAVE proc_body;
    END IF;

    -- Validate session status
    IF v_session_status NOT IN ('OPEN', 'FULL') THEN
        ROLLBACK;
        SET p_result_code = 'SESSION_NOT_AVAILABLE';
        SET p_result_message = CONCAT('Cannot register. Session is ', v_session_status, '.');
        LEAVE proc_body;
    END IF;

    -- 2. Validate member existence
    IF NOT EXISTS (SELECT 1 FROM members WHERE member_id = p_member_id) THEN
        ROLLBACK;
        SET p_result_code = 'MEMBER_NOT_FOUND';
        SET p_result_message = CONCAT('Member ID ', p_member_id, ' does not exist.');
        LEAVE proc_body;
    END IF;

    -- 3. Check for existing registration record (Locking registration row if present)
    SELECT registration_id, status
    INTO v_reg_id, v_reg_status
    FROM registrations
    WHERE member_id = p_member_id AND session_id = p_session_id
    FOR UPDATE;

    -- Prevent duplicate active registration
    IF v_reg_status = 'CONFIRMED' THEN
        ROLLBACK;
        SET p_result_code = 'DUPLICATE_REGISTRATION';
        SET p_result_message = 'Member is already confirmed for this session. Duplicate registration rejected.';
        LEAVE proc_body;
    END IF;

    -- 4. Calculate current active registrations for this session
    SELECT COUNT(*) 
    INTO v_active_count
    FROM registrations
    WHERE session_id = p_session_id AND status = 'CONFIRMED';

    -- 5. Enforce Capacity Protection
    IF v_active_count >= v_capacity THEN
        -- Ensure session status is flagged FULL
        UPDATE sessions SET status = 'FULL' WHERE session_id = p_session_id;
        ROLLBACK;
        SET p_result_code = 'CAPACITY_EXCEEDED';
        SET p_result_message = CONCAT('Registration failed: Session is at full capacity (', v_active_count, '/', v_capacity, ').');
        LEAVE proc_body;
    END IF;

    -- 6. Insert new registration OR reactivate existing cancelled registration
    IF v_reg_id IS NOT NULL AND v_reg_status = 'CANCELLED' THEN
        -- Reactivate existing record (preserves history, avoids duplicate row)
        UPDATE registrations
        SET status = 'CONFIRMED',
            updated_at = NOW(),
            cancelled_at = NULL
        WHERE registration_id = v_reg_id;

        SET p_result_code = 'REACTIVATED_SUCCESS';
        SET p_result_message = CONCAT('Registration reactivated successfully (Registration ID: ', v_reg_id, ').');
    ELSE
        -- Fresh registration insert
        INSERT INTO registrations (member_id, session_id, status, registered_at)
        VALUES (p_member_id, p_session_id, 'CONFIRMED', NOW());

        SET p_result_code = 'REGISTRATION_SUCCESS';
        SET p_result_message = 'Member successfully registered for session.';
    END IF;

    -- 7. If capacity reached after this registration, update session status to FULL
    IF (v_active_count + 1) >= v_capacity THEN
        UPDATE sessions SET status = 'FULL' WHERE session_id = p_session_id;
    END IF;

    -- Commit transaction
    COMMIT;
END proc_body$$

-- ----------------------------------------------------------------------------
-- PROCEDURE: sp_cancel_registration
-- Purpose: Cancels a registration, preserves history, and frees session capacity.
-- ----------------------------------------------------------------------------
CREATE PROCEDURE sp_cancel_registration(
    IN  p_member_id     INT,
    IN  p_session_id    INT,
    OUT p_result_code   VARCHAR(50),
    OUT p_result_message VARCHAR(255)
)
proc_body: BEGIN
    DECLARE v_reg_id     INT;
    DECLARE v_reg_status VARCHAR(20);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = 'ERROR_SQL_EXCEPTION';
        SET p_result_message = 'An unexpected database error occurred during cancellation. Rolled back.';
    END;

    START TRANSACTION;

    -- Lock registration row
    SELECT registration_id, status
    INTO v_reg_id, v_reg_status
    FROM registrations
    WHERE member_id = p_member_id AND session_id = p_session_id
    FOR UPDATE;

    IF v_reg_id IS NULL THEN
        ROLLBACK;
        SET p_result_code = 'NOT_REGISTERED';
        SET p_result_message = 'No registration found for this member and session.';
        LEAVE proc_body;
    END IF;

    IF v_reg_status = 'CANCELLED' THEN
        ROLLBACK;
        SET p_result_code = 'ALREADY_CANCELLED';
        SET p_result_message = 'Registration is already cancelled.';
        LEAVE proc_body;
    END IF;

    -- Update status to CANCELLED and preserve timestamp
    UPDATE registrations
    SET status = 'CANCELLED',
        cancelled_at = NOW()
    WHERE registration_id = v_reg_id;

    -- Reopen session if it was marked FULL
    UPDATE sessions
    SET status = 'OPEN'
    WHERE session_id = p_session_id AND status = 'FULL';

    COMMIT;
    SET p_result_code = 'CANCEL_SUCCESS';
    SET p_result_message = CONCAT('Registration ', v_reg_id, ' successfully cancelled.');
END proc_body$$

DELIMITER ;

-- ============================================================================
-- SECTION 2: EXECUTABLE DEMONSTRATIONS FOR MYSQL WORKBENCH
-- ============================================================================

-- ----------------------------------------------------------------------------
-- DEMO 1: SUCCESSFUL REGISTRATION & COMMIT
-- Scenario: Member 12 registers for Session 6 (Fresh Starters Morning Jog, Capacity 15).
-- Demonstrates: START TRANSACTION -> Row Lock -> Validation -> Insert -> COMMIT.
-- ----------------------------------------------------------------------------
SELECT '--- DEMO 1: SUCCESSFUL REGISTRATION & COMMIT ---' AS demonstration;

-- Call stored procedure
CALL sp_register_member(12, 6, @code_d1, @msg_d1);
SELECT @code_d1 AS result_code, @msg_d1 AS result_message;

-- Verify committed row
SELECT 
    r.registration_id, 
    m.full_name, 
    s.session_name, 
    r.status, 
    r.registered_at
FROM registrations r
JOIN members m ON r.member_id = m.member_id
JOIN sessions s ON r.session_id = s.session_id
WHERE r.member_id = 12 AND r.session_id = 6;


-- ----------------------------------------------------------------------------
-- DEMO 2: FAILED REGISTRATION & ROLLBACK (MANUAL WORKBENCH DEMO)
-- Scenario: Member attempts registration, an error or validation condition triggers,
--           and ROLLBACK restores the database state cleanly.
-- ----------------------------------------------------------------------------
SELECT '--- DEMO 2: FAILED REGISTRATION & ROLLBACK ---' AS demonstration;

START TRANSACTION;

-- Step 2.1: Lock session
SELECT session_id, session_name, capacity, status 
FROM sessions 
WHERE session_id = 6 
FOR UPDATE;

-- Step 2.2: Stage a temporary registration
INSERT INTO registrations (member_id, session_id, status, registered_at)
VALUES (10, 6, 'CONFIRMED', NOW());

-- Verify it exists within the uncommitted transaction
SELECT 'Inside transaction (uncommitted):' AS stage, COUNT(*) AS count_inside
FROM registrations 
WHERE member_id = 10 AND session_id = 6;

-- Step 2.3: Simulate transaction failure / cancellation criteria -> ROLLBACK
ROLLBACK;

-- Verify that the uncommitted row was cleanly discarded
SELECT 'After rollback (restored):' AS stage, COUNT(*) AS count_after_rollback
FROM registrations 
WHERE member_id = 10 AND session_id = 6;


-- ----------------------------------------------------------------------------
-- DEMO 3: CAPACITY PROTECTION & OVERBOOKING PREVENTION
-- Scenario: Session 7 (Hill Repeats & Aerobic Power) has Capacity = 4,
--           and already has 4 confirmed members.
-- Member 1 attempts to register, but is blocked by capacity protection.
-- ----------------------------------------------------------------------------
SELECT '--- DEMO 3: CAPACITY PROTECTION ---' AS demonstration;

-- Verify current capacity before attempt
SELECT 
    s.session_id, 
    s.session_name, 
    s.capacity, 
    COUNT(r.registration_id) AS current_confirmed_count,
    s.status
FROM sessions s
LEFT JOIN registrations r ON s.session_id = r.session_id AND r.status = 'CONFIRMED'
WHERE s.session_id = 7
GROUP BY s.session_id, s.session_name, s.capacity, s.status;

-- Attempt registration on full session
CALL sp_register_member(1, 7, @code_d3, @msg_d3);
SELECT @code_d3 AS result_code, @msg_d3 AS result_message;

-- Verify capacity remained strictly protected
SELECT 
    s.session_id, 
    COUNT(r.registration_id) AS confirmed_after_attempt,
    s.capacity,
    s.status
FROM sessions s
LEFT JOIN registrations r ON s.session_id = r.session_id AND r.status = 'CONFIRMED'
WHERE s.session_id = 7
GROUP BY s.session_id, s.capacity, s.status;


-- ----------------------------------------------------------------------------
-- DEMO 4: DUPLICATE REGISTRATION PREVENTION
-- Scenario: Member 12 is already confirmed for Session 6 (from Demo 1).
-- Attempting to register Member 12 again must be rejected without creating duplicates.
-- ----------------------------------------------------------------------------
SELECT '--- DEMO 4: DUPLICATE REGISTRATION PREVENTION ---' AS demonstration;

-- Attempt duplicate registration via stored procedure
CALL sp_register_member(12, 6, @code_d4, @msg_d4);
SELECT @code_d4 AS result_code, @msg_d4 AS result_message;

-- Verify database still has exactly one record for Member 12 in Session 6
SELECT 
    registration_id, 
    member_id, 
    session_id, 
    status, 
    registered_at
FROM registrations
WHERE member_id = 12 AND session_id = 6;


-- ----------------------------------------------------------------------------
-- DEMO 5: CANCELLATION AND RE-REGISTRATION (REACTIVATION)
-- Scenario:
--  Step 5.1: Member 12 cancels registration for Session 6.
--            Record remains in database with status 'CANCELLED' and cancelled_at timestamp.
--  Step 5.2: Member 12 decides to re-register for Session 6.
--            System reactivates the existing row back to 'CONFIRMED',
--            clears cancelled_at, and creates NO duplicate row.
-- ----------------------------------------------------------------------------
SELECT '--- DEMO 5: CANCELLATION AND RE-REGISTRATION ---' AS demonstration;

-- Step 5.1: Cancel registration
CALL sp_cancel_registration(12, 6, @code_d5a, @msg_d5a);
SELECT @code_d5a AS cancel_result_code, @msg_d5a AS cancel_result_message;

-- Verify cancelled state preserved in database
SELECT 
    registration_id, 
    member_id, 
    session_id, 
    status, 
    registered_at, 
    cancelled_at
FROM registrations
WHERE member_id = 12 AND session_id = 6;

-- Step 5.2: Re-register for the same session
CALL sp_register_member(12, 6, @code_d5b, @msg_d5b);
SELECT @code_d5b AS reregister_result_code, @msg_d5b AS reregister_result_message;

-- Verify reactivated record: exactly 1 row exists, status is CONFIRMED, cancelled_at is NULL
SELECT 
    registration_id, 
    member_id, 
    session_id, 
    status, 
    registered_at, 
    updated_at, 
    cancelled_at
FROM registrations
WHERE member_id = 12 AND session_id = 6;
