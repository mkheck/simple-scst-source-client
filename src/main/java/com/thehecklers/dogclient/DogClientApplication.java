package com.thehecklers.dogclient;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class DogClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DogClientApplication.class, args);
	}

}

@RestController
@RequestMapping("/dog")
@EnableBinding(Source.class)
@AllArgsConstructor
class DogController {
	private final Source source;

	@PostMapping("/{type}")
	void postPuppy(@PathVariable String type) {
		Dog newDog = new Dog(type);
		System.out.println(newDog);
		source.output().send(MessageBuilder.withPayload(newDog).build());
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