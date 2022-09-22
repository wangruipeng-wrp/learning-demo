package cool.wrp.feignapi.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @author maxiaorui
 */
public class FeignConfig {

    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
}
