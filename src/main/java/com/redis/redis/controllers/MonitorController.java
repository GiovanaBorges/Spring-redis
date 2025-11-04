package com.redis.redis.controllers;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.redis.services.MonitorService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    private MonitorService monitorService;

    //retorna info do redis como JSON
    @GetMapping("/info")
    public ResponseEntity<Map<String,Object>> getInfo() {
        Properties props = monitorService.getInfo();
        Map<String,Object> map = props.entrySet()
            .stream()
            .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> e.getValue()));
        return ResponseEntity.ok(map);
    }

    //retorna numero de chaves no redis
    @GetMapping("/dbsize")
    public ResponseEntity<Long> getDbSize() {
        return ResponseEntity.ok(monitorService.getDbSize());
    }

    // retorna "PONG" se redis estiver vivo
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(monitorService.ping());
    }
    
    
    
}
