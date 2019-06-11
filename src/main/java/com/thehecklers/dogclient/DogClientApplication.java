package com.thehecklers.dogclient;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class DogClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DogClientApplication.class, args);
	}

}

@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final PasswordEncoder pwEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	@Bean
	UserDetailsService authentication() {
		UserDetails mark = User.builder()
				.username("mark")
				.password(pwEncoder.encode("password"))
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(mark);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests().mvcMatchers(HttpMethod.POST, "/dog/add").hasRole("USER")
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.and()
				.formLogin();
	}
}

@RestController
@RequestMapping("/dog")
@EnableBinding(Source.class)
@AllArgsConstructor
class DogController {
	private final Source source;

//	@GetMapping
//	String testDog() {
//		return "Bow wow";
//	}

	@GetMapping("/get/{type}")
	private Dog getPuppy(@PathVariable String type) {
		Dog newDog = new Dog(type);
		System.out.println(newDog);
		source.output().send(MessageBuilder.withPayload(newDog).build());
		return newDog;
	}

	@PostMapping("/add")
	private Dog postPuppy(@RequestParam String dogtype) {
		Dog newDog = new Dog(dogtype);
		System.out.println(newDog);
		source.output().send(MessageBuilder.withPayload(newDog).build());
		return newDog;
	}
}

@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Dog {
	private Long id;
	@NonNull
	private String type;
}