package unidue.ub.stockanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;


@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class StockanalyzerApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(StockanalyzerApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		http.authorizeRequests()
				.anyRequest().hasIpAddress("127.0.0.1").anyRequest().permitAll().and()
				.authorizeRequests()
				.anyRequest().authenticated().anyRequest().permitAll();
	}
}
