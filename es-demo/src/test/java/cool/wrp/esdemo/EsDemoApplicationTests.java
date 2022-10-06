package cool.wrp.esdemo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import cool.wrp.esdemo.mapper.HotelMapper;
import cool.wrp.esdemo.pojo.HotelDoc;
import cool.wrp.esdemo.pojo.HotelEntity;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EsDemoApplicationTests {

    @Value("${es.host}")
    private String esHost;

    private RestHighLevelClient esClient;
    private final HotelMapper hotelMapper;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        esClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create(esHost)
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (esClient != null) {
            esClient.close();
        }
    }

    /* 索引库增删改查 start */

    @Test
    void testCreateIndex() throws IOException {
        // 1.准备Request      PUT /hotel
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        // 2.准备请求参数
        String json = "{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\" : {\n" +
                "      \"address\" : {\n" +
                "        \"type\" : \"keyword\",\n" +
                "        \"index\" : false\n" +
                "      },\n" +
                "      \"all\" : {\n" +
                "        \"type\" : \"text\",\n" +
                "        \"analyzer\" : \"ik_max_word\"\n" +
                "      },\n" +
                "      \"brand\" : {\n" +
                "        \"type\" : \"keyword\",\n" +
                "        \"copy_to\" : [\n" +
                "          \"all\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"business\" : {\n" +
                "        \"type\" : \"keyword\",\n" +
                "        \"copy_to\" : [\n" +
                "          \"all\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"city\" : {\n" +
                "        \"type\" : \"keyword\"\n" +
                "      },\n" +
                "      \"id\" : {\n" +
                "        \"type\" : \"keyword\"\n" +
                "      },\n" +
                "      \"location\" : {\n" +
                "        \"type\" : \"geo_point\"\n" +
                "      },\n" +
                "      \"name\" : {\n" +
                "        \"type\" : \"keyword\",\n" +
                "        \"copy_to\" : [\n" +
                "          \"all\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"pic\" : {\n" +
                "        \"type\" : \"keyword\"\n" +
                "      },\n" +
                "      \"price\" : {\n" +
                "        \"type\" : \"integer\"\n" +
                "      },\n" +
                "      \"score\" : {\n" +
                "        \"type\" : \"integer\"\n" +
                "      },\n" +
                "      \"starName\" : {\n" +
                "        \"type\" : \"keyword\"\n" +
                "      },\n" +
                "      \"isAd\" : {\n" +
                "        \"type\" : \"boolean\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        request.source(json, XContentType.JSON);
        // 3.发送请求
        esClient.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testExistsIndex() throws IOException {
        // 1.准备Request
        GetIndexRequest request = new GetIndexRequest("hotel");
        // 3.发送请求
        boolean isExists = esClient.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(isExists ? "存在" : "不存在");
    }

    @Test
    void testDeleteIndex() throws IOException {
        // 1.准备Request
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        // 3.发送请求
        esClient.indices().delete(request, RequestOptions.DEFAULT);
    }

    /* 文档增删改查 start */

    @Test
    void testAddDocument() throws IOException {
        // 1.查询数据库hotel数据
        HotelEntity hotel = new LambdaQueryChainWrapper<>(hotelMapper)
                .eq(HotelEntity::getId, 61083L)
                .one();
        // 2.转换为HotelDoc
        HotelDoc hotelDoc = new HotelDoc(hotel);
        // 3.转JSON
        String json = JSON.toJSONString(hotelDoc);

        // 1.准备Request
        IndexRequest request = new IndexRequest("hotel")
                .id(hotelDoc.getId().toString());
        // 2.准备请求参数DSL，其实就是文档的JSON字符串
        request.source(json, XContentType.JSON);
        // 3.发送请求
        esClient.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备 Request -> GET /hotel/_doc/{id}
        GetRequest request = new GetRequest("hotel", "61083");
        // 2.发送请求
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();

        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println("hotelDoc = " + hotelDoc);
    }

    @Test
    void testDeleteDocumentById() throws IOException {
        // 1.准备 Request -> DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest("hotel", "61083");
        // 2.发送请求
        esClient.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateById() throws IOException {
        // 1. 准备 Request
        UpdateRequest request = new UpdateRequest("hotel", "61083");
        // 2. 准备参数
        request.doc(
                "price", "870"
        );
        // 3.发送请求
        esClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 批量操作数据
     */
    @Test
    void testBulkRequest() throws IOException {
        // 查询所有的酒店数据
        List<HotelEntity> list = new LambdaQueryChainWrapper<>(hotelMapper)
                .list();

        // 1.准备Request
        BulkRequest request = new BulkRequest();
        // 2.准备参数
        for (HotelEntity hotel : list) {
            // 2.1.转为HotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            // 2.2.添加请求
            final IndexRequest indexRequest = new IndexRequest("hotel")
                    .id(hotel.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON);
            request.add(indexRequest);
        }

        // 3.发送请求
        esClient.bulk(request, RequestOptions.DEFAULT);
    }
}