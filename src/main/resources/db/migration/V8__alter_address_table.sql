-- Run this SQL to add country column to addresses table
ALTER TABLE addresses ADD COLUMN country VARCHAR(100) DEFAULT 'India';