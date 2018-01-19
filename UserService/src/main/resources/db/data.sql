INSERT INTO authority(name,value) VALUES ('Create Admin User', 100);
INSERT INTO authority(name,value) VALUES ('Create Normal User', 101);

INSERT INTO role(name) VALUES ('root user');
INSERT INTO role(name) VALUES ('admin user');
INSERT INTO role(name) VALUES ('normal user');

INSERT INTO user(name, password, state, created_by,created_date) VALUES ('root', '$2a$16$/04ho7yVxNMe52I2NvPM7OqTSXEH1z.MBQFFza60f69OfGl42OdV.', 2, 'ROOT', NOW());

INSERT INTO user_roles(user_id, roles_id) VALUES(1, 1);

INSERT INTO role_authorities(role_id, authorities_id) VALUES(1, 1);
INSERT INTO role_authorities(role_id, authorities_id) VALUES(1, 2);