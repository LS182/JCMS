-- ============================================================================
-- Jogging Club Management System (JCMS)
-- Practical and Analytical SQL Queries (complex_queries.sql)
-- Target DBMS: MySQL 8.0+
-- Demonstrates:
--  - INNER JOIN and multi-table joins
--  - LEFT (OUTER) JOIN and anti-joins
--  - GROUP BY and HAVING clauses
--  - Aggregate functions (COUNT, SUM, AVG, MIN, MAX)
--  - Subqueries (correlated subquery, IN subquery, scalar subquery, derived tables)
--  - Specific Assessment Requirements:
--      * Upcoming sessions with registration counts
--      * Members and their registration history
-- ============================================================================

USE jcms_db;

-- ============================================================================
-- QUERY 1: UPCOMING SESSIONS WITH REGISTRATION COUNTS & AVAILABILITY
-- Assessment Requirement: Upcoming sessions with registration counts
-- Demonstrates: LEFT JOIN, GROUP BY, Aggregate functions (COUNT, CASE), Expressions
-- Purpose:
--   Computes active registered participant counts dynamically (avoiding redundant
--   data storage), calculates remaining spots, and displays live booking status.
-- ============================================================================
SELECT 
    s.session_id,
    s.session_name,
    s.category,
    s.session_date,
    s.start_time,
    s.location,
    s.capacity,
    COUNT(CASE WHEN r.status = 'CONFIRMED' THEN 1 END) AS active_registrations,
    (s.capacity - COUNT(CASE WHEN r.status = 'CONFIRMED' THEN 1 END)) AS spots_remaining,
    CASE 
        WHEN s.status = 'CANCELLED' THEN 'CANCELLED'
        WHEN (s.capacity - COUNT(CASE WHEN r.status = 'CONFIRMED' THEN 1 END)) <= 0 THEN 'FULL'
        ELSE 'OPEN'
    END AS calculated_status
FROM sessions s
LEFT JOIN registrations r 
    ON s.session_id = r.session_id
WHERE s.session_date >= '2026-11-17' -- Future/current sessions from seed time
  AND s.status != 'CANCELLED'
GROUP BY 
    s.session_id, 
    s.session_name, 
    s.category, 
    s.session_date, 
    s.start_time, 
    s.location, 
    s.capacity, 
    s.status
ORDER BY s.session_date ASC, s.start_time ASC;

-- ============================================================================
-- QUERY 2: MEMBERS AND THEIR COMPLETE REGISTRATION HISTORY
-- Assessment Requirement: Members and their registration history
-- Demonstrates: INNER JOIN, LEFT JOIN (4-table join), Date formatting, Status tracking
-- Purpose:
--   Lists members alongside all sessions they have booked, showing whether the
--   registration remains confirmed or was cancelled, and whether attendance was
--   recorded if the session took place.
-- ============================================================================
SELECT 
    m.member_id,
    m.full_name AS member_name,
    m.contact_number,
    s.session_id,
    s.session_name,
    s.category,
    s.session_date,
    s.start_time,
    r.registration_id,
    r.status AS registration_status,
    r.registered_at,
    r.cancelled_at,
    COALESCE(a.status, 'NOT MARKED / UPCOMING') AS verified_attendance,
    a.notes AS attendance_notes
FROM members m
INNER JOIN registrations r 
    ON m.member_id = r.member_id
INNER JOIN sessions s 
    ON r.session_id = s.session_id
LEFT JOIN attendance_records a 
    ON r.registration_id = a.registration_id
ORDER BY m.member_id ASC, s.session_date DESC;

-- ============================================================================
-- QUERY 3: CATEGORY PERFORMANCE & CAPACITY UTILIZATION SUMMARY
-- Demonstrates: Derived table subquery, GROUP BY, COUNT, SUM, AVG, ROUND
-- Purpose:
--   Provides management insights into which jogging/running categories draw
--   the highest demand, total seats offered, and percentage capacity filled.
--   Uses a pre-aggregated derived table for registrations to avoid Cartesian
--   capacity duplication across the 1-to-many relationship.
-- ============================================================================
SELECT 
    s.category,
    COUNT(s.session_id) AS total_sessions,
    SUM(s.capacity) AS total_capacity_offered,
    COALESCE(SUM(r.active_count), 0) AS total_confirmed_bookings,
    COALESCE(SUM(r.cancelled_count), 0) AS total_cancellations,
    ROUND(AVG(s.capacity), 1) AS avg_session_capacity,
    ROUND(
        (COALESCE(SUM(r.active_count), 0) * 100.0) / NULLIF(SUM(s.capacity), 0), 
        1
    ) AS overall_utilization_pct
FROM sessions s
LEFT JOIN (
    SELECT 
        session_id,
        COUNT(CASE WHEN status = 'CONFIRMED' THEN 1 END) AS active_count,
        COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) AS cancelled_count
    FROM registrations
    GROUP BY session_id
) r ON s.session_id = r.session_id
GROUP BY s.category
ORDER BY total_confirmed_bookings DESC;

-- ============================================================================
-- QUERY 4: ATTENDANCE COMPLIANCE FOR COMPLETED SESSIONS
-- Demonstrates: INNER JOIN, GROUP BY, HAVING, Aggregate expressions
-- Purpose:
--   Analyzes completed sessions to check attendance rate (how many confirmed
--   registrants actually showed up versus were marked absent).
-- ============================================================================
SELECT 
    s.session_id,
    s.session_name,
    s.category,
    s.session_date,
    COUNT(r.registration_id) AS confirmed_participants,
    COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) AS attendees_present,
    COUNT(CASE WHEN a.status = 'LATE' THEN 1 END) AS attendees_late,
    COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) AS attendees_absent,
    ROUND(
        (COUNT(CASE WHEN a.status IN ('PRESENT', 'LATE') THEN 1 END) * 100.0) / 
        NULLIF(COUNT(r.registration_id), 0),
        1
    ) AS turnout_percentage
FROM sessions s
INNER JOIN registrations r 
    ON s.session_id = r.session_id AND r.status = 'CONFIRMED'
INNER JOIN attendance_records a 
    ON r.registration_id = a.registration_id
WHERE s.status = 'COMPLETED'
GROUP BY 
    s.session_id, 
    s.session_name, 
    s.category, 
    s.session_date
ORDER BY s.session_date DESC;

-- ============================================================================
-- QUERY 5: INACTIVE CLUB MEMBERS AUDIT (ANTI-JOIN)
-- Demonstrates: LEFT JOIN ... WHERE IS NULL (Relational Anti-Join)
-- Purpose:
--   Finds club members who have registered with the club but have zero active
--   session bookings (useful for re-engagement email campaigns).
-- ============================================================================
SELECT 
    m.member_id,
    m.full_name,
    m.email,
    m.contact_number,
    m.created_at AS member_since,
    COUNT(r.registration_id) AS total_historical_registrations,
    COUNT(CASE WHEN r.status = 'CONFIRMED' THEN 1 END) AS active_registrations
FROM members m
LEFT JOIN registrations r 
    ON m.member_id = r.member_id
GROUP BY 
    m.member_id, 
    m.full_name, 
    m.email, 
    m.contact_number, 
    m.created_at
HAVING active_registrations = 0
ORDER BY m.created_at ASC;

-- ============================================================================
-- QUERY 6: SESSIONS AT OR NEARING CAPACITY (CORRELATED SUBQUERY)
-- Demonstrates: Correlated Subquery in WHERE clause, Scalar comparison
-- Purpose:
--   Selects all sessions where the number of confirmed registrations is within
--   two spots of maximum capacity (or already completely filled).
-- ============================================================================
SELECT 
    s.session_id,
    s.session_name,
    s.category,
    s.session_date,
    s.capacity,
    (
        SELECT COUNT(*) 
        FROM registrations r 
        WHERE r.session_id = s.session_id 
          AND r.status = 'CONFIRMED'
    ) AS confirmed_count,
    s.status AS recorded_status
FROM sessions s
WHERE s.capacity - (
    SELECT COUNT(*) 
    FROM registrations r 
    WHERE r.session_id = s.session_id 
      AND r.status = 'CONFIRMED'
) <= 2
  AND s.status != 'CANCELLED'
ORDER BY confirmed_count DESC;

-- ============================================================================
-- QUERY 7: HIGH-INTENSITY PARTICIPANTS WITH EMERGENCY CONTACTS (SUBQUERY IN)
-- Demonstrates: Subquery with IN operator, Safety audit
-- Purpose:
--   Retrieves members enrolled in demanding sessions ('Advanced Running' or
--   'Marathon Training') along with their emergency contact details for safety.
-- ============================================================================
SELECT 
    m.member_id,
    m.full_name,
    m.contact_number,
    m.emergency_contact_name,
    m.emergency_contact_number
FROM members m
WHERE m.member_id IN (
    SELECT DISTINCT r.member_id
    FROM registrations r
    INNER JOIN sessions s ON r.session_id = s.session_id
    WHERE s.category IN ('Advanced Running', 'Marathon Training')
      AND r.status = 'CONFIRMED'
)
ORDER BY m.full_name ASC;

-- ============================================================================
-- QUERY 8: EXECUTIVE DASHBOARD KPI METRICS (DERIVED TABLES / MULTI-SCALAR)
-- Demonstrates: Scalar subqueries in SELECT clause, High-level aggregation
-- Purpose:
--   A single executive dashboard query providing all top-level club metrics.
-- ============================================================================
SELECT 
    (SELECT COUNT(*) FROM members) AS total_club_members,
    (SELECT COUNT(*) FROM sessions WHERE session_date >= '2026-11-17' AND status != 'CANCELLED') AS upcoming_sessions_count,
    (SELECT COUNT(*) FROM registrations WHERE status = 'CONFIRMED') AS total_active_bookings,
    (SELECT COUNT(*) FROM registrations WHERE status = 'CANCELLED') AS total_cancellations,
    (
        SELECT ROUND((COUNT(CASE WHEN status IN ('PRESENT', 'LATE') THEN 1 END) * 100.0) / COUNT(*), 1)
        FROM attendance_records
    ) AS average_attendance_rate_pct,
    (
        SELECT COUNT(*)
        FROM sessions s
        WHERE s.capacity <= (
            SELECT COUNT(*) 
            FROM registrations r 
            WHERE r.session_id = s.session_id AND r.status = 'CONFIRMED'
        ) AND s.status != 'CANCELLED'
    ) AS sessions_at_full_capacity;
