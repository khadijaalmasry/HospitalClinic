-- =============================================================================
-- Hospital & Clinic Management System - Oracle Database Schema
-- Created based on real-world hospital workflow requirements.
-- =============================================================================

-- Drop View if exists
BEGIN
   EXECUTE IMMEDIATE 'DROP VIEW jtxtareafill';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

-- Drop Tables if exist
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE drug_order CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE rad_order CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE lab_order CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE drug CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE rad_test CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE lab_test CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE visit CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE icd_10 CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE doctor CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE patient CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

-- -----------------------------------------------------------------------------
-- 1. PATIENT TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE patient (
    p_id         NUMBER PRIMARY KEY,
    p_name       VARCHAR2(100) NOT NULL,
    p_phonenum   VARCHAR2(20),
    p_birthdate  DATE,
    p_address    VARCHAR2(200),
    gender       VARCHAR2(10)
);

-- -----------------------------------------------------------------------------
-- 2. DOCTOR TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE doctor (
    d_id         NUMBER PRIMARY KEY,
    d_name       VARCHAR2(100) NOT NULL,
    d_phonenum   VARCHAR2(20),
    d_spec       VARCHAR2(100)
);

-- -----------------------------------------------------------------------------
-- 3. ICD_10 DIAGNOSIS CODES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE icd_10 (
    icd_code     VARCHAR2(20) PRIMARY KEY,
    icd_name     VARCHAR2(200) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 4. CLINIC VISIT TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE visit (
    visit_number NUMBER PRIMARY KEY,
    p_id         NUMBER NOT NULL,
    d_id         NUMBER NOT NULL,
    icd_code     VARCHAR2(20) DEFAULT '0000',
    visit_date   TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_visit_patient FOREIGN KEY (p_id) REFERENCES patient(p_id) ON DELETE CASCADE,
    CONSTRAINT fk_visit_doctor FOREIGN KEY (d_id) REFERENCES doctor(d_id) ON DELETE CASCADE,
    CONSTRAINT fk_visit_icd FOREIGN KEY (icd_code) REFERENCES icd_10(icd_code)
);

-- -----------------------------------------------------------------------------
-- 5. LAB TEST CATALOG
-- -----------------------------------------------------------------------------
CREATE TABLE lab_test (
    lt_id        NUMBER PRIMARY KEY,
    lt_name      VARCHAR2(100) NOT NULL,
    lt_cost      NUMBER(10,2) DEFAULT 0.00
);

-- -----------------------------------------------------------------------------
-- 6. LAB ORDERS FOR VISITS
-- -----------------------------------------------------------------------------
CREATE TABLE lab_order (
    lo_id        NUMBER PRIMARY KEY,
    visit_id     NUMBER NOT NULL,
    lt_id        NUMBER NOT NULL,
    CONSTRAINT fk_lo_visit FOREIGN KEY (visit_id) REFERENCES visit(visit_number) ON DELETE CASCADE,
    CONSTRAINT fk_lo_test FOREIGN KEY (lt_id) REFERENCES lab_test(lt_id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 7. RADIOLOGY TEST CATALOG
-- -----------------------------------------------------------------------------
CREATE TABLE rad_test (
    rt_id        NUMBER PRIMARY KEY,
    rt_name      VARCHAR2(100) NOT NULL,
    rt_cost      NUMBER(10,2) DEFAULT 0.00
);

-- -----------------------------------------------------------------------------
-- 8. RADIOLOGY ORDERS FOR VISITS
-- -----------------------------------------------------------------------------
CREATE TABLE rad_order (
    ro_id        NUMBER PRIMARY KEY,
    visit_id     NUMBER NOT NULL,
    rt_id        NUMBER NOT NULL,
    CONSTRAINT fk_ro_visit FOREIGN KEY (visit_id) REFERENCES visit(visit_number) ON DELETE CASCADE,
    CONSTRAINT fk_ro_test FOREIGN KEY (rt_id) REFERENCES rad_test(rt_id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 9. DRUG CATALOG
-- -----------------------------------------------------------------------------
CREATE TABLE drug (
    drug_id      NUMBER PRIMARY KEY,
    drug_name    VARCHAR2(100) NOT NULL,
    drug_cost    NUMBER(10,2) DEFAULT 0.00
);

-- -----------------------------------------------------------------------------
-- 10. DRUG PRESCRIPTION ORDERS
-- -----------------------------------------------------------------------------
CREATE TABLE drug_order (
    do_id        NUMBER PRIMARY KEY,
    visit_id     NUMBER NOT NULL,
    drug_id      NUMBER NOT NULL,
    CONSTRAINT fk_do_visit FOREIGN KEY (visit_id) REFERENCES visit(visit_number) ON DELETE CASCADE,
    CONSTRAINT fk_do_drug FOREIGN KEY (drug_id) REFERENCES drug(drug_id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 11. VIEW FOR SECRETARY VISIT LISTINGS
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW jtxtareafill AS
SELECT 
    v.visit_number, 
    p.p_name, 
    d.d_name, 
    v.visit_date
FROM visit v
JOIN patient p ON v.p_id = p.p_id
JOIN doctor d ON v.d_id = d.d_id;

-- -----------------------------------------------------------------------------
-- SEED DATA
-- -----------------------------------------------------------------------------

-- Default ICD Code required by Application logic for unassigned visits
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('0000', 'DEFAULT');
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('J00', 'Acute Nasopharyngitis (Common Cold)');
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('E11', 'Type 2 Diabetes Mellitus');
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('I10', 'Essential (Primary) Hypertension');
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('J45', 'Asthma');
INSERT INTO icd_10 (icd_code, icd_name) VALUES ('K21.9', 'Gastro-esophageal Reflux Disease without Esophagitis');

-- Sample Doctors
INSERT INTO doctor (d_id, d_name, d_phonenum, d_spec) VALUES (1, 'Dr. Ahmad Masri', '0599001122', 'Cardiology');
INSERT INTO doctor (d_id, d_name, d_phonenum, d_spec) VALUES (2, 'Dr. Sara Hassan', '0599334455', 'Pediatrics');
INSERT INTO doctor (d_id, d_name, d_phonenum, d_spec) VALUES (3, 'Dr. Omar Farooq', '0599667788', 'Internal Medicine');

-- Sample Patients
INSERT INTO patient (p_id, p_name, p_phonenum, p_birthdate, p_address, gender) 
VALUES (1, 'Tariq Khaled', '0598112233', TO_DATE('1992-05-15', 'YYYY-MM-DD'), 'Nablus, Main St', 'Male');
INSERT INTO patient (p_id, p_name, p_phonenum, p_birthdate, p_address, gender) 
VALUES (2, 'Laila Mahmoud', '0598445566', TO_DATE('1998-11-20', 'YYYY-MM-DD'), 'Ramallah, Center', 'Female');

-- Sample Lab Tests
INSERT INTO lab_test (lt_id, lt_name, lt_cost) VALUES (1, 'Complete Blood Count (CBC)', 25.00);
INSERT INTO lab_test (lt_id, lt_name, lt_cost) VALUES (2, 'Fasting Blood Sugar (FBS)', 15.00);
INSERT INTO lab_test (lt_id, lt_name, lt_cost) VALUES (3, 'Lipid Profile', 40.00);

-- Sample Radiology Tests
INSERT INTO rad_test (rt_id, rt_name, rt_cost) VALUES (1, 'Chest X-Ray', 50.00);
INSERT INTO rad_test (rt_id, rt_name, rt_cost) VALUES (2, 'Abdominal Ultrasound', 80.00);
INSERT INTO rad_test (rt_id, rt_name, rt_cost) VALUES (3, 'Brain MRI', 300.00);

-- Sample Medications
INSERT INTO drug (drug_id, drug_name, drug_cost) VALUES (1, 'Amoxicillin 500mg', 12.50);
INSERT INTO drug (drug_id, drug_name, drug_cost) VALUES (2, 'Paracetamol 500mg', 5.00);
INSERT INTO drug (drug_id, drug_name, drug_cost) VALUES (3, 'Metformin 850mg', 18.00);
INSERT INTO drug (drug_id, drug_name, drug_cost) VALUES (4, 'Omeprazole 20mg', 22.00);

COMMIT;
