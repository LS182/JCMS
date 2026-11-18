-- ============================================================================
-- Jogging Club Management System (JCMS)
-- Sample Seed Data (data.sql)
-- Target DBMS: MySQL 8.0+
-- ============================================================================

USE jcms_db;

-- Clear any existing data in reverse dependency order
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendance_records;
TRUNCATE TABLE registrations;
TRUNCATE TABLE sessions;
TRUNCATE TABLE members;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 1. INSERT MEMBERS
-- 12 realistic club members with contact and emergency contact details
-- ============================================================================
INSERT INTO members (member_id, full_name, email, contact_number, emergency_contact_name, emergency_contact_number, created_at)
VALUES
    (1,  'Sipho Dlamini',          'sipho.dlamini@example.com',     '+27 82 123 4567', 'Nomvula Dlamini',          '+27 82 987 6543', '2026-09-01 08:30:00'),
    (2,  'Liam Smith',             'liam.smith@example.com',        '+27 83 234 5678', 'Sarah Smith',             '+27 83 876 5432', '2026-09-05 09:15:00'),
    (3,  'Thabo Mokoena',          'thabo.mokoena@example.com',     '+27 84 345 6789', 'Lerato Mokoena',           '+27 84 765 4321', '2026-09-10 14:00:00'),
    (4,  'Anika Patel',            'anika.patel@example.com',       '+27 82 456 7890', 'Rajesh Patel',             '+27 82 654 3210', '2026-09-15 11:45:00'),
    (5,  'David van der Merwe',    'david.vdm@example.com',         '+27 83 567 8901', 'Elize van der Merwe',     '+27 83 543 2109', '2026-09-20 16:20:00'),
    (6,  'Zanele Khumalo',         'zanele.khumalo@example.com',    '+27 84 678 9012', 'Bongani Khumalo',          '+27 84 432 1098', '2026-09-25 10:10:00'),
    (7,  'Michael Chen',           'michael.chen@example.com',      '+27 82 789 0123', 'Grace Chen',              '+27 82 321 0987', '2026-10-01 13:00:00'),
    (8,  'Keabetswe Ndlovu',       'kea.ndlovu@example.com',        '+27 83 890 1234', 'Jabulani Ndlovu',         '+27 83 210 9876', '2026-10-05 15:30:00'),
    (9,  'Fatima Al-Mansoor',      'fatima.m@example.com',          '+27 84 901 2345', 'Tariq Al-Mansoor',         '+27 84 109 8765', '2026-10-10 09:00:00'),
    (10, 'Johan Botha',            'johan.botha@example.com',       '+27 82 012 3456', 'Marietjie Botha',         '+27 82 098 7654', '2026-10-15 17:40:00'),
    (11, 'Naledi Molefe',          'naledi.molefe@example.com',     '+27 83 123 4567', 'Kagiso Molefe',           '+27 83 987 6540', '2026-10-20 12:15:00'),
    (12, 'Brandon Adams',          'brandon.adams@example.com',     '+27 84 234 5678', 'Chloe Adams',             '+27 84 876 5430', '2026-10-25 08:50:00');

-- Reset auto increment
ALTER TABLE members AUTO_INCREMENT = 13;

-- ============================================================================
-- 2. INSERT SESSIONS
-- Across all 5 required categories:
--  - Beginner Jogging
--  - Intermediate Running
--  - Advanced Running
--  - Family Fun Runs
--  - Marathon Training
-- Across all 4 statuses: OPEN, FULL, CANCELLED, COMPLETED
-- ============================================================================
INSERT INTO sessions (session_id, session_name, description, category, session_date, start_time, end_time, location, capacity, status, created_at)
VALUES
    -- Past Completed Sessions
    (1,  'Saturday Sunrise 5K', 
         'Gentle scenic morning jog around Green Point Common designed for newcomers and casual joggers.', 
         'Beginner Jogging',      '2026-11-07', '06:30:00', '07:30:00', 'Green Point Park, Cape Town',               15, 'COMPLETED', '2026-10-20 10:00:00'),
    
    (2,  'Midweek Trail Pace 8K', 
         'Structured tempo and pace-building session through wooded trail sections with rolling hills.', 
         'Intermediate Running',  '2026-11-10', '17:30:00', '18:45:00', 'Delta Park, Johannesburg',                  10, 'COMPLETED', '2026-10-22 11:30:00'),
    
    (3,  'Weekend Coastal Tempo 15K', 
         'Fast-paced coastal run along the Atlantic seaboard. Cancelled due to severe weather gale warnings.', 
         'Advanced Running',      '2026-11-12', '06:00:00', '07:30:00', 'Sea Point Promenade, Cape Town',            12, 'CANCELLED', '2026-10-25 09:00:00'),
    
    (4,  'Botanical Gardens Family Trot 4K', 
         'Relaxed, fun family-friendly jog through picturesque gardens with strollers and children welcome.', 
         'Family Fun Runs',       '2026-11-14', '08:00:00', '09:15:00', 'Kirstenbosch Gardens, Cape Town',           20, 'COMPLETED', '2026-10-28 14:00:00'),
    
    (5,  'Endurance Prep 25K', 
         'High-mileage aerobic endurance simulation for registered marathon and ultra-distance athletes.', 
         'Marathon Training',     '2026-11-15', '05:30:00', '08:30:00', 'Camps Bay to Hout Bay Coastal Road',        8,  'COMPLETED', '2026-10-29 08:00:00'),

    -- Upcoming Active Sessions
    (6,  'Fresh Starters Morning Jog', 
         'Encouraging walk/jog intervals with warm-up mobility drills. Ideal for beginners building confidence.', 
         'Beginner Jogging',      '2026-11-21', '07:00:00', '08:00:00', 'Emmarentia Dam, Johannesburg',             15, 'OPEN',      '2026-11-01 09:00:00'),
    
    (7,  'Hill Repeats & Aerobic Power', 
         'Intensive uphill sprint repetitions focused on cadence, cardiovascular capacity, and running economy.', 
         'Intermediate Running',  '2026-11-22', '06:30:00', '07:45:00', 'Signal Hill Road, Cape Town',               4,  'FULL',      '2026-11-02 10:00:00'),
    
    (8,  'High-Cadence Track Intervals 10K', 
         'Structured 800m and 1200m track intervals with recovery laps to sharpen speed and lactate threshold.', 
         'Advanced Running',      '2026-11-25', '18:00:00', '19:30:00', 'Green Point Athletics Stadium',             10, 'OPEN',      '2026-11-05 12:00:00'),
    
    (9,  'Sunday Stroller & Puppy 3K', 
         'Leisurely weekend community run suitable for all ages, family dogs on leashes, and social runners.', 
         'Family Fun Runs',       '2026-11-28', '08:30:00', '09:45:00', 'Walter Sisulu National Botanical Garden',   25, 'OPEN',      '2026-11-06 15:00:00'),
    
    (10, 'Comrades Qualifying Long Run 32K', 
         'Long slow distance (LSD) run with mobile hydration points preparing runners for ultra-marathon qualifying.', 
         'Marathon Training',     '2026-12-05', '05:00:00', '09:00:00', 'Pietermaritzburg Route, KZN',               12, 'OPEN',      '2026-11-08 07:30:00');

-- Reset auto increment
ALTER TABLE sessions AUTO_INCREMENT = 11;

-- ============================================================================
-- 3. INSERT REGISTRATIONS
-- Links members to sessions.
-- Includes:
--  - Confirmed active registrations
--  - Cancelled registrations (preserving history with cancelled_at timestamp)
--  - Reactivated registration (originally cancelled, then re-registered)
-- ============================================================================
INSERT INTO registrations (registration_id, member_id, session_id, status, registered_at, updated_at, cancelled_at)
VALUES
    -- Registrations for Session 1 (Completed, Beginner Jogging)
    (1,  1,  1, 'CONFIRMED', '2026-10-21 09:00:00', '2026-10-21 09:00:00', NULL),
    (2,  2,  1, 'CONFIRMED', '2026-10-21 10:15:00', '2026-10-21 10:15:00', NULL),
    (3,  4,  1, 'CONFIRMED', '2026-10-22 14:20:00', '2026-10-22 14:20:00', NULL),
    (4,  6,  1, 'CONFIRMED', '2026-10-23 11:00:00', '2026-10-23 11:00:00', NULL),

    -- Registrations for Session 2 (Completed, Intermediate Running)
    (5,  3,  2, 'CONFIRMED', '2026-10-23 16:00:00', '2026-10-23 16:00:00', NULL),
    (6,  5,  2, 'CONFIRMED', '2026-10-24 08:30:00', '2026-10-24 08:30:00', NULL),
    (7,  7,  2, 'CONFIRMED', '2026-10-25 12:45:00', '2026-10-25 12:45:00', NULL),

    -- Registrations for Session 3 (Cancelled session - registrations preserved)
    (8,  5,  3, 'CONFIRMED', '2026-10-26 10:00:00', '2026-10-26 10:00:00', NULL),
    (9,  7,  3, 'CONFIRMED', '2026-10-26 14:30:00', '2026-10-26 14:30:00', NULL),
    (10, 10, 3, 'CANCELLED', '2026-10-27 09:15:00', '2026-10-30 16:00:00', '2026-10-30 16:00:00'),

    -- Registrations for Session 4 (Completed, Family Fun Runs)
    (11, 1,  4, 'CONFIRMED', '2026-10-29 09:30:00', '2026-10-29 09:30:00', NULL),
    (12, 4,  4, 'CONFIRMED', '2026-10-29 11:00:00', '2026-10-29 11:00:00', NULL),
    (13, 8,  4, 'CONFIRMED', '2026-10-30 15:45:00', '2026-10-30 15:45:00', NULL),
    (14, 9,  4, 'CONFIRMED', '2026-11-01 10:00:00', '2026-11-01 10:00:00', NULL),
    (15, 11, 4, 'CONFIRMED', '2026-11-02 13:20:00', '2026-11-02 13:20:00', NULL),

    -- Registrations for Session 5 (Completed, Marathon Training)
    (16, 3,  5, 'CONFIRMED', '2026-10-30 08:00:00', '2026-10-30 08:00:00', NULL),
    (17, 7,  5, 'CONFIRMED', '2026-10-31 16:10:00', '2026-10-31 16:10:00', NULL),
    (18, 10, 5, 'CONFIRMED', '2026-11-01 07:45:00', '2026-11-01 07:45:00', NULL),

    -- Registrations for Session 6 (Upcoming OPEN, Beginner Jogging)
    (19, 1,  6, 'CONFIRMED', '2026-11-02 11:00:00', '2026-11-02 11:00:00', NULL),
    -- Member 2: Cancelled on Nov 4, then re-registered (reactivated) on Nov 10
    (20, 2,  6, 'CONFIRMED', '2026-11-02 14:30:00', '2026-11-10 09:00:00', NULL),
    (21, 6,  6, 'CONFIRMED', '2026-11-03 10:15:00', '2026-11-03 10:15:00', NULL),
    (22, 11, 6, 'CONFIRMED', '2026-11-04 16:45:00', '2026-11-04 16:45:00', NULL),

    -- Registrations for Session 7 (Upcoming FULL - Capacity 4, 4 Confirmed Registrations)
    (23, 2,  7, 'CONFIRMED', '2026-11-03 08:00:00', '2026-11-03 08:00:00', NULL),
    (24, 3,  7, 'CONFIRMED', '2026-11-03 09:30:00', '2026-11-03 09:30:00', NULL),
    (25, 5,  7, 'CONFIRMED', '2026-11-04 11:15:00', '2026-11-04 11:15:00', NULL),
    (26, 8,  7, 'CONFIRMED', '2026-11-05 14:00:00', '2026-11-05 14:00:00', NULL),

    -- Registrations for Session 8 (Upcoming OPEN, Advanced Running)
    (27, 3,  8, 'CONFIRMED', '2026-11-06 09:00:00', '2026-11-06 09:00:00', NULL),
    (28, 7,  8, 'CONFIRMED', '2026-11-06 10:30:00', '2026-11-06 10:30:00', NULL),
    (29, 10, 8, 'CONFIRMED', '2026-11-07 13:00:00', '2026-11-07 13:00:00', NULL),
    -- Member 4: Cancelled registration (preserved with status 'CANCELLED')
    (30, 4,  8, 'CANCELLED', '2026-11-07 15:45:00', '2026-11-12 18:20:00', '2026-11-12 18:20:00'),

    -- Registrations for Session 9 (Upcoming OPEN, Family Fun Runs)
    (31, 1,  9, 'CONFIRMED', '2026-11-07 08:15:00', '2026-11-07 08:15:00', NULL),
    (32, 4,  9, 'CONFIRMED', '2026-11-08 11:30:00', '2026-11-08 11:30:00', NULL),
    (33, 6,  9, 'CONFIRMED', '2026-11-09 14:00:00', '2026-11-09 14:00:00', NULL),
    (34, 8,  9, 'CONFIRMED', '2026-11-10 16:45:00', '2026-11-10 16:45:00', NULL),
    (35, 9,  9, 'CONFIRMED', '2026-11-11 10:00:00', '2026-11-11 10:00:00', NULL),

    -- Registrations for Session 10 (Upcoming OPEN, Marathon Training)
    (36, 2,  10, 'CONFIRMED', '2026-11-09 07:00:00', '2026-11-09 07:00:00', NULL),
    (37, 3,  10, 'CONFIRMED', '2026-11-09 08:30:00', '2026-11-09 08:30:00', NULL),
    (38, 5,  10, 'CONFIRMED', '2026-11-10 12:15:00', '2026-11-10 12:15:00', NULL),
    (39, 7,  10, 'CONFIRMED', '2026-11-11 09:45:00', '2026-11-11 09:45:00', NULL),
    (40, 8,  10, 'CONFIRMED', '2026-11-12 14:00:00', '2026-11-12 14:00:00', NULL),
    (41, 10, 10, 'CONFIRMED', '2026-11-13 11:20:00', '2026-11-13 11:20:00', NULL);

-- Reset auto increment
ALTER TABLE registrations AUTO_INCREMENT = 42;

-- ============================================================================
-- 4. INSERT ATTENDANCE RECORDS
-- Note: Attendance is strictly recorded ONLY when marked on the day of the
--       session. Therefore, records exist ONLY for past COMPLETED sessions (1, 2, 4, 5).
-- Upcoming sessions (6, 7, 8, 9, 10) and cancelled sessions (3) have NO attendance.
-- ============================================================================
INSERT INTO attendance_records (attendance_id, registration_id, marked_at, status, notes)
VALUES
    -- Session 1 Attendance (Session Date: 2026-11-07)
    (1,  1,  '2026-11-07 06:35:00', 'PRESENT', 'On time; completed full 5K loop.'),
    (2,  2,  '2026-11-07 06:36:00', 'PRESENT', 'Strong pace; assisted newcomer.'),
    (3,  3,  '2026-11-07 06:45:00', 'LATE',    'Arrived 15 min late; joined warm-up group B.'),
    (4,  4,  '2026-11-07 07:30:00', 'ABSENT',  'No show; did not notify coordinator.'),

    -- Session 2 Attendance (Session Date: 2026-11-10)
    (5,  5,  '2026-11-10 17:32:00', 'PRESENT', 'Completed all 8K hill circuits.'),
    (6,  6,  '2026-11-10 17:33:00', 'PRESENT', 'Paced middle group steadily.'),
    (7,  7,  '2026-11-10 17:35:00', 'PRESENT', 'Completed session; good recovery heart rate.'),

    -- Session 4 Attendance (Session Date: 2026-11-14)
    (8,  11, '2026-11-14 08:05:00', 'PRESENT', 'Ran with family group; completed 4K.'),
    (9,  12, '2026-11-14 08:06:00', 'PRESENT', 'Jogged comfortably with children.'),
    (10, 13, '2026-11-14 08:07:00', 'PRESENT', 'Completed course.'),
    (11, 14, '2026-11-14 08:08:00', 'PRESENT', 'Completed course.'),
    (12, 15, '2026-11-14 09:15:00', 'ABSENT',  'Notified illness that morning.'),

    -- Session 5 Attendance (Session Date: 2026-11-15)
    (13, 16, '2026-11-15 05:32:00', 'PRESENT', 'Completed full 25K distance.'),
    (14, 17, '2026-11-15 05:34:00', 'PRESENT', 'Paced lead group; finished in 2h 10m.'),
    (15, 18, '2026-11-15 05:35:00', 'PRESENT', 'Completed 25K run; assisted with hydration checkpoint.');

-- Reset auto increment
ALTER TABLE attendance_records AUTO_INCREMENT = 16;
