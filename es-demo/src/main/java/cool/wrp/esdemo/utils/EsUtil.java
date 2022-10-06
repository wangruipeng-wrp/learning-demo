package cool.wrp.esdemo.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import cool.wrp.esdemo.pojo.HotelDoc;
import cool.wrp.esdemo.pojo.PageResult;
import cool.wrp.esdemo.pojo.RequestParams;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 码小瑞
 */
public class EsUtil {

    public static void hotelBasicQuery(SearchRequest request, RequestParams params) {
        // 准备 Boolean 查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键字搜索，match 查询，放到 must 中
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            // 不为空，根据关键字查询
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            // 为空，查询所有
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        // 品牌
        String brand = params.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }

        // 城市
        String city = params.getCity();
        if (StringUtils.isNotBlank(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }

        // 星级
        String starName = params.getStarName();
        if (StringUtils.isNotBlank(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }

        // 价格范围
        if (params.getMinPrice() != null || params.getMaxPrice() != null) {
            Integer minPrice = params.getMinPrice() != null ? params.getMinPrice() : 0;
            Integer maxPrice = params.getMaxPrice() != null ? params.getMaxPrice() : Integer.MAX_VALUE;
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

        // 2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                // 原始查询
                boolQuery,
                // function数组
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                // 过滤条件
                                QueryBuilders.termQuery("isAd", true),
                                // 算分函数
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )
                }
        );

        request.source().query(functionScoreQuery);
    }

    public static void buildPage(SearchRequest request, RequestParams params) {
        int page = params.getPage();
        int size = params.getSize();
        request.source().from((page - 1) * size).size(size);
    }

    public static void sortByLocation(SearchRequest request, RequestParams params) {
        // 我觉得用户输入的优先级是要高一点滴
        if (StringUtils.isNotBlank(params.getKey())) {
            return;
        }

        String location = params.getLocation();
        if (StringUtils.isBlank(location)) {
            return;
        }

        request.source().sort(SortBuilders
                .geoDistanceSort("location", new GeoPoint(location))
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS)
        );
    }

    public static PageResult handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (map != null && !map.isEmpty()) {
                // 2）根据字段名，获取高亮结果
                HighlightField highlightField = map.get("name");
                if (highlightField != null) {
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightField.getFragments()[0].toString();
                    // 4）把高亮结果放到HotelDoc中
                    hotelDoc.setName(hName);
                }
            }

            // 4.8.距离信息
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                hotelDoc.setDistance(sortValues[0]);
            }

            // 4.9.放入集合
            hotels.add(hotelDoc);
        }
        return new PageResult(total, hotels);
    }

    public static List<String> getAggregationByName(Aggregations aggregations, String aggName) {
        // 1.根据聚合名称，获取聚合结果
        Terms terms = aggregations.get(aggName);
        // 2.获取buckets
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        // 3.遍历
        List<String> list = new ArrayList<>(buckets.size());
        for (Terms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            list.add(brandName);
        }
        return list;
    }
}
