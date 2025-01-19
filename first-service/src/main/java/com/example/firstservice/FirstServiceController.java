package com.example.firstservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first-service/")
public class FirstServiceController {

    private final Environment environment;

    public FirstServiceController(Environment environment) {
        this.environment = environment;
    }
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service.";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        System.out.println(header);
        return "Hello world in First Service";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        System.out.println("Server port: " + request.getServerPort());
        return String.format("Hi, there. This is a message from First Service on PORT %s", environment.getProperty("local.server.port"));
    }
}
