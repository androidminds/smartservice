
INSERT INTO role(name) VALUES ('administrator');
INSERT INTO role(name) VALUES ('user');

INSERT INTO user(name, password) VALUES ('admin', '123456');
INSERT INTO user(name, password) VALUES ('foo', '123456');


INSERT INTO authority(name, value) VALUES('root', '1111');
INSERT INTO authority(name, value) VALUES('access', '2222');