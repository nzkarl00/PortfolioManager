DROP TABLE IF EXISTS Account_Profile CASCADE CONSTRAINTS;
DROP TABLE IF EXISTS AccountProfile CASCADE CONSTRAINTS;
DROP TABLE IF EXISTS Roles;

CREATE TABLE IF NOT EXISTS ACCOUNT_PROFILE (
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT CHECK (id > 0), /*The user's unique identification number*/
    username VARCHAR(30) UNIQUE NOT NULL, /*The user's username*/
    password_hash VARCHAR(60) NOT NULL, /*The user's encrypted password*/
    register_date DATE NOT NULL CHECK (register_date >= DATE'2022-01-01'), /*The date the user registered their account, must be after 1/1/2022*/
    bio VARCHAR(1024), /*The user's short autobiography with a maximum length of 1MB of text*/
    email VARCHAR(30) UNIQUE NOT NULL, /*The user's email address*/
    photo_path VARCHAR(100), /*A path to the user's uploaded profile photo*/
    first_name CHAR(20) NOT NULL, /*The user's first name*/
    last_name CHAR(20) NOT NULL, /*The user's last name*/
    middle_name CHAR(20), /*The user's middle or other name(s)*/
    nickname CHAR(20), /*The user's preferred alias*/
    pronouns CHAR(10) /*User's preferred pronouns, a user may have multiple pronouns, e.g him/her*/
);

CREATE TABLE IF NOT EXISTS Roles (
    user_role_id INTEGER PRIMARY KEY,
    registered_user INTEGER NOT NULL,
    user_role VARCHAR(20), /* Role of the user */
    FOREIGN KEY (registered_user) REFERENCES Account_Profile
);
