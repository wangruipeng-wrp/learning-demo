package cool.wrp.esdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 码小瑞
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult {

    private Long total;
    private List<HotelDoc> hotels;
}
