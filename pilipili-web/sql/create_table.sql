# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员小新</a>
#

-- 创建库
create database if not exists xin_Chat;

-- 切换库
use xin_Chat;

-- 用户表
create table if not exists userInfo
(
    useId        varchar(12)                           not null comment 'userId' primary key,
    email        varchar(50)                            not null unique comment '邮箱',
    Password     varchar(32)                           not null comment '密码',
    NickName     varchar(20)                           not null unique comment '用户昵称',
    Sex          tinyint(1)                             null comment '1：男,0:女,2:未知',
    Birthday     varchar(10)                            null comment '出生日期',
    School       varchar(50)                            null comment '学校',
    userProfile  varchar(512)                           null comment '用户简介',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    JoinTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    lastLoginTime datetime                              null comment '最后登录时间',
    lastLoginIp    varchar(50)                              not null comment '最后登录ip',
    noticeInfo       varchar(300)                         null comment '空间通告',
    status     tinyint      default 1                 not null comment '用户状态 0:禁用，1:正常',
    totalCoinCount int(11)  default 10                 not null  comment '总金币数量',
    currentCoinCount int(11)  default 10                not null  comment '当前金币数量',
    theme     tinyint default 1                    not null comment '主题',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户信息表' collate = utf8mb4_general_ci;



## 视频发布的信息表，还未审核，所谓无需期待多余的信息
CREATE TABLE `VideoInfoPost` (
                                 `videoId` varchar(10) NOT NULL DEFAULT '0' COMMENT '视频ID',
                                 `videoCover` varchar(50) NOT NULL COMMENT '视频封面',
                                 `videoName` varchar(100) NOT NULL COMMENT '视频名称',
                                 `userId` varchar(10) NOT NULL COMMENT '用户ID',
                                 `createTime` datetime NOT NULL COMMENT '创建时间',
                                 `lastUpdateTime` datetime NOT NULL COMMENT '最后更新时间',
                                 `pCategoryId` int(11) NOT NULL COMMENT '父级分类ID',
                                 `categoryId` int(11) DEFAULT NULL COMMENT '分类ID',
                                 `status` tinyint(1) NOT NULL COMMENT '0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败',
                                 `postType` tinyint(4) NOT NULL COMMENT '0:自制作 1:转载',
                                 `originInfo` varchar(200) DEFAULT NULL COMMENT '原资源说明',
                                 `tags` varchar(300) DEFAULT NULL COMMENT '标签',
                                 `introduction` varchar(2000) DEFAULT NULL COMMENT '简介',
                                 `interaction` varchar(5) DEFAULT NULL COMMENT '互动设置',
                                 `duration` int(11) DEFAULT NULL COMMENT '持续时间（秒）',
                                 PRIMARY KEY (`videoId`) USING BTREE,
                                 KEY `idxCreateTime` (`createTime`) USING BTREE,
                                 KEY `idxUserId` (`userId`) USING BTREE,
                                 KEY `idxCategoryId` (`categoryId`) USING BTREE,
                                 KEY `idxPCategoryId` (`pCategoryId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='视频信息';


## 视频发布的信息表,新增了播放量，点赞量，弹幕数，评论数，投币数，收藏数，推荐状态，最后播放时间的信息
CREATE TABLE `VideoInfo` (
                             `videoId` varchar(10) NOT NULL DEFAULT '0' COMMENT '视频ID',
                             `videoCover` varchar(50) NOT NULL COMMENT '视频封面',
                             `videoName` varchar(100) NOT NULL COMMENT '视频名称',
                             `userId` varchar(10) NOT NULL COMMENT '用户ID',
                             `createTime` datetime NOT NULL COMMENT '创建时间',
                             `lastUpdateTime` datetime NOT NULL COMMENT '最后更新时间',
                             `pCategoryId` int(11) NOT NULL COMMENT '父级分类ID',
                             `categoryId` int(11) DEFAULT NULL COMMENT '分类ID',
                             `postType` tinyint(4) NOT NULL COMMENT '0:自制作 1:转载',
                             `originInfo` varchar(200) DEFAULT NULL COMMENT '原资源说明',
                             `tags` varchar(300) DEFAULT NULL COMMENT '标签',
                             `introduction` varchar(2000) DEFAULT NULL COMMENT '简介',
                             `interaction` varchar(5) DEFAULT NULL COMMENT '互动设置',
                             `duration` int(11) DEFAULT '0' COMMENT '持续时间（秒）',
                             `playCount` int(11) DEFAULT '0' COMMENT '播放数量',
                             `likeCount` int(11) DEFAULT '0' COMMENT '点赞数量',
                             `danMuCount` int(11) DEFAULT '0' COMMENT '弹幕数量',
                             `commentCount` int(11) DEFAULT '0' COMMENT '评论数量',
                             `coinCount` int(11) DEFAULT '0' COMMENT '投币数量',
                             `collectCount` int(11) DEFAULT '0' COMMENT '收藏数量',
                             `recommendType` tinyint(1) DEFAULT '0' COMMENT '是否推荐0:未推荐 1:已推荐',
                             `lastPlayTime` datetime DEFAULT NULL COMMENT '最后播放时间',
                             PRIMARY KEY (`videoId`) USING BTREE,
                             KEY `idxCreateTime` (`createTime`) USING BTREE,
                             KEY `idxUserId` (`userId`) USING BTREE,
                             KEY `idxCategoryId` (`categoryId`) USING BTREE,
                             KEY `idxPCategoryId` (`pCategoryId`) USING BTREE,
                             KEY `idxRecommendType` (`recommendType`) USING BTREE,
                             KEY `idxLastUpdateTime` (`lastPlayTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='视频信息';

## 视频文件信息表
CREATE TABLE `VideoInfoFilePost` (
                                     `fileId` varchar(20) NOT NULL COMMENT '唯一ID',
                                     `uploadId` varchar(15) NOT NULL COMMENT '上传ID',
                                     `userId` varchar(10) NOT NULL COMMENT '用户ID',
                                     `videoId` varchar(10) NOT NULL COMMENT '视频ID',
                                     `fileIndex` int(11) NOT NULL COMMENT '文件索引',
                                     `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
                                     `fileSize` bigint(20) DEFAULT NULL COMMENT '文件大小',
                                     `filePath` varchar(100) DEFAULT NULL COMMENT '文件路径',
                                     `updateType` tinyint(4) DEFAULT NULL COMMENT '0:无更新 1:有更新',
                                     `transferResult` tinyint(4) DEFAULT NULL COMMENT '0:转码中 1:转码成功 2:转码失败',
                                     `duration` int(11) DEFAULT NULL COMMENT '持续时间（秒）',
                                     PRIMARY KEY (`fileId`) USING BTREE,
                                     UNIQUE KEY `idxKeyUploadId` (`uploadId`, `userId`) USING BTREE,
                                     KEY `idxVideoId` (`videoId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='视频文件信息';

## 静态文件信息表
CREATE TABLE `VideoInfoFile` (
                                 `fileId` varchar(20) NOT NULL COMMENT '唯一ID',
                                 `userId` varchar(10) NOT NULL COMMENT '用户ID',
                                 `videoId` varchar(10) NOT NULL COMMENT '视频ID',
                                 `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
                                 `fileIndex` int(11) NOT NULL COMMENT '文件索引',
                                 `fileSize` bigint(20) DEFAULT NULL COMMENT '文件大小',
                                 `filePath` varchar(100) DEFAULT NULL COMMENT '文件路径',
                                 `duration` int(11) DEFAULT NULL COMMENT '持续时间（秒）',
                                 PRIMARY KEY (`fileId`) USING BTREE,
                                 KEY `idxVideoId` (`videoId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='视频文件信息';


CREATE TABLE `UserAction` (
                               `actionId` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                               `videoId` varchar(10) NOT NULL COMMENT '视频ID',
                               `videoUserId` varchar(10) NOT NULL COMMENT '视频用户ID',
                               `commentId` int(11) NOT NULL DEFAULT '0' COMMENT '评论ID',
                               `actionType` tinyint(1) NOT NULL COMMENT '0:评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币',
                               `actionCount` int(11) NOT NULL COMMENT '数量',
                               `userId` varchar(10) NOT NULL COMMENT '用户ID',
                               `actionTime` datetime NOT NULL COMMENT '操作时间',
                               PRIMARY KEY (`actionId`) USING BTREE,
                               UNIQUE KEY `idxKeyVideoCommentTypeUser` (`videoId`,`commentId`,`actionType`,`userId`) USING BTREE,
                               KEY `idxVideoId` (`videoId`) USING BTREE,
                               KEY `idxUserId` (`userId`) USING BTREE,
                               KEY `idxType` (`actionType`) USING BTREE,
                               KEY `idxActionTime` (`actionTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户行为 点赞、评论';


CREATE TABLE `VideoDanMu` (
                               `DanMuId` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                               `videoId` varchar(10) NOT NULL COMMENT '视频ID',
                               `fileId` varchar(20) NOT NULL COMMENT '唯一ID',
                               `userId` varchar(15) NOT NULL COMMENT '用户ID',
                               `postTime` datetime DEFAULT NULL COMMENT '发布时间',
                               `text` varchar(300) DEFAULT NULL COMMENT '内容',
                               `mode` tinyint(1) DEFAULT NULL COMMENT '展示位置',
                               `color` varchar(10) DEFAULT NULL COMMENT '颜色',
                               `time` int(11) DEFAULT NULL COMMENT '展示时间',
                               PRIMARY KEY (`DanMuId`) USING BTREE,
                               KEY `idxFileId` (`fileId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='视频弹幕';


CREATE TABLE `VideoComment` (
                                 `commentId` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
                                 `pCommentId` int(11) NOT NULL COMMENT '父级评论ID',
                                 `videoId` varchar(10) NOT NULL COMMENT '视频ID',
                                 `videoUserId` varchar(10) NOT NULL COMMENT '视频用户ID',
                                 `content` varchar(500) DEFAULT NULL COMMENT '回复内容',
                                 `imgPath` varchar(150) DEFAULT NULL COMMENT '图片',
                                 `userId` varchar(15) NOT NULL COMMENT '用户ID',
                                 `replyUserId` varchar(15) DEFAULT NULL COMMENT '回复人ID',
                                 `topType` tinyint(4) DEFAULT '0' COMMENT '0:不置顶 1:置顶',
                                 `postTime` datetime NOT NULL COMMENT '发布时间',
                                 `likeCount` int(11) DEFAULT '0' COMMENT '喜欢数量',
                                 `hateCount` int(11) DEFAULT '0' COMMENT '讨厌数量',
                                 PRIMARY KEY (`commentId`) USING BTREE,
                                 KEY `idxPostTime` (`postTime`) USING BTREE,
                                 KEY `idxTop` (`topType`) USING BTREE,
                                 KEY `idxPId` (`pCommentId`) USING BTREE,
                                 KEY `idxUserId` (`userId`) USING BTREE,
                                 KEY `idxVideoId` (`videoId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='评论';

CREATE TABLE `UserFocus` (
    `userId` varchar(10) NOT NULL COMMENT '用户ID',
    `focusUserId` varchar(10) NOT NULL COMMENT '用户ID',
    `focusTime` datetime DEFAULT NULL,
    PRIMARY KEY (`userId`, `focusUserId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

CREATE TABLE `UserVideoSeries` (
    `seriesId` int(11) NOT NULL AUTO_INCREMENT COMMENT '列表ID',
    `seriesName` varchar(100) NOT NULL COMMENT '列表名称',
    `seriesDescription` varchar(200) DEFAULT NULL COMMENT '描述',
    `userId` varchar(10) NOT NULL COMMENT '用户ID',
    `sort` tinyint(4) NOT NULL COMMENT '排序',
    `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`seriesId`) USING BTREE,
    KEY `idxUserId` (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户视频序列归档';


CREATE TABLE `UserVideoSeriesVideo` (
    `seriesId` int(11) NOT NULL COMMENT '列表ID',
    `videoId` varchar(10) NOT NULL COMMENT '视频ID',
    `userId` varchar(10) NOT NULL COMMENT '用户ID',
    `sort` tinyint(4) NOT NULL COMMENT '排序',
    PRIMARY KEY (`seriesId`, `videoId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;