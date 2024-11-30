CREATE TABLE `CategoryInfo` (
                                 `categoryId` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增分类ID',
                                 `categoryCode` varchar(30) NOT NULL COMMENT '分类编码',
                                 `categoryName` varchar(30) NOT NULL COMMENT '分类名称',
                                 `pCategoryId` int(11) NOT NULL COMMENT '父级分类ID',
                                 `icon` varchar(50) DEFAULT NULL COMMENT '图标',
                                 `background` varchar(50) DEFAULT NULL COMMENT '背景图',
                                 `sort` tinyint(4) NOT NULL COMMENT '排序号',
                                 PRIMARY KEY (`categoryId`) USING BTREE,
                                 UNIQUE KEY `idx_key_category_code` (`categoryCode`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分类信息';
