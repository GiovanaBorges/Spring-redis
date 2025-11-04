package com.redis.redis.services;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    public Properties getInfo(){
        return connectionFactory.getConnection().serverCommands().info();
    }

    public Long getDbSize(){
        return connectionFactory.getConnection().serverCommands().dbSize();
    }

    public String ping(){
        try(RedisConnection connection = connectionFactory.getConnection()){
            return connection.ping();
        }
    }
}
