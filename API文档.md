# 点评项目 API 接口文档

---

## 通用响应格式

所有接口统一返回 `Result` 对象：

```json
{
  "success": true|false,
  "errorMsg": "错误信息",
  "data": {},
  "total": 0
}
```

---

## 一、用户模块 `/user`

### 1. 发送手机验证码

- **URL:** `POST /user/code`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | phone | String | 是 | Query | 手机号 |
- **响应:** `Result`

---

### 2. 登录

- **URL:** `POST /user/login`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | loginForm | LoginFormDTO | 是 | Body(JSON) | 登录表单 |
- **LoginFormDTO 结构:**
  | 字段 | 类型 | 说明 |
  |------|------|------|
  | phone | String | 手机号 |
  | code | String | 验证码 |
  | password | String | 密码 |
- **响应:** `Result`

---

### 3. 登出

- **URL:** `POST /user/logout`
- **参数:** 无
- **响应:** `Result`

---

### 4. 获取当前登录用户

- **URL:** `GET /user/me`
- **需要登录:** 是
- **参数:** 无
- **响应:** `Result` (data 为 UserDTO)
- **UserDTO 结构:**
  | 字段 | 类型 | 说明 |
  |------|------|------|
  | id | Long | 用户ID |
  | nickName | String | 昵称 |
  | icon | String | 头像 |

---

### 5. 查询用户详细信息

- **URL:** `GET /user/info/{id}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 用户ID |
- **响应:** `Result` (data 为 UserInfo)

**UserInfo 结构:**
| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| city | String | 城市名称 |
| introduce | String | 个人介绍 |
| fans | Integer | 粉丝数量 |
| followee | Integer | 关注数量 |
| gender | Boolean | 性别(0:男,1:女) |
| birthday | LocalDate | 生日 |
| credits | Integer | 积分 |
| level | Boolean | 会员级别 |

---

### 6. 根据ID查询用户基本信息

- **URL:** `GET /user/{id}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 用户ID |
- **响应:** `Result` (data 为 UserDTO)

---

### 7. 签到

- **URL:** `POST /user/sign`
- **需要登录:** 是
- **参数:** 无
- **响应:** `Result`

---

### 8. 统计连续签到天数

- **URL:** `GET /user/sign/count`
- **需要登录:** 是
- **参数:** 无
- **响应:** `Result`

---

## 二、商铺模块 `/shop`

### 1. 根据ID查询商铺详情

- **URL:** `GET /shop/{id}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 商铺ID |
- **响应:** `Result` (data 为 Shop)

**Shop 结构:**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 商铺名称 |
| typeId | Long | 商铺类型ID |
| images | String | 商铺图片(多张以逗号分隔) |
| area | String | 商圈 |
| address | String | 地址 |
| x | Double | 经度 |
| y | Double | 纬度 |
| avgPrice | Long | 均价 |
| sold | Integer | 销量 |
| comments | Integer | 评论数量 |
| score | Integer | 评分(1~5分,乘10存储) |
| openHours | String | 营业时间 |
| distance | Double | 距离(非DB字段) |

---

### 2. 新增商铺

- **URL:** `POST /shop`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | shop | Shop | 是 | Body(JSON) | 商铺数据 |
- **响应:** `Result` (data 为商铺ID)

---

### 3. 更新商铺

- **URL:** `PUT /shop`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | shop | Shop | 是 | Body(JSON) | 商铺数据 |
- **响应:** `Result`

---

### 4. 按类型分页查询商铺（支持经纬度排序）

- **URL:** `GET /shop/of/type`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | typeId | Integer | 是 | Query | 商铺类型ID |
  | current | Integer | 否 | Query | 页码(默认1) |
  | x | Double | 否 | Query | 当前经度(按距离排序) |
  | y | Double | 否 | Query | 当前纬度(按距离排序) |
- **响应:** `Result`

---

### 5. 按名称关键字搜索商铺

- **URL:** `GET /shop/of/name`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | name | String | 否 | Query | 商铺名称关键字 |
  | current | Integer | 否 | Query | 页码(默认1) |
- **响应:** `Result`

---

## 三、商铺类型模块 `/shop-type`

### 1. 查询所有商铺类型

- **URL:** `GET /shop-type/list`
- **参数:** 无
- **响应:** `Result` (data 为 List\<ShopType\>)

**ShopType 结构:**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 类型名称 |
| icon | String | 图标 |
| sort | Integer | 排序 |

---

## 四、博客/探店模块 `/blog`

### 1. 发布博客

- **URL:** `POST /blog`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | blog | Blog | 是 | Body(JSON) | 博客数据 |
- **Blog 结构:**
  | 字段 | 类型 | 说明 |
  |------|------|------|
  | id | Long | 主键 |
  | shopId | Long | 关联商铺ID |
  | userId | Long | 用户ID |
  | icon | String | 用户头像(非DB字段) |
  | name | String | 用户昵称(非DB字段) |
  | isLike | Boolean | 是否已点赞(非DB字段) |
  | title | String | 标题 |
  | images | String | 图片(最多9张,逗号分隔) |
  | content | String | 文字描述 |
  | liked | Integer | 点赞数 |
  | comments | Integer | 评论数 |
- **响应:** `Result`

---

### 2. 点赞/取消点赞博客

- **URL:** `PUT /blog/like/{id}`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 博客ID |
- **响应:** `Result`

---

### 3. 查看当前用户的博客

- **URL:** `GET /blog/of/me`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | current | Integer | 否 | Query | 页码(默认1) |
- **响应:** `Result` (data 为 List\<Blog\>)

---

### 4. 查询热门博客

- **URL:** `GET /blog/hot`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | current | Integer | 否 | Query | 页码(默认1) |
- **响应:** `Result`

---

### 5. 根据ID查询博客详情

- **URL:** `GET /blog/{id}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 博客ID |
- **响应:** `Result` (data 为 Blog)

---

### 6. 查询博客的点赞用户列表

- **URL:** `GET /blog/likes/{id}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 博客ID |
- **响应:** `Result`

---

### 7. 根据用户ID查询博客

- **URL:** `GET /blog/of/user`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Query | 用户ID |
  | current | Integer | 否 | Query | 页码(默认1) |
- **响应:** `Result` (data 为 List\<Blog\>)

---

### 8. 查询关注用户的博客（滚动分页）

- **URL:** `GET /blog/of/follow`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | lastId | Long | 是 | Query | 上一页最后一条ID |
  | offset | Integer | 否 | Query | 偏移量(默认0) |
- **响应:** `Result` (data 为 ScrollResult)

**ScrollResult 结构:**
| 字段 | 类型 | 说明 |
|------|------|------|
| list | List\<?\> | 数据列表 |
| minTime | Long | 最小时间戳 |
| offset | Integer | 偏移量 |

---

## 五、博客评论模块 `/blog-comments`

该模块暂无可用接口（Controller 未实现具体方法）。

---

## 六、关注模块 `/follow`

### 1. 关注/取关用户

- **URL:** `PUT /follow/{id}/{isFollow}`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 目标用户ID |
  | isFollow | Boolean | 是 | Path | true=关注, false=取关 |
- **响应:** `Result`

---

### 2. 判断是否已关注

- **URL:** `GET /follow/or/not/{id}`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 目标用户ID |
- **响应:** `Result`

---

### 3. 查询共同关注

- **URL:** `GET /follow/common/{id}`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 目标用户ID |
- **响应:** `Result`

---

## 七、优惠券模块 `/voucher`

### 1. 新增普通优惠券

- **URL:** `POST /voucher`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | voucher | Voucher | 是 | Body(JSON) | 优惠券数据 |
- **Voucher 结构:**
  | 字段 | 类型 | 说明 |
  |------|------|------|
  | id | Long | 主键 |
  | shopId | Long | 商铺ID |
  | title | String | 代金券标题 |
  | subTitle | String | 副标题 |
  | rules | String | 使用规则 |
  | payValue | Long | 支付金额(分) |
  | actualValue | Long | 抵扣金额(分) |
  | type | Integer | 优惠券类型 |
  | status | Integer | 状态 |
  | stock | Integer | 库存(非DB字段) |
  | beginTime | LocalDateTime | 生效时间(非DB字段) |
  | endTime | LocalDateTime | 失效时间(非DB字段) |
- **响应:** `Result` (data 为优惠券ID)

---

### 2. 新增秒杀优惠券

- **URL:** `POST /voucher/seckill`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | voucher | Voucher | 是 | Body(JSON) | 优惠券数据(包含秒杀信息) |
- **响应:** `Result` (data 为优惠券ID)

---

### 3. 查询店铺的优惠券列表

- **URL:** `GET /voucher/list/{shopId}`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | shopId | Long | 是 | Path | 店铺ID |
- **响应:** `Result` (data 为优惠券列表)

---

## 八、优惠券秒杀模块 `/voucher-order`

### 1. 秒杀优惠券

- **URL:** `POST /voucher-order/seckill/{id}`
- **需要登录:** 是
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | id | Long | 是 | Path | 优惠券ID |
- **响应:** `Result`

---

## 九、文件上传模块 `/upload`

### 1. 上传博客图片

- **URL:** `POST /upload/blog`
- **Content-Type:** `multipart/form-data`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | file | MultipartFile | 是 | FormData | 图片文件 |
- **响应:** `Result` (data 为文件名)

---

### 2. 删除博客图片

- **URL:** `GET /upload/blog/delete`
- **参数:**
  | 参数名 | 类型 | 必填 | 位置 | 说明 |
  |--------|------|------|------|------|
  | name | String | 是 | Query | 文件名 |
- **响应:** `Result`

---

## 接口汇总表

| 模块 | 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|------|----------|
| 用户 | POST | `/user/code` | 发送验证码 | 否 |
| 用户 | POST | `/user/login` | 登录 | 否 |
| 用户 | POST | `/user/logout` | 登出 | 否 |
| 用户 | GET | `/user/me` | 当前用户信息 | 是 |
| 用户 | GET | `/user/info/{id}` | 用户详细信息 | 否 |
| 用户 | GET | `/user/{id}` | 用户基本信息 | 否 |
| 用户 | POST | `/user/sign` | 签到 | 是 |
| 用户 | GET | `/user/sign/count` | 连续签到天数 | 是 |
| 商铺 | GET | `/shop/{id}` | 商铺详情 | 否 |
| 商铺 | POST | `/shop` | 新增商铺 | 否 |
| 商铺 | PUT | `/shop` | 更新商铺 | 否 |
| 商铺 | GET | `/shop/of/type` | 按类型分页查询 | 否 |
| 商铺 | GET | `/shop/of/name` | 按名称搜索 | 否 |
| 商铺类型 | GET | `/shop-type/list` | 全部类型列表 | 否 |
| 博客 | POST | `/blog` | 发布博客 | 是 |
| 博客 | PUT | `/blog/like/{id}` | 点赞博客 | 是 |
| 博客 | GET | `/blog/of/me` | 我的博客 | 是 |
| 博客 | GET | `/blog/hot` | 热门博客 | 否 |
| 博客 | GET | `/blog/{id}` | 博客详情 | 否 |
| 博客 | GET | `/blog/likes/{id}` | 点赞列表 | 否 |
| 博客 | GET | `/blog/of/user` | 按用户查博客 | 否 |
| 博客 | GET | `/blog/of/follow` | 关注用户博客 | 是 |
| 关注 | PUT | `/follow/{id}/{isFollow}` | 关注/取关 | 是 |
| 关注 | GET | `/follow/or/not/{id}` | 是否已关注 | 是 |
| 关注 | GET | `/follow/common/{id}` | 共同关注 | 是 |
| 优惠券 | POST | `/voucher` | 新增普通券 | 否 |
| 优惠券 | POST | `/voucher/seckill` | 新增秒杀券 | 否 |
| 优惠券 | GET | `/voucher/list/{shopId}` | 店铺优惠券列表 | 否 |
| 秒杀 | POST | `/voucher-order/seckill/{id}` | 秒杀优惠券 | 是 |
| 上传 | POST | `/upload/blog` | 上传图片 | 否 |
| 上传 | GET | `/upload/blog/delete` | 删除图片 | 否 |

---

> 总计：**9 个 Controller**，**26 个接口**（BlogCommentsController 无可用接口）
