package cn.guet.competition_cert_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.guet.competition_cert_system.mapper")
public class CompetitionCertSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompetitionCertSystemApplication.class, args);
	}

}
