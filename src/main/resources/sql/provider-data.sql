-- Insert test data for Providers
INSERT IGNORE INTO provider (provider_code, hospital_name, city, network_status, contact_email, created_at, updated_at)
VALUES 
('PROV-MUM-001', 'Lilavati Hospital', 'Mumbai', 'IN_NETWORK', 'billing@lilavati.com', NOW(), NOW()),
('PROV-PUN-002', 'Ruby Hall Clinic', 'Pune', 'IN_NETWORK', 'insurance@rubyhall.com', NOW(), NOW()),
('PROV-DEL-003', 'Apollo Spectra', 'Delhi', 'OUT_OF_NETWORK', 'claims@apollospectra.com', NOW(), NOW());
