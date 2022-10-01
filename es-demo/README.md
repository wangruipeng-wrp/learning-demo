# docker 安装 es

kibana 需要与 es 服务端互相通信，所以先构建一个 docker network

```bash
docker network create es-net
```

## 安装 es 服务端

下载启动即可，启动命令如下：

```bash
docker run -d \
    --name es \
    -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
    -e "discovery.type=single-node" \
    -v es-data:/usr/share/elasticsearch/data \
    -v es-plugins:/usr/share/elasticsearch/plugins \
    --privileged \
    --network es-net \
    -p 9200:9200 \
    -p 9300:9300 \
elasticsearch:7.12.1
```

启动命令参数解释：

| 命令 | 含义                |
| :---- |:-----|
| `-e "cluster.name=es-docker-cluster"` | 设置集群名称            |
| `-e "http.host=0.0.0.0"` | 监听的地址，可以外网访问      |
| `-e "ES_JAVA_OPTS=-Xms512m -Xmx512m"` | 内存大小              |
| `-e "discovery.type=single-node"` | 非集群模式             |
| `-v es-data:/usr/share/elasticsearch/data` | 挂载逻辑卷，绑定es的数据目录   |
| `-v es-logs:/usr/share/elasticsearch/logs` | 挂载逻辑卷，绑定es的日志目录   |
| `-v es-plugins:/usr/share/elasticsearch/plugins` | 挂载逻辑卷，绑定es的插件目录   |
| `--privileged` | 授予逻辑卷访问权          |
| `--network es-net` | 加入一个名为es-net的网络中  |
| `-p 9200:9200` | 端口映射配置            |

> 在浏览器中输入：`http://ip:9200` 即可看到 elasticsearch 的响应结果

## 安装 kibana

> kibana 安装时版本需要与 es 保持一致。 

kibana可以给我们提供一个elasticsearch的可视化界面，便于我们学习。

docker 下载安装即可，启动命令如下：

```bash
docker run -d \
    --name kibana \
    -e ELASTICSEARCH_HOSTS=http://es:9200 \
    --network=es-net \
    -p 5601:5601  \
kibana:7.12.1
```

启动参数说明：

| 命令 | 含义                |
| :---- |:-----|
| `--network es-net` | 加入一个名为es-net的网络中，与elasticsearch在同一个网络中 | 
| `-e ELASTICSEARCH_HOSTS=http://es:9200"` | 设置elasticsearch的地址，因为kibana已经与elasticsearch在一个网络，因此可以用容器名直接访问elasticsearch | 
| `-p 5601:5601` | 端口映射配置 | 

> 在浏览器中输入：`http://ip:5601` 即可看到 kibana 的响应结果

Management 目录下 Dev Tools 选项可以操作 es 服务端。

## 安装 IK 分词器

### 在线安装

1. 进入容器内部

```bash
docker exec -it elasticsearch /bin/bash
```

2. 在线下载并安装

```bash
# 下载安装
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.12.1/elasticsearch-analysis-ik-7.12.1.zip

# 退出
exit

# 重启容器
docker restart elasticsearch
```

### 离线安装

上传下载好的安装包到 es 的 plugins 目录下并解压重命名为 `ik`，重启容器即可。

### IK 分词器的两种分词模式

- `ik_smart`：最少切分
- `ik_max_word`：最细切分

### 扩展词和停用词

ik 目录下的 config 文件夹，里面的 `IKAnalyzer.cfg.xml` 文件可以配置 扩展词和停用词。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
        <comment>IK Analyzer 扩展配置</comment>
        <!--用户可以在这里配置自己的扩展字典 -->
        <entry key="ext_dict">ext_dict.dic</entry>
         <!--用户可以在这里配置自己的扩展停止词字典-->
        <entry key="ext_stopwords">extra_stopword.dic</entry>
        <!--用户可以在这里配置远程扩展字典 -->
        <!-- <entry key="remote_ext_dict">words_location</entry> -->
        <!--用户可以在这里配置远程扩展停止词字典-->
        <!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```

# DSL 操作 es

## DSL 操作索引



## DSL 操作文档

