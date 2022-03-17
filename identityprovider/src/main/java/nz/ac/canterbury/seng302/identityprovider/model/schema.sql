/*DROP TABLE IF EXISTS AccountProfile CASCADE CONSTRAINTS;
DROP TABLE IF EXISTS AccountLogin;
DROP TABLE IF EXISTS AccountName;
DROP TABLE IF EXISTS Pronouns;
DROP TABLE IF EXISTS Teacher;*/

CREATE TABLE AccountProfile (
    id INTEGER NOT NULL PRIMARY KEY CHECK (id > 0), /*The user's unique identification number*/
    username VARCHAR(10) UNIQUE NOT NULL, /*The user's username*/
    passwordhash VARCHAR(30), /*The user's encrypted password*/
    registerdate DATE NOT NULL CHECK (registerdate > DATE'2022-01-01'), /*The date the user registered their account, must be after 1/1/2022*/
    bio VARCHAR(1024), /*The user's short autobiography with a maximum length of 1MB of text*/
    email VARCHAR(30) UNIQUE NOT NULL, /*The user's email address*/
    photopath VARCHAR(50) /*A path to the user's uploaded profile photo*/
);

CREATE TABLE AccountName (
    registereduser INTEGER NOT NULL PRIMARY KEY,
    firstname CHAR(20) NOT NULL, /*The user's first name*/
    lastname CHAR(20) NOT NULL, /*The user's last name*/
    middleothername CHAR(20), /*The user's middle or other name(s)*/
    nickname CHAR(20), /*The user's preferred alias*/
    FOREIGN KEY (registereduser) REFERENCES AccountProfile
);

CREATE TABLE Pronouns (
    registereduser INTEGER NOT NULL PRIMARY KEY,
    pronoun CHAR(10), /*User's preferred pronouns, a user may have multiple pronouns, e.g him/her*/
    FOREIGN KEY (registereduser) REFERENCES AccountProfile
);

CREATE TABLE Teacher (
    registereduser INTEGER NOT NULL PRIMARY KEY,
    title CHAR(5), /*The user's title, e.g Mr/Mrs*/
    occupation CHAR(10), /*The user's occupation, e.g Lecturer/Tutor*/
    FOREIGN KEY (registereduser) REFERENCES AccountProfile
);