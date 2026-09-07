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
