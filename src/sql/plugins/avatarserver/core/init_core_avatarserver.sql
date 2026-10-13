-- liquibase formatted sql
-- changeset avatarserver:init_core_avatarserver.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'AVATARSERVER_MANAGEMENT'
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'UPDATER_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('AVATARSERVER_MANAGEMENT','avatarserver.adminFeature.ManageAvatarserver.name',1,'jsp/admin/plugins/avatarserver/ManageAvatars.jsp','avatarserver.adminFeature.ManageAvatarserver.description',0,'avatarserver',NULL,'ti ti-user-circle',NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'AVATARSERVER_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('AVATARSERVER_MANAGEMENT',1);

-- changeset avatarserver:init_core_avatarserver-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-user-circle' WHERE id_right = 'AVATARSERVER_MANAGEMENT' AND ( icon_url IS NULL OR icon_url = '' );
