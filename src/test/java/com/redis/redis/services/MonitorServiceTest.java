package com.redis.redis.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;

@ExtendWith(MockitoExtension.class)
public class MonitorServiceTest {
    @Mock
    private RedisConnectionFactory connectionFactory;

    @Mock
    private RedisConnection connection;

    @Mock
    private RedisServerCommands serverCommands;

    @InjectMocks
    private MonitorService service;

    @Test
    void shouldReturnInfo(){
        Properties props = new Properties();
        props.setProperty("redis_version", "7.0.0");

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.serverCommands()).thenReturn(serverCommands);
        when(serverCommands.info()).thenReturn(props);

        Properties result = service.getInfo();
        assertEquals("7.0.0", result.getProperty("redis_version"));
    }

    @Test
    void shouldReturnDbSize(){
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.serverCommands()).thenReturn(serverCommands);
        when(serverCommands.dbSize()).thenReturn(42L);

        Long result = service.getDbSize();

        assertEquals(42L,result);
    }

    @Test
    void shouldPingRedis(){
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");

        String result = service.ping();

        assertEquals("PONG", result);
        verify(connection).close(); //garante que fechou o recurso
    }

}