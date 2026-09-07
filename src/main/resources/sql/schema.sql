CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '商品分类',
    `price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '商品图片地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1在售，0下架',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status_create_time` (`status`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品表';

CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '收藏表';

CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '留言ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `user_id` BIGINT NOT NULL COMMENT '留言用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '留言内容',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父留言ID（0为一级留言）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '留言表';
