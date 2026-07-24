-- =========================================================
--  Library Management System - Database Schema Setup Script
--  Run this once to initialize the database.
-- =========================================================

CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- --------------------------------------------------------
--  Admin Table (legacy console login)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin (
    admin_id   INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default admin account (username: admin, password: admin)
INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'admin');

-- --------------------------------------------------------
--  Users Table (GUI operator accounts)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(50)  NOT NULL DEFAULT 'Librarian'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default system accounts
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'Admin');

-- --------------------------------------------------------
--  Books Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    book_id   INT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author    VARCHAR(255) NOT NULL,
    category  VARCHAR(100) NOT NULL,
    quantity  INT          NOT NULL DEFAULT 0,
    price     DECIMAL(10, 2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
--  Students Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id  INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    department  VARCHAR(100) NOT NULL,
    phone       VARCHAR(20)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
--  Issue Books Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS issue_books (
    issue_id    INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT          NOT NULL,
    student_id  INT          NOT NULL,
    issue_date  DATE         NOT NULL,
    return_date DATE,
    status      VARCHAR(50)  NOT NULL DEFAULT 'Issued',
    fine        DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_issue_book    FOREIGN KEY (book_id)    REFERENCES books(book_id)    ON DELETE CASCADE,
    CONSTRAINT fk_issue_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
--  Reservations Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id       INT          NOT NULL,
    book_id          INT          NOT NULL,
    reservation_date DATE         NOT NULL,
    status           VARCHAR(50)  NOT NULL DEFAULT 'Waiting',
    CONSTRAINT fk_res_book    FOREIGN KEY (book_id)    REFERENCES books(book_id)       ON DELETE CASCADE,
    CONSTRAINT fk_res_student FOREIGN KEY (student_id) REFERENCES students(student_id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
--  Purchases Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchases (
    purchase_id    INT AUTO_INCREMENT PRIMARY KEY,
    book_id        INT             NOT NULL,
    supplier_name  VARCHAR(255)    NOT NULL,
    quantity       INT             NOT NULL,
    price_per_book DECIMAL(10, 2)  NOT NULL,
    total_amount   DECIMAL(10, 2)  NOT NULL,
    purchase_date  DATE            NOT NULL,
    CONSTRAINT fk_purch_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
--  Notifications Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    notification_id   INT AUTO_INCREMENT PRIMARY KEY,
    message           TEXT        NOT NULL,
    notification_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status            VARCHAR(50) NOT NULL DEFAULT 'Unread'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
