CREATE TABLE IF NOT EXISTS exp_members (
  member_id int(10) unsigned NOT NULL AUTO_INCREMENT,
  group_id smallint(4) NOT NULL DEFAULT '0',
  username varchar(50) NOT NULL,
  screen_name varchar(50) NOT NULL,
  password varchar(128) NOT NULL,
  salt varchar(128) NOT NULL DEFAULT '',
  unique_id varchar(40) NOT NULL,
  crypt_key varchar(40) DEFAULT NULL,
  authcode varchar(10) DEFAULT NULL,
  email varchar(72) NOT NULL,
  url varchar(150) DEFAULT NULL,
  pmember_id int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (member_id),
  KEY group_id (group_id),
  KEY unique_id (unique_id),
  KEY password (password)
);

INSERT INTO exp_members (member_id, group_id, username, screen_name, password, salt, unique_id, crypt_key, authcode, email, url) VALUES
(1, 6, 'sonrisa', 'Sonrisa', 'be7092276163473fc88a6e37f6923c2865a1e4985884f368d1de7d9bd3aaa76c11fecd55aa9aac4f2c2b097f84902d76ff18df2d8c8d0d2ecd6a18e037a2b1a9', 'B9]$uH_@#+''idmB`FBGWwa6-;-8,tgwBcpyi<AA#k}Vd^=j-&1:oSug]wcJ.@=*Qdy"=B^4$`LDJ7~-U?`A:Zhg$q#EdEQ:*#t8.}9gJXvY/`-0e.dL|kE@6x!dqXr[l', '3f0c1264c9c433b0cca18a36da4de7eb15e7326a', '0086c3a95c119e8b0ca138c3488af9a89b08ea09', NULL, 'nandor.miklos@sonrisainc.com', ''),
(2, 1, 'admin', 'Sonrisa Admin', 'be7092276163473fc88a6e37f6923c2865a1e4985884f368d1de7d9bd3aaa76c11fecd55aa9aac4f2c2b097f84902d76ff18df2d8c8d0d2ecd6a18e037a2b1a9', 'B9]$uH_@#+''idmB`FBGWwa6-;-8,tgwBcpyi<AA#k}Vd^=j-&1:oSug]wcJ.@=*Qdy"=B^4$`LDJ7~-U?`A:Zhg$q#EdEQ:*#t8.}9gJXvY/`-0e.dL|kE@6x!dqXr[l', '3f0c1264c9c433b0cca18a36da4de7eb15e7326a', '0086c3a95c119e8b0ca138c3488af9a89b08ea09', NULL, 'nandor.miklos@sonrisainc.com', '');


CREATE TABLE IF NOT EXISTS exp_member_groups (
  group_id smallint(4) unsigned NOT NULL,
  site_id int(4) unsigned NOT NULL DEFAULT '1',
  group_title varchar(100) NOT NULL,
  group_description text NOT NULL,
  is_locked char(1) NOT NULL DEFAULT 'y',
  can_access_members char(1) NOT NULL DEFAULT 'n',
  can_access_admin char(1) NOT NULL DEFAULT 'n',
  can_access_sys_prefs char(1) NOT NULL DEFAULT 'n',
  can_admin_members char(1) NOT NULL DEFAULT 'n',
  can_delete_members char(1) NOT NULL DEFAULT 'n',
  PRIMARY KEY (group_id,site_id)
);

INSERT INTO exp_member_groups (group_id, site_id, group_title, group_description, is_locked, can_access_admin, can_access_sys_prefs, can_admin_members, can_delete_members) VALUES
(1, 1, 'Super Admins', '', 'y', 'y', 'y', 'y', 'y'),
(2, 1, 'Banned', '', 'y', 'n', 'n', 'n', 'n'),
(3, 1, 'Guests', '', 'y', 'n', 'n', 'n', 'n'),
(4, 1, 'Pending', '', 'y', 'n', 'n', 'n', 'n'),
(6, 1, 'Enterprise Retailers', 'Access to all Swarm features and services', 'y', 'n', 'n', 'n', 'n'),
(7, 1, 'Managers', 'For users who have access to more than one store.', 'y', 'n', 'n', 'n', 'n'),
(8, 1, 'Interns', '', 'y', 'n', 'n', 'n', 'n');