# 浏览相关流程说明

浏览相关功能包含两条主链路：商品列表浏览与检索、商品详情浏览与浏览量统计。

## 1. 商品列表浏览与检索

用户进入 `/` 商品列表页后，前端初始化 `page = 1`、`size = 10`，并请求：

```text
GET /products?page=1&size=10&keyword=&category=
```

后端处理过程：

1. `ProductController.list` 接收分页、关键词、分类参数。
2. `ProductService.listProducts` 将 `page` 限制为最小 1，将 `size` 限制在 1 到 100。
3. 根据参数拼接 Redis 缓存 Key。
4. 优先读取 Redis 缓存，命中则直接返回。
5. 未命中时构建 MyBatis-Plus 查询条件：
   - 仅查询 `status = 1` 的在售商品。
   - 传入分类时，按分类精确过滤。
   - 传入关键词时，对标题或描述进行模糊查询。
   - 按创建时间倒序排序。
6. 查询 MySQL 并写入 Redis 缓存，TTL 为 10 分钟。
7. 返回 `PageResult`，前端渲染商品卡片和分页控件。

前端交互：

- 搜索框输入关键词后按回车，将页码重置为 1 后重新加载。
- 分类按钮切换后，将页码重置为 1 后重新加载。
- 上一页、下一页按钮改变页码后重新加载。

## 2. 商品详情浏览与浏览量统计

用户点击商品卡片后跳转到 `/products/:id`，前端请求：

```text
GET /products/:id
```

后端处理过程：

1. `ProductController.detail` 调用 `ProductService.viewProduct`。
2. `viewProduct` 先调用 `getProductDetail`：
   - 优先读取 Redis 详情缓存，TTL 为 30 分钟。
   - 缓存未命中时从 MySQL 查询，仅返回 `status = 1` 的商品。
   - 查询成功后写入详情缓存。
3. 商品存在时调用 `incrementViewCount`：
   - Redis 浏览量计数加一。
   - 通过 SQL 将 `view_count` 加一。
   - 更新浏览量缓存，并删除旧详情缓存。
4. 更新详情缓存中的浏览量后返回商品。
5. 前端展示商品图片、标题、价格、描述、发布者、浏览量和发布时间。

对应图片：`browse-flow.png`

