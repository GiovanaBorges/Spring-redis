package com.redis.redis.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RateLimiterService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    // metodo principal : verifica se o ip ainda pode fazer requisição
    public boolean isAllowed(HttpServletRequest request){
        
        String ip = getClientIp(request);
        String key = "rate:" + ip;
        Long count = redisTemplate.opsForValue().increment(key);

        if(count == 1) redisTemplate.expire(key, Duration.ofMinutes(1));
        return count <= 5; // max 5 requistions for minute
    }

    // metodo auxiliar para detectar o ip do cliente
    public String getClientIp(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isEmpty()){
            return forwardedFor.split(",")[0].trim(); // pega o primeiro ip
        }
        return request.getRemoteAddr();
    }
}
