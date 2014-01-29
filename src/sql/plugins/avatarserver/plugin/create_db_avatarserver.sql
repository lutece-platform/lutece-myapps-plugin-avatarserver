
--
-- Structure for table avatarserver_avatar
--

DROP TABLE IF EXISTS avatarserver_avatar;
CREATE TABLE avatarserver_avatar (		
id_avatar int(11) NOT NULL default '0',
email varchar(255) NOT NULL default '',
avatar_image varchar(50) NOT NULL default '',
PRIMARY KEY (id_avatar)
);
