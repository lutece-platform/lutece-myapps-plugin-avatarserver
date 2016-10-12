
--
-- Structure for table avatarserver_avatar
--

DROP TABLE IF EXISTS avatarserver_avatar;
CREATE TABLE avatarserver_avatar (		
id_avatar int default '0' NOT NULL,
email varchar(255) default '' NOT NULL,
mime_type varchar(50) default '' NOT NULL,
file_value LONG VARBINARY,
hash_email varchar(50) default '' NOT NULL,
PRIMARY KEY (id_avatar),
CONSTRAINT hash_email UNIQUE (hash_email)
);

