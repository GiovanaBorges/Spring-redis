package com.redis.redis.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.redis.redis.services.RateLimiterService;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Aspect
public class RateLimiterAspect {
    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private HttpServletRequest request;

    @Before("@annotation(com.redis.redis.annotations.RateLimited)")
    public void checkRateLimit(){
        if(!rateLimiterService.isAllowed(request)){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Too many requests");
        }
    }
}
