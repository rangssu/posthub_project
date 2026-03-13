package com.posthub;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {

    @GetMapping("/health")
    public String checkHealth() {
        return "hello";
    }
}
