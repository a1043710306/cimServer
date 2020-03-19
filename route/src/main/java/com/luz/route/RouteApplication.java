package com.luz.route;

import com.luz.route.enetity.Node;
import com.luz.route.utils.IpMapUtils;
import com.luz.route.utils.SpringUtils;
import com.luz.route.utils.ZkitUtils;
import com.luz.route.zk.ZkServicer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.List;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.luz.route.dao")
public class RouteApplication {
    private Logger LOGGER = LoggerFactory.getLogger(RouteApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
        ZkServicer.getRoot();
    }

}
