package com.redis.redis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PubSubService implements MessageListener {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void publish(String message){
        redisTemplate.convertAndSend("product-updates", message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern){
        System.out.println("Mensagem recebida" + message.toString());
    }

}
