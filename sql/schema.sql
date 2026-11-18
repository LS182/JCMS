-- ============================================================================
-- Jogging Club Management System (JCMS)
-- Database Schema Definition (schema.sql)
-- Target DBMS: MySQL 8.0+
-- Storage Engine: InnoDB
-- Character Set: UTF-8 (utf8mb4)
-- ============================================================================

-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS jcms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE jcms_db;

-- 2. Drop existing tables in reverse dependency order to avoid FK errors
DROP TABLE IF EXISTS attendance_records;
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS members;

-- ============================================================================
-- TABLE: members
-- Purpose: Stores registered jogging club members and emergency contacts.
-- Normalization: 1NF, 2NF, 3NF compliant.
-- ============================================================================
CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    emergency_contact_name VARCHAR(100) NOT NULL,
    emergency_contact_number VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Business and validation constraints
    CONSTRAINT uq_member_email UNIQUE (email),
    CONSTRAINT chk_member_name_not_empty CHECK (LENGTH(TRIM(full_name)) > 0),
    CONSTRAINT chk_member_email_format CHECK (email LIKE '%_@__%.__%'),
    CONSTRAINT chk_member_contact_len CHECK (LENGTH(TRIM(contact_number)) >= 7),
    CONSTRAINT chk_emergency_name_not_empty CHECK (LENGTH(TRIM(emergency_contact_name)) > 0),
    CONSTRAINT chk_emergency_contact_len CHECK (LENGTH(TRIM(emergency_contact_number)) >= 7)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: sessions
-- Purpose: Stores organized club jogging and running sessions.
-- Note: Redundant participant count is NOT stored here; it is calculated
--       dynamically from active registrations to ensure 3NF compliance.
-- ============================================================================
CREATE TABLE sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    session_name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(150) NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Business and validation constraints
    CONSTRAINT chk_session_name_not_empty CHECK (LENGTH(TRIM(session_name)) > 0),
    CONSTRAINT chk_session_category CHECK (
        category IN (
            'Beginner Jogging',
            'Intermediate Running',
            'Advanced Running',
            'Family Fun Runs',
            'Marathon Training'
        )
    ),
    CONSTRAINT chk_session_status CHECK (
        status IN ('OPEN', 'FULL', 'CANCELLED', 'COMPLETED')
    ),
    CONSTRAINT chk_session_capacity CHECK (capacity > 0),
    CONSTRAINT chk_session_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: registrations
-- Purpose: Records session registrations by club members.
-- Rules:
--  - One record per member per session (UNIQUE KEY uq_member_session).
--  - Supports 'CONFIRMED' and 'CANCELLED' statuses.
--  - Re-registration reactivates an existing record instead of inserting a duplicate.
-- ============================================================================
CREATE TABLE registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    session_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at DATETIME NULL,

    -- Business and validation constraints
    CONSTRAINT chk_registration_status CHECK (status IN ('CONFIRMED', 'CANCELLED')),
    CONSTRAINT uq_member_session UNIQUE (member_id, session_id),

    -- Foreign key constraints
    CONSTRAINT fk_registrations_member 
        FOREIGN KEY (member_id) 
        REFERENCES members(member_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_registrations_session 
        FOREIGN KEY (session_id) 
        REFERENCES sessions(session_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: attendance_records
-- Purpose: Tracks verified attendance when marked by a session coordinator.
-- Rules:
--  - Kept completely separate from registration history.
--  - Created ONLY when attendance is actually marked on the day of the session.
--  - Linked 1-to-1 with a specific registration record.
-- ============================================================================
CREATE TABLE attendance_records (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    marked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    notes VARCHAR(255) NULL,

    -- Business and validation constraints
    CONSTRAINT uq_attendance_registration UNIQUE (registration_id),
    CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),

    -- Foreign key constraints
    CONSTRAINT fk_attendance_registration 
        FOREIGN KEY (registration_id) 
        REFERENCES registrations(registration_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- PERFORMANCE INDEXES
-- Index frequently queried columns (dates, categories, statuses, foreign keys)
-- ============================================================================
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_sessions_date ON sessions(session_date);
CREATE INDEX idx_sessions_category ON sessions(category);
CREATE INDEX idx_sessions_status ON sessions(status);
CREATE INDEX idx_registrations_member ON registrations(member_id);
CREATE INDEX idx_registrations_session ON registrations(session_id);
CREATE INDEX idx_registrations_status ON registrations(status);
CREATE INDEX idx_registrations_session_status ON registrations(session_id, status);
CREATE INDEX idx_attendance_marked_at ON attendance_records(marked_at);
