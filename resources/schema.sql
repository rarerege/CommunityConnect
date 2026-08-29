CREATE DATABASE IF NOT EXISTS communityconnect;
USE communityconnect;

DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    quantity INT NOT NULL DEFAULT 0,
    description TEXT,
    location VARCHAR(150),
    contact_info VARCHAR(150),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    supplier_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES users(id)
);

CREATE TABLE requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    customer_id INT NOT NULL,
    quantity_requested INT NOT NULL,
    urgency VARCHAR(20) NOT NULL DEFAULT 'medium',
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    notes TEXT,
    admin_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES resources(id),
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

-- Demo accounts (see LoginGUI): all use password "password123" in plaintext,
-- matching UserDAO.authenticate()'s plain string comparison.
INSERT INTO users (username, password, email, full_name, phone, role) VALUES
    ('admin', 'password123', 'admin@communityconnect.org', 'System Admin', '555-0100', 'admin'),
    ('redcross', 'password123', 'contact@redcross.example', 'Red Cross Local', '555-0101', 'supplier'),
    ('foodbank', 'password123', 'contact@foodbank.example', 'Community Food Bank', '555-0102', 'supplier'),
    ('customer1', 'password123', 'customer1@example.com', 'Jane Customer', '555-0103', 'customer');

INSERT INTO categories (name) VALUES
    ('Food'), ('Clothing'), ('Shelter'), ('Medical'), ('Furniture');

INSERT INTO resources (name, category_id, quantity, description, location, contact_info, is_available, supplier_id) VALUES
    ('Canned Goods', 1, 200, 'Assorted canned vegetables and soups', 'Main Warehouse', 'contact@foodbank.example', TRUE, 3),
    ('Winter Coats', 2, 50, 'Adult and children winter coats', 'Donation Center', 'contact@redcross.example', TRUE, 2),
    ('Blankets', 3, 75, 'Emergency blankets', 'Donation Center', 'contact@redcross.example', TRUE, 2),
    ('First Aid Kits', 4, 30, 'Basic first aid supplies', 'Medical Storage', 'contact@redcross.example', TRUE, 2);

INSERT INTO requests (resource_id, customer_id, quantity_requested, urgency, status, notes) VALUES
    (1, 4, 5, 'medium', 'pending', 'Family of four in need of groceries');
