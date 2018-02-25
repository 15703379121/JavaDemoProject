# Host: localhost  (Version 5.7.20-log)
# Date: 2018-02-25 17:28:10
# Generator: MySQL-Front 5.4  (Build 4.153) - http://www.mysqlfront.de/

/*!40101 SET NAMES utf8 */;

#
# Structure for table "catalog"
#

DROP TABLE IF EXISTS `catalog`;
CREATE TABLE `catalog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project` int(11) DEFAULT NULL,
  `tag` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Data for table "catalog"
#

INSERT INTO `catalog` VALUES (1,0,1,'java概述');

#
# Structure for table "user"
#

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `identifier` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hcenuj4ofutyg7lgx0dh017fh` (`identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

#
# Data for table "user"
#

INSERT INTO `user` VALUES (1,'0808990816','123','1','李芳'),(2,'0961140401','123','0','白沙'),(3,'root','root','2','管理员');

#
# Structure for table "practice"
#

DROP TABLE IF EXISTS `practice`;
CREATE TABLE `practice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6imtt0a723sdy5abwpe8rh9u2` (`user_id`),
  CONSTRAINT `FK6imtt0a723sdy5abwpe8rh9u2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

#
# Data for table "practice"
#

INSERT INTO `practice` VALUES (1,'info','Suser','http://192.168.43.248:8080/javademo/upload/practice/1519372478642.txt',1),(2,'文档','文档测试','http://192.168.43.248:8080/javademo/upload/practice/1519373050018.docx',1);

#
# Structure for table "catalog_practice"
#

DROP TABLE IF EXISTS `catalog_practice`;
CREATE TABLE `catalog_practice` (
  `catalog_id` int(11) NOT NULL,
  `practice_id` int(11) NOT NULL,
  UNIQUE KEY `UK_fwlyp69xcq36ls05bmk1d75gl` (`practice_id`),
  KEY `FKd4r2a116aiyl75i0duws98mqy` (`catalog_id`),
  CONSTRAINT `FKary8htnfd33e1lyi62dt9qeit` FOREIGN KEY (`practice_id`) REFERENCES `practice` (`id`),
  CONSTRAINT `FKd4r2a116aiyl75i0duws98mqy` FOREIGN KEY (`catalog_id`) REFERENCES `catalog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "catalog_practice"
#

INSERT INTO `catalog_practice` VALUES (1,1),(1,2);

#
# Structure for table "information"
#

DROP TABLE IF EXISTS `information`;
CREATE TABLE `information` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commentinfo_size` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `voteinfo_size` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK41l434l1a16bjfxld0rmcjj7b` (`user_id`),
  CONSTRAINT `FK41l434l1a16bjfxld0rmcjj7b` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

#
# Data for table "information"
#

INSERT INTO `information` VALUES (1,0,'2018-02-20 12:35:47','分享图片',0,'http://192.168.43.248:8080/javademo/upload/publish/pic/1519101346296.jpg;http://192.168.43.248:8080/javademo/upload/publish/pic/1519101346304.jpg;http://192.168.43.248:8080/javademo/upload/publish/pic/1519101346341.png;',0,1),(2,1,'2018-02-20 12:51:11','分享视频',-1,'',1,1),(3,0,'2018-02-20 13:00:49','分享视频',1,'http://192.168.43.248:8080/javademo/upload/publish/video/1519102848792.mp4;http://192.168.43.248:8080/javademo/upload/publish/video/1519102848806.jpg;',0,1),(4,0,'2018-02-20 13:03:43','发布一个',1,'http://192.168.43.248:8080/javademo/upload/publish/video/1519103022553.mp4;http://192.168.43.248:8080/javademo/upload/publish/video/1519103022681.jpg;',1,1),(5,0,'2018-02-20 13:04:31','拍摄图片',0,'http://192.168.43.248:8080/javademo/upload/publish/pic/1519103071232.png;',1,1),(7,3,'2018-02-20 21:35:29','发一个好看的视频共享一哈哈',1,'http://192.168.43.248:8080/javademo/upload/publish/video/1519133728618.mp4;http://192.168.43.248:8080/javademo/upload/publish/video/1519133729436.jpg;',2,1),(8,0,'2018-02-25 15:54:28','呀',0,'http://192.168.43.248:8080/javademo/upload/publish/pic/1519545267707.png;',1,2),(9,0,'2018-02-25 17:15:20','你看',0,'http://192.168.43.248:8080/javademo/upload/publish/pic/1519550119787.jpg;',0,1),(10,0,'2018-02-25 17:15:52','咦，这是谁啊？',-1,'',0,2);

#
# Structure for table "collection"
#

DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `information_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK11wmkwo57j01340xbgygm9i7i` (`information_id`),
  KEY `FKpo8h7vwck3icylwdgbhs04jf8` (`user_id`),
  CONSTRAINT `FK11wmkwo57j01340xbgygm9i7i` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`),
  CONSTRAINT `FKpo8h7vwck3icylwdgbhs04jf8` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "collection"
#


#
# Structure for table "commentinfo"
#

DROP TABLE IF EXISTS `commentinfo`;
CREATE TABLE `commentinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `information_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `comment_second_size` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKouuri64hl4qi3cjyq1xxel9v7` (`information_id`),
  KEY `FKav1mj8rwsoegu5dklvt66ppsu` (`user_id`),
  CONSTRAINT `FKav1mj8rwsoegu5dklvt66ppsu` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKouuri64hl4qi3cjyq1xxel9v7` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

#
# Data for table "commentinfo"
#

INSERT INTO `commentinfo` VALUES (1,'说说评论测试','2018-02-22 14:36:38',1,7,1,3),(3,'幸福是啥啊','2018-02-23 12:14:20',1,7,1,0),(6,'你好啊','2018-02-23 13:44:00',1,7,2,0),(7,'分享','2018-02-24 12:16:44',1,2,1,0);

#
# Structure for table "information_commentinfo"
#

DROP TABLE IF EXISTS `information_commentinfo`;
CREATE TABLE `information_commentinfo` (
  `information_id` int(11) NOT NULL,
  `commentinfo_id` int(11) NOT NULL,
  UNIQUE KEY `UK_t89cv39o6twcpm7k5uvikimsx` (`commentinfo_id`),
  KEY `FKfb18io9yh2bcax0psdoptjpej` (`information_id`),
  CONSTRAINT `FKfb18io9yh2bcax0psdoptjpej` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`),
  CONSTRAINT `FKjluf2ekd51prvjuuajsckdiel` FOREIGN KEY (`commentinfo_id`) REFERENCES `commentinfo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "information_commentinfo"
#

INSERT INTO `information_commentinfo` VALUES (2,7),(7,1),(7,3),(7,6);

#
# Structure for table "comment_second"
#

DROP TABLE IF EXISTS `comment_second`;
CREATE TABLE `comment_second` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `commentinfo_id` int(11) DEFAULT NULL,
  `u_receive_id` int(11) DEFAULT NULL,
  `u_send_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt9xhidjpxnr4alg464bu2xpgk` (`commentinfo_id`),
  KEY `FKh3muuand4ub46wo2vn8o5tevp` (`u_receive_id`),
  KEY `FKmejyrcp06r3vrwdfg0xnv6oc0` (`u_send_id`),
  CONSTRAINT `FKh3muuand4ub46wo2vn8o5tevp` FOREIGN KEY (`u_receive_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKmejyrcp06r3vrwdfg0xnv6oc0` FOREIGN KEY (`u_send_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt9xhidjpxnr4alg464bu2xpgk` FOREIGN KEY (`commentinfo_id`) REFERENCES `commentinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

#
# Data for table "comment_second"
#

INSERT INTO `comment_second` VALUES (1,'说一说','2018-02-23 11:41:08',1,1,1,1),(2,'不知道','2018-02-23 12:11:14',1,1,1,1),(4,'哎呀','2018-02-23 12:13:44',1,1,1,1);

#
# Structure for table "commentinfo_comment_second"
#

DROP TABLE IF EXISTS `commentinfo_comment_second`;
CREATE TABLE `commentinfo_comment_second` (
  `commentinfo_id` int(11) NOT NULL,
  `comment_second_id` int(11) NOT NULL,
  UNIQUE KEY `UK_tavgn14b6vy96a2848gjykfp6` (`comment_second_id`),
  KEY `FKq35e0sxu86ob8jk5vyhbd1vxm` (`commentinfo_id`),
  CONSTRAINT `FK6wghuvjoav7v6hb6qgm702286` FOREIGN KEY (`comment_second_id`) REFERENCES `comment_second` (`id`),
  CONSTRAINT `FKq35e0sxu86ob8jk5vyhbd1vxm` FOREIGN KEY (`commentinfo_id`) REFERENCES `commentinfo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "commentinfo_comment_second"
#

INSERT INTO `commentinfo_comment_second` VALUES (1,1),(1,2),(1,4);

#
# Structure for table "video"
#

DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_size` int(11) DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `vote_size` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `catalog_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlvftuhj7tfoq8kigg4lc2ps7p` (`user_id`),
  CONSTRAINT `FKlvftuhj7tfoq8kigg4lc2ps7p` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

#
# Data for table "video"
#

INSERT INTO `video` VALUES (1,4,'这是一个测试视频','java概述','http://192.168.43.248:8080/javademo/upload/video/java.mp4',1,1,1),(2,0,'啊啊啊','什么个情况','http://192.168.43.248:8080/javademo/upload/video/smqk.mp4',0,1,1),(3,0,'推送测试','progress','http://192.168.43.248:8080/javademo/upload/video/1519534678600.mp4',0,1,1),(4,0,'123','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519535155648.mp4',0,1,1),(5,0,'111','oppowechat','http://192.168.43.248:8080/javademo/upload/video/1519536298502.mp4',0,1,1),(6,0,'1','1519102243692','http://192.168.43.248:8080/javademo/upload/video/1519536578345.mp4',0,1,1),(7,0,'1','1519102243692','http://192.168.43.248:8080/javademo/upload/video/1519536589361.mp4',0,1,1),(8,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519536620220.mp4',0,1,1),(9,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519536754658.mp4',0,1,1),(10,0,'啊','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519536797738.mp4',0,1,1),(11,0,'啊','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519537024801.mp4',0,1,1),(12,0,'啊','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519537092560.mp4',0,1,1),(13,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519537360380.mp4',0,1,1),(14,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519540103462.mp4',0,1,1),(15,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519540391040.mp4',0,1,1),(16,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519541225151.mp4',0,1,1),(17,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519541277575.mp4',0,1,1),(18,0,'哈哈哈','oppowechat','http://192.168.43.248:8080/javademo/upload/video/1519541595481.mp4',0,1,1),(19,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519541714737.mp4',0,1,1),(20,0,'？？？','VID20180206164121','http://192.168.43.248:8080/javademo/upload/video/1519542153488.mp4',0,1,1),(21,0,'！','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519542497151.mp4',0,1,1),(22,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519543113705.mp4',0,1,1),(23,0,'你好么','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519543559093.mp4',0,1,1),(24,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519543625902.mp4',0,1,1),(25,0,'？','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519543926646.mp4',0,1,1),(26,0,'1','VID20180203212618','http://192.168.43.248:8080/javademo/upload/video/1519544039000.mp4',0,1,1),(27,0,'试试','1517830310836','http://192.168.43.248:8080/javademo/upload/video/1519544172838.mp4',1,1,1),(28,0,'不知道啊','progress','http://192.168.43.248:8080/javademo/upload/video/1519544498071.mp4',0,1,1),(29,0,'啊','VID20180207144738','http://192.168.43.248:8080/javademo/upload/video/1519544604773.mp4',0,1,1),(30,0,'123','VID20180207144738','http://192.168.43.248:8080/javademo/upload/video/1519544824563.mp4',0,1,1),(31,0,'咦','VID20180207144738','http://192.168.43.248:8080/javademo/upload/video/1519544985283.mp4',0,1,1),(32,1,'那就厉害了','1517830310836','http://192.168.43.248:8080/javademo/upload/video/1519545141723.mp4',1,1,1);

#
# Structure for table "comment"
#

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `video_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8kcum44fvpupyw6f5baccx25c` (`user_id`),
  KEY `FKbsuh08kv1lyh8v6ivufrollr6` (`video_id`),
  CONSTRAINT `FK8kcum44fvpupyw6f5baccx25c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbsuh08kv1lyh8v6ivufrollr6` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

#
# Data for table "comment"
#

INSERT INTO `comment` VALUES (1,'这是一个评论测试','2018-02-18 19:19:09',1,2,1),(2,'添加评论','2018-02-19 10:58:01',1,2,1),(3,'我也不知道咋回事','2018-02-19 11:02:23',1,2,1),(4,'哈哈哈','2018-02-19 11:11:11',1,2,1),(5,'试试','2018-02-25 15:53:17',1,2,32);

#
# Structure for table "catalog_video"
#

DROP TABLE IF EXISTS `catalog_video`;
CREATE TABLE `catalog_video` (
  `catalog_id` int(11) NOT NULL,
  `video_id` int(11) NOT NULL,
  UNIQUE KEY `UK_rotge2n8jg6dh4dyygu9bc2vm` (`video_id`),
  KEY `FKobjwn2ggojfekkde9h831g3wu` (`catalog_id`),
  CONSTRAINT `FKobjwn2ggojfekkde9h831g3wu` FOREIGN KEY (`catalog_id`) REFERENCES `catalog` (`id`),
  CONSTRAINT `FKrrvyhhpeteufunge6et32mptb` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "catalog_video"
#

INSERT INTO `catalog_video` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32);

#
# Structure for table "video_comment"
#

DROP TABLE IF EXISTS `video_comment`;
CREATE TABLE `video_comment` (
  `video_id` int(11) NOT NULL,
  `comment_id` int(11) NOT NULL,
  UNIQUE KEY `UK_k15a7vs0bl11b7q3bc88x9nw3` (`comment_id`),
  KEY `FKi9rnt9uebvtiw4clylqvukqbr` (`video_id`),
  CONSTRAINT `FK7qcjc7d5kd072rch4xl5x42dr` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `FKi9rnt9uebvtiw4clylqvukqbr` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "video_comment"
#

INSERT INTO `video_comment` VALUES (1,1),(1,2),(1,3),(1,4),(32,5);

#
# Structure for table "vote"
#

DROP TABLE IF EXISTS `vote`;
CREATE TABLE `vote` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `video_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcsaksoe2iepaj8birrmithwve` (`user_id`),
  KEY `FKs9icflsjmtnm52ndfollma1sy` (`video_id`),
  CONSTRAINT `FKcsaksoe2iepaj8birrmithwve` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKs9icflsjmtnm52ndfollma1sy` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

#
# Data for table "vote"
#

INSERT INTO `vote` VALUES (10,'2018-02-19 11:32:53',1,1,1),(11,'2018-02-25 15:39:03',1,2,27),(12,'2018-02-25 15:52:58',1,2,32);

#
# Structure for table "video_vote"
#

DROP TABLE IF EXISTS `video_vote`;
CREATE TABLE `video_vote` (
  `video_id` int(11) NOT NULL,
  `vote_id` int(11) NOT NULL,
  UNIQUE KEY `UK_s1m2alt1wfnfwic3407a1fuf` (`vote_id`),
  KEY `FKg6h7i78otx5q60j3mavtms41m` (`video_id`),
  CONSTRAINT `FKg6h7i78otx5q60j3mavtms41m` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`),
  CONSTRAINT `FKn5j0smce97a3yd3rcreit9em0` FOREIGN KEY (`vote_id`) REFERENCES `vote` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "video_vote"
#

INSERT INTO `video_vote` VALUES (1,10),(27,11),(32,12);

#
# Structure for table "voteinfo"
#

DROP TABLE IF EXISTS `voteinfo`;
CREATE TABLE `voteinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `information_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9trr5bfisb8n3gfksyiq7w2ju` (`information_id`),
  KEY `FKgesq7oajlntqscp8xbm228fs0` (`user_id`),
  CONSTRAINT `FK9trr5bfisb8n3gfksyiq7w2ju` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`),
  CONSTRAINT `FKgesq7oajlntqscp8xbm228fs0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

#
# Data for table "voteinfo"
#

INSERT INTO `voteinfo` VALUES (3,'2018-02-23 13:43:25',1,7,2),(4,'2018-02-23 13:43:37',1,7,1),(6,'2018-02-24 12:28:39',1,4,1),(8,'2018-02-24 12:34:57',1,5,1),(10,'2018-02-24 13:15:04',1,2,1),(11,'2018-02-25 15:59:26',1,8,1);

#
# Structure for table "information_voteinfo"
#

DROP TABLE IF EXISTS `information_voteinfo`;
CREATE TABLE `information_voteinfo` (
  `information_id` int(11) NOT NULL,
  `voteinfo_id` int(11) NOT NULL,
  UNIQUE KEY `UK_slfwg6r5xq8g8bf9er3er435m` (`voteinfo_id`),
  KEY `FKos0e8sv2wtpenxo7id32pfl7f` (`information_id`),
  CONSTRAINT `FKomquxcwwl45n45vmfyilj1gv1` FOREIGN KEY (`voteinfo_id`) REFERENCES `voteinfo` (`id`),
  CONSTRAINT `FKos0e8sv2wtpenxo7id32pfl7f` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "information_voteinfo"
#

INSERT INTO `information_voteinfo` VALUES (2,10),(4,6),(5,8),(7,3),(7,4),(8,11);
