CREATE TABLE ROLES(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME NVARCHAR(255) UNIQUE NOT NULL,
    DESCRIPTION NVARCHAR(255) DEFAULT NULL
) ENGINE=INNODB;