package com.luz.hormone;

import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.utils.IpUtils;
import com.luz.hormone.utils.SpringUtils;
import com.luz.hormone.zookeeper.RegZk;
import com.luz.hormone.zookeeper.ZkServicer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.net.InetAddress;

@MapperScan("com.luz.hormone.dao")
@SpringBootApplication()
public class HormoneApplication  implements CommandLineRunner{
    private final static Logger LOGGER = LoggerFactory.getLogger(HormoneApplication.class);

    @Value("${mynetty.port}")
    private  int port;

    @Value("${server.port}")
    private int webServerPort;

    public static void main(String[] args) {
        SpringApplication.run(HormoneApplication.class, args);
        LOGGER.info("start HormoneApplication is ok");
    }
    public void run(String... args) throws Exception {
        ZkServicer zkServicer= SpringUtils.getObject(ZkServicer.class);
        LOGGER.info("zk starting ...");
        String addr = InetAddress.getLocalHost().getHostAddress();
        LOGGER.info("this server Address is "+addr);
        Thread thread = new Thread(new RegZk(addr, port,webServerPort));
        thread.setName("registry-zk");
        thread.start() ;
        LOGGER.info("zk start ok");

    }

}
