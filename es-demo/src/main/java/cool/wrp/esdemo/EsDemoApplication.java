package cool.wrp.esdemo;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author 码小瑞
 */
@SpringBootApplication
@MapperScan("cool.wrp.esdemo.mapper")
public class EsDemoApplication {

    @Value("${es.host}")
    private String esHost;

    public static void main(String[] args) {
        SpringApplication.run(EsDemoApplication.class, args);
    }

    @Bean
    RestHighLevelClient esClient() {
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create(esHost)
        ));
    }
}
