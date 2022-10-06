package cool.wrp.esdemo.web;

import cool.wrp.esdemo.pojo.PageResult;
import cool.wrp.esdemo.pojo.RequestParams;
import cool.wrp.esdemo.utils.EsUtil;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 码小瑞
 */
@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HotelController {

    /* 代码比较简单，这里就不严格遵循 MVC 三层架构了 */

    private final RestHighLevelClient esClient;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams params) {
        try {
            // 前端传参数有点问题，没去研究，毕竟我也不咋研究前端，有好心人看到了帮忙改改也可以，要不留个 TODO 吧
            params.setLocation("31.03463,121.61245");

            // 1.准备Request
            SearchRequest request = new SearchRequest("hotel");

            // 2.准备请求参数
            EsUtil.hotelBasicQuery(request, params);
            EsUtil.buildPage(request, params);
            EsUtil.sortByLocation(request, params);

            // 3.发送请求
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

            // 4.解析响应
            return EsUtil.handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("搜索数据失败", e);
        }
    }

    @PostMapping("/filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params) {
        try {
            SearchRequest request = new SearchRequest("hotel");
            EsUtil.hotelBasicQuery(request, params);
            request.source().size(0);

            // 聚合
            request.source()
                    .aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(100));
            request.source()
                    .aggregation(AggregationBuilders.terms("cityAgg").field("city").size(100));
            request.source()
                    .aggregation(AggregationBuilders.terms("starAgg").field("starName").size(100));

            // 发出请求
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

            // 解析结果
            Aggregations aggregations = response.getAggregations();
            List<String> brandList = EsUtil.getAggregationByName(aggregations, "brandAgg");
            List<String> cityList = EsUtil.getAggregationByName(aggregations, "cityAgg");
            List<String> starList = EsUtil.getAggregationByName(aggregations, "starAgg");

            Map<String, List<String>> filters = new HashMap<>(3);
            filters.put("brand", brandList);
            filters.put("city", cityList);
            filters.put("starName", starList);
            return filters;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String key) {
        try {
            // 1.准备请求
            SearchRequest request = new SearchRequest("hotel");
            // 2.请求参数
            request.source().suggest(new SuggestBuilder()
                    .addSuggestion(
                            "hotelSuggest",
                            SuggestBuilders
                                    .completionSuggestion("suggestion")
                                    .size(10)
                                    .skipDuplicates(true)
                                    .prefix(key)
                    ));
            // 3.发出请求
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            // 4.解析
            Suggest suggest = response.getSuggest();
            // 4.1.根据名称获取结果
            CompletionSuggestion suggestion = suggest.getSuggestion("hotelSuggest");
            // 4.2.获取options
            List<String> list = new ArrayList<>();
            for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
                // 4.3.获取补全的结果
                String str = option.getText().toString();
                // 4.4.放入集合
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
