CREATE TYPE E_METHODS AS ENUM ('GET', 'POST', 'PUT', 'PATCH', 'DELETE');
CREATE TYPE E_PERMISSIONS AS ENUM ('VIEW', 'EDIT', 'DELETE', 'APPROVE');
CREATE TABLE tbl_activities (
	id SERIAL,
    method E_METHODS,
	url VARCHAR(1024) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE tbl_permissions (
	id SERIAL,
	name E_PERMISSIONS,
    enabled BOOLEAN,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
);

CREATE TABLE tbl_roles (
	id SERIAL,
	name VARCHAR(255) NOT NULL,
    enabled BOOLEAN,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (name),
	PRIMARY KEY(id)
);

CREATE TABLE tbl_role_permission_activities (
	role_id INT NOT NULL,
	permission_id INT NOT NULL,
	activity_id INT NOT NULL,
	PRIMARY KEY(role_id, permission_id, activity_id),
	CONSTRAINT tbl_role_permission_activities_fk1 FOREIGN KEY (role_id) REFERENCES tbl_roles(id),
	CONSTRAINT tbl_role_permission_activities_fk2 FOREIGN KEY (permission_id) REFERENCES tbl_permissions(id),
	CONSTRAINT tbl_role_permission_activities_fk3 FOREIGN KEY (activity_id) REFERENCES tbl_activities(id)
);

CREATE TABLE tbl_users (
	id SERIAL,
	email VARCHAR(50) NOT NULL,
	username VARCHAR(50) NOT NULL,
	password VARCHAR(255) NOT NULL,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE(username),
	PRIMARY KEY(id)
);

CREATE TABLE tbl_user_roles (
	user_id INT NOT NULL,
	role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
	CONSTRAINT tbl_user_role_fk1 FOREIGN KEY (user_id) REFERENCES tbl_users(id),
	CONSTRAINT tbl_user_role_fk2 FOREIGN KEY (role_id) REFERENCES tbl_roles(id)
);

INSERT INTO tbl_activities (id, method,  url) values
(1, 'GET', '/api/v1/users'),
(2, 'GET', '/api/v1/users/{id}'),
(3, 'POST', '/api/v1/users'),
(4, 'PUT', '/api/v1/users/{id}'),
(5, 'PATCH', '/api/v1/users/{id}'),
(6, 'DELETE', '/api/v1/users/{id}');

INSERT INTO tbl_permissions (id, name, enabled) VALUES
(1, 'VIEW', true),
(2, 'EDIT', true),
(3, 'DELETE', true),
(4, 'APPROVE', true);

INSERT INTO tbl_roles (id, name, enabled) VALUES
(1, 'SYSTEM_ADMIN', true),
(2, 'ROLE_ADMIN', true),
(3, 'ROLE_MANAGER', true),
(4, 'ROLE_USER', true);

INSERT INTO tbl_role_permission_activities (role_id, permission_id, activity_id) VALUES
(1, 1, 1),
(1, 1, 2),
(1, 2, 3),
(1, 2, 4),
(1, 2, 5),
(1, 3, 6),
(4, 2, 3);

INSERT INTO tbl_users (email, username, password) VALUES
('sysadmin@email.com', 'sysadmin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
('admin@email.com', 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
('manager@email.com', 'manager', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
('taylq@beetsoft.com.vn', 'user', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');

INSERT INTO tbl_user_roles (user_id, role_id) VALUES (1, 1);


-- Business Logic Database
CREATE TABLE tbl_inventory
(
    id SERIAL,
    sku_code VARCHAR(255) NOT NULL,
    quantity INTEGER,
    PRIMARY KEY (id)
);
INSERT INTO tbl_inventory (sku_code, quantity) VALUES('Iphone_13', 0),('Iphone_14', 10);