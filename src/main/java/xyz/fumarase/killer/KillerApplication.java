package xyz.fumarase.killer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author YuanTao
 */
@SpringBootApplication
@MapperScan("xyz.fumarase.killer.mapper")
public class KillerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KillerApplication.class, args);
    }

}
