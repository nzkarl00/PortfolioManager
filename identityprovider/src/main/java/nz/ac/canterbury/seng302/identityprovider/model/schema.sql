CREATE TABLE IF NOT EXISTS Account_Profile (
    id INTEGER NOT NULL PRIMARY KEY CHECK AUTO INCREMENT(id > 0), /*The user's unique identification number*/
    username VARCHAR(30) UNIQUE NOT NULL, /*The user's username*/
    password_hash VARCHAR(30) NOT NULL, /*The user's encrypted password*/
    register_date DATE NOT NULL CHECK (register_date >= DATE'2022-01-01'), /*The date the user registered their account, must be after 1/1/2022*/
    bio VARCHAR(1024), /*The user's short autobiography with a maximum length of 2MB of text*/
    email VARCHAR(30) UNIQUE NOT NULL, /*The user's email address*/
    photo_path VARCHAR(100) /*A path to the user's uploaded profile photo*/
);

CREATE TABLE IF NOT EXISTS Account_Name (
                                            registered_user INTEGER NOT NULL PRIMARY KEY,
                                            first_name CHAR(20) NOT NULL, /*The user's first name*/
    last_name CHAR(20) NOT NULL, /*The user's last name*/
    middle_other_name CHAR(20), /*The user's middle or other name(s)*/
    nickname CHAR(20), /*The user's preferred alias*/
    FOREIGN KEY (registered_user) REFERENCES Account_Profile
    );

CREATE TABLE IF NOT EXISTS Pronouns (
                                        registered_user INTEGER NOT NULL PRIMARY KEY,
                                        pronoun CHAR(10), /*User's preferred pronouns, a user may have multiple pronouns, e.g him/her*/
    FOREIGN KEY (registered_user) REFERENCES Account_Profile
    );

CREATE TABLE IF NOT EXISTS Teacher (
                                       registered_user INTEGER NOT NULL PRIMARY KEY,
                                       title CHAR(5), /*The user's title, e.g Mr/Mrs*/
    occupation CHAR(10), /*The user's occupation, e.g Lecturer/Tutor*/
    FOREIGN KEY (registered_user) REFERENCES Account_Profile
    );
