# 新增文档

语法格式：

```json lines
POST /索引库名/_doc/文档id
{
    "字段1": "值1",
    "字段2": "值2",
    "字段3": {
        "子属性1": "值3",
        "子属性2": "值4"
    },
    // ...
}
```

使用示例：

```json lines
PUT /user/_doc/1
{
  "username": "张三",
  "age": 20,
  "email": "zhangsan@qq.com"
}
```

# 修改文档

语法格式：

```http request
GET /索引库名/_doc/{id}
```

使用示例：

```json lines
GET /user/_doc/1
```

> 修改文档分为全量修改和增量修改

### 全量修改

> 直接覆盖原来的文档

全量修改是覆盖原来的文档，其本质是：

- 根据指定的id删除文档
- 新增一个相同id的文档

**注意**：如果根据id删除时，id不存在，第二步的新增也会执行，也就从修改变成了新增操作了。

语法格式：

```http request
PUT /索引库名/_doc/文档id
{
    "字段1": "值1",
    "字段2": "值2",
    // ... 略
}
```

使用示例：

```json lines
PUT /user/_doc/1
{
  "username": "李四",
  "age": 20,
  "email": "zhangsan@qq.com"
}
```

### 增量修改

> 修改文档中的部分字段

语法格式：

```json lines
POST /索引库名/_update/文档id
{
  "doc": {
    "字段名":"新的值",
  }
}
```

使用示例：

```json lines
POST /user/_update/1
{
  "doc": {
    "email": "zs@qq.com"
  }
}
```

# 删除文档

语法格式：

```http request
DELETE /索引库名//_doc/id值
```

使用示例：

```http request
DELETE /user/_doc/1
```
