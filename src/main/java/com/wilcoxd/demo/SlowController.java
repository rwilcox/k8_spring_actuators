package com.wilcoxd.demo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlowController {

    @Autowired
    private MeterRegistry repo;

    @RequestMapping("/")
    public String index()  {
        try {

            Thread.sleep(2000); // ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Greetings from Spring Boot!";
    }

}