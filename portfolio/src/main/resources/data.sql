DROP TABLE IF EXISTS PROJECT ;
CREATE TABLE PROJECT (
PRODUCT_ID INT PRIMARY KEY,
DESCRIPTION VARCHAR(255),
START_DATE DATE,
END_DATE DATE
);

SELECT * FROM PROJECT ORDER BY PRODUCT_ID;

DROP TABLE IF EXISTS SPRINT ;
CREATE TABLE SPRINT (
ID INT PRIMARY KEY,
-- PRODUCT_ID INT,
-- FOREIGN KEY (PRODUCT_ID) REFERENCES PROJECT(PRODUCT_ID),
NAME VARCHAR(50),
DESCRIPTION VARCHAR(255),
-- START_DATE DATE,
-- END_DATE DATE
);

SELECT * FROM SPRINT ORDER BY LABEL;