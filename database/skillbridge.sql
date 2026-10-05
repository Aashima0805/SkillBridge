-- ============================================================
-- SkillBridge : Local Job and Daily-Wage Worker Portal
-- Database schema + demo data (MySQL 8 / MariaDB 10.5+)
-- Run:  mysql -u root -p < skillbridge.sql
-- Demo logins are listed in README.md
-- ============================================================
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS skillbridge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET NAMES utf8mb4;
USE skillbridge;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS workers;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(20)  NOT NULL UNIQUE,
  full_name     VARCHAR(60)  NOT NULL,
  email         VARCHAR(100) NOT NULL UNIQUE,
  phone         CHAR(10)     NOT NULL UNIQUE,
  password_hash VARCHAR(60)  NOT NULL,
  role          ENUM('CUSTOMER','WORKER','ADMIN') NOT NULL,
  city          VARCHAR(40)  NOT NULL,
  active        TINYINT(1)   NOT NULL DEFAULT 1,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE skills (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(40)  NOT NULL UNIQUE,
  icon        VARCHAR(16)  NOT NULL DEFAULT '🛠',
  description VARCHAR(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

CREATE TABLE workers (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  user_id          INT NOT NULL UNIQUE,
  skill_id         INT NOT NULL,
  experience_years TINYINT UNSIGNED NOT NULL DEFAULT 0,
  hourly_rate      DECIMAL(8,2) NOT NULL,
  city             VARCHAR(40)  NOT NULL,
  bio              VARCHAR(300) NOT NULL DEFAULT '',
  work_start       TIME NOT NULL DEFAULT '08:00:00',
  work_end         TIME NOT NULL DEFAULT '18:00:00',
  available        TINYINT(1) NOT NULL DEFAULT 1,
  verified         TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_worker_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
  CONSTRAINT fk_worker_skill FOREIGN KEY (skill_id) REFERENCES skills(id),
  INDEX idx_worker_skill (skill_id),
  INDEX idx_worker_city (city)
) ENGINE=InnoDB;

CREATE TABLE bookings (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  customer_id  INT NOT NULL,
  worker_id    INT NOT NULL,
  booking_date DATE NOT NULL,
  start_time   TIME NOT NULL,
  end_time     TIME NOT NULL,
  hours        TINYINT UNSIGNED NOT NULL,
  address      VARCHAR(200) NOT NULL,
  notes        VARCHAR(200) NOT NULL DEFAULT '',
  hourly_rate  DECIMAL(8,2)  NOT NULL,
  subtotal     DECIMAL(10,2) NOT NULL,
  platform_fee DECIMAL(10,2) NOT NULL,
  total        DECIMAL(10,2) NOT NULL,
  status       ENUM('PENDING','ACCEPTED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_booking_worker   FOREIGN KEY (worker_id)   REFERENCES workers(id),
  INDEX idx_booking_worker_date (worker_id, booking_date),
  INDEX idx_booking_customer (customer_id)
) ENGINE=InnoDB;

CREATE TABLE payments (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL UNIQUE,
  amount   DECIMAL(10,2) NOT NULL,
  method   ENUM('UPI','CARD') NOT NULL,
  detail   VARCHAR(60) NOT NULL DEFAULT '',
  txn_ref  VARCHAR(30) NOT NULL UNIQUE,
  status   ENUM('SUCCESS','REFUNDED') NOT NULL DEFAULT 'SUCCESS',
  paid_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reviews (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  booking_id  INT NOT NULL UNIQUE,
  customer_id INT NOT NULL,
  worker_id   INT NOT NULL,
  rating      TINYINT UNSIGNED NOT NULL,
  comment     VARCHAR(300) NOT NULL DEFAULT '',
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_review_booking  FOREIGN KEY (booking_id)  REFERENCES bookings(id) ON DELETE CASCADE,
  CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_review_worker   FOREIGN KEY (worker_id)   REFERENCES workers(id),
  CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
  INDEX idx_review_worker (worker_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Demo data
-- ------------------------------------------------------------

INSERT INTO skills (id, name, icon, description) VALUES
  (1, 'Electrician', '⚡', 'Wiring, fittings, fan and light installation, fault finding and repairs.'),
  (2, 'Plumber', '🔧', 'Leak repair, pipe fitting, tap and bathroom fixtures, water tank work.'),
  (3, 'Carpenter', '🪚', 'Furniture repair, doors and windows, custom woodwork and fittings.'),
  (4, 'Painter', '🎨', 'Interior and exterior painting, wall putty, polish and finishing.'),
  (5, 'Helper', '🧰', 'General helpers for shifting, cleaning, loading and site assistance.');

-- Passwords: admin = Admin@123 | customers = Customer@123 | workers = Worker@123 (BCrypt hashed)
INSERT INTO users (id, username, full_name, email, phone, password_hash, role, city, active) VALUES
  (1, 'admin', 'SkillBridge Admin', 'admin@skillbridge.example', '9000000001', '$2a$10$m7DifKjGCMjmSZF8jrqDVO7T4oLlnvdr6bqZwNBh7WiPDnWBt5uF2', 'ADMIN', 'Hyderabad', 1),
  (2, 'customer1', 'Ananya Rao', 'ananya.rao@example.com', '9000000002', '$2a$10$g.yKcdNXfnp/qQ.85bX6dunW193PfKhw..s4d9RcxNKZuFiQxxjTW', 'CUSTOMER', 'Hyderabad', 1),
  (3, 'customer2', 'Vikram Singh', 'vikram.singh@example.com', '9000000003', '$2a$10$g.yKcdNXfnp/qQ.85bX6dunW193PfKhw..s4d9RcxNKZuFiQxxjTW', 'CUSTOMER', 'Bengaluru', 1),
  (4, 'customer3', 'Meera Iyer', 'meera.iyer@example.com', '9000000004', '$2a$10$g.yKcdNXfnp/qQ.85bX6dunW193PfKhw..s4d9RcxNKZuFiQxxjTW', 'CUSTOMER', 'Chennai', 1),
  (5, 'customer4', 'Rohit Verma', 'rohit.verma@example.com', '9000000005', '$2a$10$g.yKcdNXfnp/qQ.85bX6dunW193PfKhw..s4d9RcxNKZuFiQxxjTW', 'CUSTOMER', 'Delhi', 1),
  (6, 'ramesh_k', 'Ramesh Kumar', 'ramesh.kumar@example.com', '9100000001', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Hyderabad', 1),
  (7, 'imran_s', 'Imran Sheikh', 'imran.sheikh@example.com', '9100000002', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Bengaluru', 1),
  (8, 'suresh_b', 'Suresh Babu', 'suresh.babu@example.com', '9100000003', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Chennai', 1),
  (9, 'mahesh_y', 'Mahesh Yadav', 'mahesh.yadav@example.com', '9100000004', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Hyderabad', 1),
  (10, 'anil_t', 'Anil Thomas', 'anil.thomas@example.com', '9100000005', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Bengaluru', 1),
  (11, 'deepak_s', 'Deepak Sharma', 'deepak.sharma@example.com', '9100000006', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Delhi', 1),
  (12, 'gopal_d', 'Gopal Das', 'gopal.das@example.com', '9100000007', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Kolkata', 1),
  (13, 'salim_a', 'Salim Ansari', 'salim.ansari@example.com', '9100000008', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Hyderabad', 1),
  (14, 'ravi_t', 'Ravi Teja', 'ravi.teja@example.com', '9100000009', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Chennai', 1),
  (15, 'kiran_p', 'Kiran Patil', 'kiran.patil@example.com', '9100000010', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Pune', 1),
  (16, 'naseer_a', 'Naseer Ahmed', 'naseer.ahmed@example.com', '9100000011', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Bengaluru', 1),
  (17, 'lakshmi_d', 'Lakshmi Devi', 'lakshmi.devi@example.com', '9100000012', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Hyderabad', 1),
  (18, 'bhaskar_r', 'Bhaskar Rao', 'bhaskar.rao@example.com', '9100000013', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Hyderabad', 1),
  (19, 'sunita_k', 'Sunita Kumari', 'sunita.kumari@example.com', '9100000014', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Delhi', 1),
  (20, 'pooja_n', 'Pooja Nair', 'pooja.nair@example.com', '9100000015', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Bengaluru', 1),
  (21, 'tarun_m', 'Tarun Mehta', 'tarun.mehta@example.com', '9100000016', '$2a$10$uIM2nL6wWpolH5XvneL0B.b0ks39ejBqcjliK7.cF/.AuMkldHARK', 'WORKER', 'Pune', 1);

INSERT INTO workers (id, user_id, skill_id, experience_years, hourly_rate, city, bio, work_start, work_end, available, verified) VALUES
  (1, 6, 1, 8, 450.00, 'Hyderabad', 'Licensed house electrician. Wiring, MCB boards, inverter and fan work done neatly and safely.', '08:00:00', '18:00:00', 1, 1),
  (2, 7, 1, 5, 400.00, 'Bengaluru', 'Apartment wiring and appliance point specialist. Quick fault finding, same-day service.', '09:00:00', '19:00:00', 1, 1),
  (3, 8, 1, 12, 550.00, 'Chennai', '12 years in industrial and home electrical work. Panel upgrades and earthing checks.', '08:00:00', '17:00:00', 1, 1),
  (4, 9, 2, 7, 350.00, 'Hyderabad', 'Leak detection, bathroom fittings and pipeline repair. Clean work, no mess left behind.', '08:00:00', '18:00:00', 1, 1),
  (5, 10, 2, 10, 500.00, 'Bengaluru', 'Complete plumbing for homes and small offices, including water tank and motor fitting.', '09:00:00', '18:00:00', 1, 1),
  (6, 11, 2, 4, 300.00, 'Delhi', 'Tap, flush and drainage fixes. Reliable for quick repairs and maintenance visits.', '07:00:00', '16:00:00', 1, 1),
  (7, 12, 3, 15, 600.00, 'Kolkata', 'Master carpenter for custom furniture, wardrobes and door frames. Fine finishing.', '09:00:00', '18:00:00', 1, 1),
  (8, 13, 3, 6, 420.00, 'Hyderabad', 'Furniture repair, modular fittings and window work. Punctual and tidy.', '08:00:00', '17:00:00', 1, 1),
  (9, 14, 3, 9, 480.00, 'Chennai', 'Woodwork for kitchens and wardrobes, polishing and hinge and lock replacement.', '09:00:00', '19:00:00', 1, 1),
  (10, 15, 4, 9, 380.00, 'Pune', 'Interior and exterior painting with smooth putty work and clean edges.', '08:00:00', '17:00:00', 1, 1),
  (11, 16, 4, 11, 450.00, 'Bengaluru', 'Texture painting, waterproofing coats and full home repainting teams.', '08:00:00', '18:00:00', 1, 1),
  (12, 17, 4, 6, 350.00, 'Hyderabad', 'Careful room painting and touch-ups. Furniture is covered and floors protected.', '09:00:00', '17:00:00', 1, 1),
  (13, 18, 5, 3, 200.00, 'Hyderabad', 'Shifting, loading and cleaning help. Hard-working and on time.', '07:00:00', '17:00:00', 1, 1),
  (14, 19, 5, 5, 220.00, 'Delhi', 'Home cleaning and general assistance for events and site work.', '08:00:00', '18:00:00', 1, 1),
  (15, 20, 5, 4, 230.00, 'Bengaluru', 'Deep cleaning, organising and general household help.', '08:00:00', '16:00:00', 1, 1),
  (16, 21, 1, 3, 380.00, 'Pune', 'New on SkillBridge: residential wiring and light installation. Verification pending.', '09:00:00', '18:00:00', 1, 0);

INSERT INTO bookings (id, customer_id, worker_id, booking_date, start_time, end_time, hours, address, notes, hourly_rate, subtotal, platform_fee, total, status, created_at) VALUES
  (1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -20 DAY), '09:00:00', '12:00:00', 3, 'Plot 21, Road No. 5, Banjara Hills', '', 450.00, 1350.00, 67.50, 1417.50, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -22 DAY)),
  (2, 3, 1, DATE_ADD(CURDATE(), INTERVAL -14 DAY), '10:00:00', '12:00:00', 2, 'Flat 304, Green Residency, Koramangala', '', 450.00, 900.00, 45.00, 945.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -16 DAY)),
  (3, 4, 2, DATE_ADD(CURDATE(), INTERVAL -12 DAY), '11:00:00', '13:00:00', 2, '12/4, Lake View Apartments, Adyar', '', 400.00, 800.00, 40.00, 840.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -14 DAY)),
  (4, 5, 3, DATE_ADD(CURDATE(), INTERVAL -9 DAY), '09:00:00', '13:00:00', 4, 'B-17, Sector 9, Rohini', '', 550.00, 2200.00, 110.00, 2310.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -11 DAY)),
  (5, 2, 4, DATE_ADD(CURDATE(), INTERVAL -18 DAY), '09:00:00', '11:00:00', 2, 'Plot 21, Road No. 5, Banjara Hills', '', 350.00, 700.00, 35.00, 735.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -20 DAY)),
  (6, 3, 5, DATE_ADD(CURDATE(), INTERVAL -11 DAY), '10:00:00', '13:00:00', 3, 'Flat 304, Green Residency, Koramangala', '', 500.00, 1500.00, 75.00, 1575.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -13 DAY)),
  (7, 5, 6, DATE_ADD(CURDATE(), INTERVAL -8 DAY), '08:00:00', '10:00:00', 2, 'B-17, Sector 9, Rohini', '', 300.00, 600.00, 30.00, 630.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -10 DAY)),
  (8, 2, 7, DATE_ADD(CURDATE(), INTERVAL -16 DAY), '10:00:00', '15:00:00', 5, 'Plot 21, Road No. 5, Banjara Hills', '', 600.00, 3000.00, 150.00, 3150.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -18 DAY)),
  (9, 4, 8, DATE_ADD(CURDATE(), INTERVAL -10 DAY), '09:00:00', '12:00:00', 3, '12/4, Lake View Apartments, Adyar', '', 420.00, 1260.00, 63.00, 1323.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -12 DAY)),
  (10, 3, 9, DATE_ADD(CURDATE(), INTERVAL -7 DAY), '10:00:00', '14:00:00', 4, 'Flat 304, Green Residency, Koramangala', '', 480.00, 1920.00, 96.00, 2016.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -9 DAY)),
  (11, 2, 10, DATE_ADD(CURDATE(), INTERVAL -15 DAY), '09:00:00', '15:00:00', 6, 'Plot 21, Road No. 5, Banjara Hills', '', 380.00, 2280.00, 114.00, 2394.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -17 DAY)),
  (12, 4, 11, DATE_ADD(CURDATE(), INTERVAL -13 DAY), '09:00:00', '15:00:00', 6, '12/4, Lake View Apartments, Adyar', '', 450.00, 2700.00, 135.00, 2835.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -15 DAY)),
  (13, 5, 12, DATE_ADD(CURDATE(), INTERVAL -6 DAY), '10:00:00', '14:00:00', 4, 'B-17, Sector 9, Rohini', '', 350.00, 1400.00, 70.00, 1470.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -8 DAY)),
  (14, 2, 13, DATE_ADD(CURDATE(), INTERVAL -5 DAY), '08:00:00', '12:00:00', 4, 'Plot 21, Road No. 5, Banjara Hills', '', 200.00, 800.00, 40.00, 840.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -7 DAY)),
  (15, 3, 14, DATE_ADD(CURDATE(), INTERVAL -4 DAY), '09:00:00', '12:00:00', 3, 'Flat 304, Green Residency, Koramangala', '', 220.00, 660.00, 33.00, 693.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -6 DAY)),
  (16, 4, 15, DATE_ADD(CURDATE(), INTERVAL -3 DAY), '09:00:00', '11:00:00', 2, '12/4, Lake View Apartments, Adyar', '', 230.00, 460.00, 23.00, 483.00, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -5 DAY)),
  (17, 2, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', '16:00:00', 2, 'Plot 21, Road No. 5, Banjara Hills', '', 450.00, 900.00, 45.00, 945.00, 'PENDING', DATE_ADD(NOW(), INTERVAL 0 DAY)),
  (18, 2, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00', '13:00:00', 3, 'Plot 21, Road No. 5, Banjara Hills', '', 350.00, 1050.00, 52.50, 1102.50, 'ACCEPTED', DATE_ADD(NOW(), INTERVAL 1 DAY)),
  (19, 3, 10, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '09:00:00', '13:00:00', 4, 'Flat 304, Green Residency, Koramangala', '', 380.00, 1520.00, 76.00, 1596.00, 'PENDING', DATE_ADD(NOW(), INTERVAL 3 DAY)),
  (20, 2, 7, DATE_ADD(CURDATE(), INTERVAL -2 DAY), '11:00:00', '13:00:00', 2, 'Plot 21, Road No. 5, Banjara Hills', '', 600.00, 1200.00, 60.00, 1260.00, 'CANCELLED', DATE_ADD(NOW(), INTERVAL -4 DAY));

INSERT INTO payments (id, booking_id, amount, method, detail, txn_ref, status, paid_at) VALUES
  (1, 1, 1417.50, 'UPI', 'UPI: user2@okaxis', 'SB20260901', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -22 DAY)),
  (2, 2, 945.00, 'CARD', 'Card ****4242', 'SB20260902', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -16 DAY)),
  (3, 3, 840.00, 'UPI', 'UPI: user4@okaxis', 'SB20260903', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -14 DAY)),
  (4, 4, 2310.00, 'CARD', 'Card ****4242', 'SB20260904', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -11 DAY)),
  (5, 5, 735.00, 'UPI', 'UPI: user2@okaxis', 'SB20260905', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -20 DAY)),
  (6, 6, 1575.00, 'CARD', 'Card ****4242', 'SB20260906', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -13 DAY)),
  (7, 7, 630.00, 'UPI', 'UPI: user5@okaxis', 'SB20260907', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -10 DAY)),
  (8, 8, 3150.00, 'CARD', 'Card ****4242', 'SB20260908', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -18 DAY)),
  (9, 9, 1323.00, 'UPI', 'UPI: user4@okaxis', 'SB20260909', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -12 DAY)),
  (10, 10, 2016.00, 'CARD', 'Card ****4242', 'SB20260910', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -9 DAY)),
  (11, 11, 2394.00, 'UPI', 'UPI: user2@okaxis', 'SB20260911', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -17 DAY)),
  (12, 12, 2835.00, 'CARD', 'Card ****4242', 'SB20260912', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -15 DAY)),
  (13, 13, 1470.00, 'UPI', 'UPI: user5@okaxis', 'SB20260913', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -8 DAY)),
  (14, 14, 840.00, 'CARD', 'Card ****4242', 'SB20260914', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -7 DAY)),
  (15, 15, 693.00, 'UPI', 'UPI: user3@okaxis', 'SB20260915', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -6 DAY)),
  (16, 16, 483.00, 'CARD', 'Card ****4242', 'SB20260916', 'SUCCESS', DATE_ADD(NOW(), INTERVAL -5 DAY)),
  (17, 17, 945.00, 'UPI', 'UPI: user2@okaxis', 'SB20260917', 'SUCCESS', DATE_ADD(NOW(), INTERVAL 0 DAY)),
  (18, 18, 1102.50, 'CARD', 'Card ****4242', 'SB20260918', 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY)),
  (19, 19, 1596.00, 'UPI', 'UPI: user3@okaxis', 'SB20260919', 'SUCCESS', DATE_ADD(NOW(), INTERVAL 3 DAY)),
  (20, 20, 1260.00, 'CARD', 'Card ****4242', 'SB20260920', 'REFUNDED', DATE_ADD(NOW(), INTERVAL -4 DAY));

INSERT INTO reviews (id, booking_id, customer_id, worker_id, rating, comment, created_at) VALUES
  (1, 1, 2, 1, 5, 'Ramesh fixed our wiring problem in one visit. Very neat and professional.', DATE_ADD(NOW(), INTERVAL -19 DAY)),
  (2, 2, 3, 1, 5, 'On time and explained the fault clearly. Will book again.', DATE_ADD(NOW(), INTERVAL -13 DAY)),
  (3, 3, 4, 2, 4, 'Good work on the fan and light points.', DATE_ADD(NOW(), INTERVAL -11 DAY)),
  (4, 4, 5, 3, 5, 'Excellent panel upgrade. Very experienced.', DATE_ADD(NOW(), INTERVAL -8 DAY)),
  (5, 5, 2, 4, 5, 'Fixed a leaking pipe quickly and cleaned up after.', DATE_ADD(NOW(), INTERVAL -17 DAY)),
  (6, 6, 3, 5, 4, 'Solid plumbing work, slightly late but did a great job.', DATE_ADD(NOW(), INTERVAL -10 DAY)),
  (7, 7, 5, 6, 4, 'Tap and flush fixed at a fair price.', DATE_ADD(NOW(), INTERVAL -7 DAY)),
  (8, 8, 2, 7, 5, 'Beautiful custom wardrobe. Real craftsmanship.', DATE_ADD(NOW(), INTERVAL -15 DAY)),
  (9, 9, 4, 8, 4, 'Repaired our old dining table very well.', DATE_ADD(NOW(), INTERVAL -9 DAY)),
  (10, 10, 3, 9, 5, 'Kitchen shelves came out perfectly.', DATE_ADD(NOW(), INTERVAL -6 DAY)),
  (11, 11, 2, 10, 4, 'Clean edges and a good finish on all rooms.', DATE_ADD(NOW(), INTERVAL -14 DAY)),
  (12, 12, 4, 11, 5, 'Repainted our whole flat. Very professional team.', DATE_ADD(NOW(), INTERVAL -12 DAY)),
  (13, 13, 5, 12, 3, 'Decent job, took a bit longer than expected.', DATE_ADD(NOW(), INTERVAL -5 DAY)),
  (14, 14, 2, 13, 4, 'Helped shift furniture quickly and carefully.', DATE_ADD(NOW(), INTERVAL -4 DAY)),
  (15, 15, 3, 14, 5, 'Great deep cleaning. Highly recommended.', DATE_ADD(NOW(), INTERVAL -3 DAY)),
  (16, 16, 4, 15, 4, 'Polite and hard-working.', DATE_ADD(NOW(), INTERVAL -2 DAY));
