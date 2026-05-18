-- Run this if phase2_production_readiness.sql was already applied before
-- the SYSTEMCONFIGURATION table was added to that script.

CREATE TABLE systemconfiguration (
    config_key VARCHAR2(100) PRIMARY KEY,
    config_value VARCHAR2(255) NOT NULL,
    description VARCHAR2(255)
);

INSERT INTO systemconfiguration (config_key, config_value, description)
VALUES (
    'purchase.approval.threshold',
    '100000',
    'Purchase orders above this total require manager approval'
);
