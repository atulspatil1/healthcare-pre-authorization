-- Insert test data for Members
INSERT IGNORE INTO member (member_number, name, dob, gender, policy_number, policy_status, email, phone, created_at, updated_at, created_by, updated_by)
VALUES 
('MEM-2026-0001', 'Rahul Sharma', '1985-06-15', 'MALE', 'POL-998877', 'ACTIVE', 'rahul.s@example.com', '9876543210', NOW(), NOW(), 'system', 'system'),
('MEM-2026-0002', 'Priya Patel', '1992-11-23', 'FEMALE', 'POL-554433', 'ACTIVE', 'priya.p@example.com', '8765432109', NOW(), NOW(), 'system', 'system'),
('MEM-2026-0003', 'Amit Kumar', '1978-02-10', 'MALE', 'POL-112233', 'EXPIRED', 'amit.k@example.com', '7654321098', NOW(), NOW(), 'system', 'system');
