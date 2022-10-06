package cool.wrp.esdemo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * es hotel 索引实体类
 * @author 码小瑞
 */
@Data
@NoArgsConstructor
public class HotelDoc {
    private Long id;
    private String name;
    private String address;
    private Integer price;
    private Integer score;
    private String brand;
    private String city;
    private String starName;
    private String business;
    private Object distance;
    private String location;
    private String pic;
    private Boolean isAd;
    private List<String> suggestion;

    public HotelDoc(HotelEntity hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.price = hotel.getPrice();
        this.score = hotel.getScore();
        this.brand = hotel.getBrand();
        this.city = hotel.getCity();
        this.starName = hotel.getStarName();
        this.business = hotel.getBusiness();
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        this.pic = hotel.getPic();
        this.isAd = hotel.getIsAd();
        this.suggestion = Arrays.asList(
                hotel.getName(),
                hotel.getBrand(),
                hotel.getBusiness(),
                hotel.getCity(),
                hotel.getStarName());
    }
}
