package com.redis.redis.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String,String> valueOperations;

    @InjectMocks
    private RateLimiterService service;

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldAllowWhenUnderLimit(){
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("rate:127.0.0.1")).thenReturn(1L);
        
        boolean allowed = service.isAllowed(request);
        
        assertTrue(allowed);
        verify(redisTemplate).expire("rate:127.0.0.1", Duration.ofMinutes(1));
    }

    @Test
    void shouldBlockWhenOverLimit(){
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("rate:127.0.0.1")).thenReturn(6L);

        boolean allowed = service.isAllowed(request);
        assertFalse(allowed);
    }

    @Test
    void shouldGetClientIpFromHeader(){
        when(request.getHeader("X-Forwarded-For"))
            .thenReturn("192.168.0.1 , 10.0.0.2");
        String ip = service.getClientIp(request);
        assertEquals("129.168.0.1", ip);
    }
}