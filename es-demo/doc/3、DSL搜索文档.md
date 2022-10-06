# copy_to

参考：[ES系列之原理copy_to用好了这么香](https://cloud.tencent.com/developer/article/1697284)

> 可以使用 copy_to 把多个字段合并到一个字段内，方便对多个字段做搜索，对一个字段的搜索效率也要比对多个字段搜索高。

**copy_to 使用示例：**

```json lines
PUT my_index
{
  "mappings": {
    "properties": {
      "first_name": {
        "type": "text",
        "copy_to": "full_name" 
      },
      "last_name": {
        "type": "text",
        "copy_to": "full_name" 
      },
      "full_name": {
        "type": "text"
      }
    }
  }
}

PUT my_index/_doc/1
{
  "first_name": "John",
  "last_name": "Smith"
}

PUT my_index/_doc/2
{
  "first_name": "Tom",
  "last_name": "Cruise"
}

GET my_index/_search
{
  "query": {
    "match": {
      "full_name": {
        "query": "John Smith",
        "operator": "and"
      }
    }
  }
}
```

# 全文搜索

使用场景：搜索框搜索

> 要求：搜索字段必须分词

全文检索查询的基本流程如下：

1. 对用户搜索的内容做分词，得到词条
2. 根据词条去倒排索引库中匹配，得到文档id
3. 根据文档id找到文档，返回给用户

**查询所有：**

```json lines
GET /indexName/_search
{
  "query": {
    "match_all": {
    }
  }
}
```

**多字段查询：**

```json lines
GET /indexName/_search
{
  "query": {
    "multi_match": {
      "query": "TEXT",
      "fields": ["FIELD1", " FIELD12"]
    }
  }
}
```

**使用示例：**

```json lines
// 查询单个字段
GET hotel/_search
{
  "query": {
    "match": {
      "all": "外滩如家"
    }
  }
}

// 查询多个字段
GET hotel/_search
{
  "query": {
    "multi_match": {
      "query": "外滩如家",
      "fields": ["bound", "name", "business"]
    }
  }
}
```

# 精确搜索

## term：根据词条精确值搜索

因为精确查询的字段搜是不分词的字段，因此查询的条件也必须是**不分词**的词条。查询时，用户输入的内容跟自动值完全匹配时才认为符合条件。如果用户输入的内容过多，反而搜索不到数据。

语法格式：

```json lines
GET /indexName/_search
{
  "query": {
    "term": {
      "FIELD": {
        "value": "VALUE"
      }
    }
  }
}
```

## range：根据值的范围搜索

范围查询，一般应用在对数值类型做范围过滤的时候。比如做价格范围过滤。

语法格式：

```json lines
GET /indexName/_search
{
  "query": {
    "range": {
      "FIELD": {
        "gte": 10, // 这里的gte代表大于等于，gt则代表大于
        "lte": 20  // lte代表小于等于，lt则代表小于
      }
    }
  }
}
```

## 地理坐标搜索

根据经纬度搜索，[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html)

### 矩形范围搜索，geo_bounding_box

搜索坐标落在某个矩形范围的所有文档。 搜索时，需要指定矩形的 **左上**、**右下** 两个点的坐标，然后画出一个矩形，落在该矩形内的都是符合条件的点。

语法格式：

```json lines
GET /indexName/_search
{
  "query": {
    "geo_bounding_box": {
      "FIELD": {
        "top_left": { // 左上点
          "lat": 31.1,
          "lon": 121.5
        },
        "bottom_right": { // 右下点
          "lat": 30.9,
          "lon": 121.7
        }
      }
    }
  }
}
```

### 距离范围搜索，geo_distance

附近查询，也叫做距离查询（geo_distance）：查询到指定中心点小于某个距离值的所有文档。

换句话来说，在地图上找一个点作为圆心，以指定距离为半径，画一个圆，落在圆内的坐标都算符合条件：

语法格式：

```json lines
GET /indexName/_search
{
  "query": {
    "geo_distance": {
      "distance": "15km", // 半径
      "FIELD": "31.21,121.5" // 圆心
    }
  }
}
```

# bool搜索

布尔查询是一个或多个查询子句的组合，每一个子句就是一个**子查询**。子查询的组合方式有：

- must：必须匹配每个子查询，类似“与”
- should：选择性匹配子查询，类似“或”
- must_not：必须不匹配，**不参与算分**，类似“非”
- filter：必须匹配，**不参与算分**

比如在搜索酒店时，除了关键字搜索外，我们还可能根据品牌、价格、城市等字段做过滤。

每一个不同的字段，其查询的条件、方式都不一样，必须是多个不同的查询，而要组合这些查询，就必须用bool查询了。

需要注意的是，搜索时，参与**打分的字段越多，查询的性能也越差**。因此这种多条件查询时，建议这样做：

- 搜索框的关键字搜索，是全文检索查询，使用must查询，参与算分
- 其它过滤条件，采用filter查询。不参与算分

语法格式：

```json lines
GET /hotel/_search
{
  "query": {
    "bool": {
      "must": [
        {"term": {"city": "上海" }}
      ],
      "should": [
        {"term": {"brand": "皇冠假日" }},
        {"term": {"brand": "华美达" }}
      ],
      "must_not": [
        { "range": { "price": { "lte": 500 } }}
      ],
      "filter": [
        { "range": {"score": { "gte": 45 } }}
      ]
    }
  }
}
```

# 相关性算分

function score 查询中包含四部分内容：

- **原始查询**条件：query部分，基于这个条件搜索文档，并且基于BM25算法给文档打分，**原始算分**（query score)
- **过滤条件**：filter部分，符合该条件的文档才会重新算分
- **算分函数**：符合filter条件的文档要根据这个函数做运算，得到的**函数算分**（function score），有四种函数
    - weight：函数结果是常量
    - field_value_factor：以文档中的某个字段值作为函数结果
    - random_score：以随机数作为函数结果
    - script_score：自定义算分函数算法
- **运算模式**：算分函数的结果、原始查询的相关性算分，两者之间的运算方式，包括：
    - multiply：相乘
    - replace：用function score替换query score
    - 其它，例如：sum、avg、max、min

function score的运行流程如下：

- 1）根据**原始条件**查询搜索文档，并且计算相关性算分，称为**原始算分**（query score）
- 2）根据**过滤条件**，过滤文档
- 3）符合**过滤条件**的文档，基于**算分函数**运算，得到**函数算分**（function score）
- 4）将**原始算分**（query score）和**函数算分**（function score）基于**运算模式**做运算，得到最终结果，作为相关性算分。

因此，其中的关键点是：

- 过滤条件：决定哪些文档的算分被修改
- 算分函数：决定函数算分的算法
- 运算模式：决定最终算分结果

使用示例：

```json lines
GET /hotel/_search
{
  "query": {
    "function_score": {
      "query": {}, // 原始查询，可以是任意条件
      "functions": [ // 算分函数
        {
          "filter": { // 满足的条件，品牌必须是如家
            "term": {
              "brand": "如家"
            }
          },
          "weight": 2 // 算分权重为2
        }
      ],
      "boost_mode": "sum" // 加权模式，求和
    }
  }
}
```

# 聚合搜索

> **注意：** 参加聚合的字段必须是keyword、日期、数值、布尔类型

聚合常见的有三类：

1. **桶（Bucket）** 聚合：用来对文档做分组
  - TermAggregation：按照文档字段值分组，例如按照品牌值分组、按照国家分组
  - Date Histogram：按照日期阶梯分组，例如一周为一组，或者一月为一组
2. **度量（Metric）** 聚合：用以计算一些值，比如：最大值、最小值、平均值等
  - Avg：求平均值
  - Max：求最大值
  - Min：求最小值
  - Stats：同时求max、min、avg、sum等
3. **管道（pipeline）** 聚合：其它聚合的结果为基础做聚合

**语法格式：**

```json lines
GET /hotel/_search
{
  "query": {
    "range": {
      "price": {
        // Bucket聚合默认对索引库的所有文档做聚合。
        // 但真实场景下，用户会输入搜索条件，因此聚合必须是对搜索结果聚合。那么聚合必须添加限定条件。
        "lte": 200 // 只对200元以下的文档聚合
      }
    }
  },
  "size": 0,  // 设置size为0，结果中不包含文档，只包含聚合结果
  "aggs": { // 定义聚合
    "brandAgg": { //给聚合起个名字
      "terms": { // 聚合的类型，按照品牌值聚合，所以选择term
        "field": "brand", // 参与聚合的字段
        "size": 20, // 希望获取的聚合结果数量
        "order": { // 给聚合结果做排序
          "score_stats.avg": "desc" // 按照平均分倒序排序
        }
      },
      "aggs": { // 是brands聚合的子聚合，也就是分组后对每组分别计算
        "score_stats": { // 聚合名称
          "stats": { // 聚合类型，这里stats会取出所有的聚合计算结果：min、max、avg等
            "field": "score" // 聚合字段，这里是score
          }
        }
      }
    }
  }
}
```

# 自动补全搜索

[自动补全搜索官网](https://www.elastic.co/guide/en/elasticsearch/reference/7.6/search-suggesters.html)

自动补全搜索会匹配以用户输入内容开头的词条并返回。 为了提高补全查询的效率，对于文档中字段的类型有一些约束：

- 参与补全查询的字段必须是 completion 类型。
- 字段的内容一般是用来补全的多个词条形成的数组。

**索引库：**

```json lines
PUT test
{
  "mappings": {
    "properties": {
      "title":{
        "type": "completion"
      }
    }
  }
}
```

**示例数据：**

```json lines
POST test/_doc
{
  "title": ["Sony", "WH-1000XM3"]
}
POST test/_doc
{
  "title": ["SK-II", "PITERA"]
}
POST test/_doc
{
  "title": ["Nintendo", "switch"]
}
```

**自动补全的DSL语句：**

```json lines
GET /test/_search
{
  "suggest": {
    "title_suggest": {
      "text": "s", // 关键字
      "completion": {
        "field": "title", // 补全查询的字段
        "skip_duplicates": true, // 跳过重复的
        "size": 10 // 只获取前10条结果
      }
    }
  }
}
```