> 索引库就类似数据库表，mapping映射就类似表的结构。我们要向es中存储数据，必须先创建“库”和“表”。

# mapping 是对索引库中文档的约束，常见的 mapping 属性包括：

- type：字段数据类型，常见的简单类型有：
    - 字符串：text（可分词的文本）、keyword（精确值，例如：品牌、国家、ip地址）
    - 数值：long、integer、short、byte、double、float、
    - 布尔：boolean
    - 日期：date
    - 对象：object
- index：是否创建索引，默认为true
- analyzer：使用哪种分词器
- properties：该字段的子字段
  geo_distance
# 创建索引和映射

**语法格式：**

```json lines
// 创建索引
PUT /索引库名称
{
  // 设置映射
  "mappings": {
    "properties": {
      "字段名":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "字段名2":{
        "type": "keyword",
        "index": "false"
      },
      "字段名3":{
        "properties": {
          "子字段": {
            "type": "keyword"
          }
        }
      },
      // ...略
    }
  }
}
```

创建 user 索引示例：

```json lines
PUT /user
{
  "mappings": {
    "properties": {
      "username": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "age": {
        "type": "integer"
      },
      "email":{
        "type": "keyword",
        "index": "false"
      }
    }
  }
}
```

# 自定义分词器

elasticsearch中分词器（analyzer）的组成包含三部分：

- character filters：在tokenizer之前对文本进行处理。例如删除字符、替换字符
- tokenizer：将文本按照一定的规则切割成词条（term）。例如keyword，就是不分词；还有ik_smart
- tokenizer filter：将tokenizer输出的词条做进一步处理。例如大小写转换、同义词处理、拼音处理等

文档分词时会依次由这三部分来处理文档：

![分词器处理顺序](./images/分词器处理顺序.png)

**自定义分词器语法格式：**

```json lines
PUT /test
{
  "settings": {
    "analysis": {
      "analyzer": { // 自定义分词器
        "my_analyzer": {  // 分词器名称
          "tokenizer": "ik_max_word",
          "filter": "py"
        }
      },
      "char_filter": { // 可在此处定义 character filters
      },
      "tokenizer": { // 可在此处定义 tokenizer
      },
      "filter": { // 自定义tokenizer filter
        "py": { // 过滤器名称
          "type": "pinyin", // 过滤器类型，这里是pinyin
          "keep_full_pinyin": false,
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true,
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "my_analyzer",
        "search_analyzer": "ik_smart"
      }
    }
  }
}
```

# 修改索引库

> 由于重建倒排索引的开销太大，所以在不允许直接修改索引库，仅可在索引库上新增字段。

**语法格式：**

```json lines
PUT /索引库名/_mapping
{
  "properties": {
    "新字段名":{
      "type": "integer"
    }
  }
}
```

**新增 user 索引库字段示例：**

```json lines
PUT /user/_mapping
{
  "properties": {
    "phone": {
      "type": "integer",
      "index": false
    }
  }
}
```

# 查询和删除索引库

**查询索引库：**

```http request
GET /user
```

**删除索引库：**

```http request
DELETE /user
```
