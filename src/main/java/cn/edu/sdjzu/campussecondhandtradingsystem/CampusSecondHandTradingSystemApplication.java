package cn.edu.sdjzu.campussecondhandtradingsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.sdjzu.campussecondhandtradingsystem.mapper")
public class CampusSecondHandTradingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSecondHandTradingSystemApplication.class, args);
    }

}
