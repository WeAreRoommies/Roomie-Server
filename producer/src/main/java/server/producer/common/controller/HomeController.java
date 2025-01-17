package server.producer.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/oauth")
public class HomeController {
	@GetMapping()
	@ResponseBody
	public String home(@RequestBody String token) {
		return token;
	}
}

