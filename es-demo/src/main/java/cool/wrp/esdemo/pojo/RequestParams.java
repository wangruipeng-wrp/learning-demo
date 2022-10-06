package cool.wrp.esdemo.pojo;

import lombok.Data;

/**
 * @author 码小瑞
 */
@Data
public class RequestParams {

    private String key;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String brand;
    private String city;
    private String starName;
    private Integer minPrice;
    private Integer maxPrice;
    private String location;
}
