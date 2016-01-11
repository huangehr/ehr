package service.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import service.model.Movie;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("movie")
public interface MovieClient {

    @RequestMapping(value = "/hello", method = GET)
    Movie helloWorld();

}
