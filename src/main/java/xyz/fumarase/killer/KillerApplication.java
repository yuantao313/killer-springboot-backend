package xyz.fumarase.killer;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author YuanTao
 */
@SpringBootApplication
@EnableSwagger2
@MapperScan("xyz.fumarase.killer.mapper")
public class KillerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KillerApplication.class, args);
    }

}
