package com.redis.redis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.redis.services.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.redis.redis.dto.UserRequestDTO;
import com.redis.redis.dto.UserResponseDTO;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable String id) {
        UserResponseDTO response = service.findUserById(id);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/save")
    public ResponseEntity<UserResponseDTO> saveUser(@RequestBody UserRequestDTO user){
        UserResponseDTO response = service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> usersFound = service.getAllUsers();
        return ResponseEntity.ok(usersFound);
    }

    @DeleteMapping()
    public ResponseEntity<UserResponseDTO> deleteUser(String id){
        UserResponseDTO userDeleted = service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDeleted);
    }
    
    
}
