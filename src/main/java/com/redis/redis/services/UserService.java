package com.redis.redis.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.redis.redis.dto.UserRequestDTO;
import com.redis.redis.dto.UserResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.User;
import com.redis.redis.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public UserResponseDTO saveUser(UserRequestDTO userDTO){
        try{
        User userToBeSaved = User.builder()
            .name(userDTO.name())
            .email(userDTO.email())
            .endereco(userDTO.endereco())
            .pedidosId(userDTO.idpedidos())
            .build();

        User userSaved = repository.save(userToBeSaved);
        return new UserResponseDTO(
            userSaved.getId(),
            userSaved.getName(),
            userSaved.getEmail(),
            userSaved.getPedidosId(),
            userSaved.getEndereco());
        }catch(Exception e){
            throw new ApiException("Erro ao salvar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserResponseDTO findUserById(String idUser){
        Optional<User> user = repository.findById(idUser);
        if(user.isEmpty()){
            throw new ApiException("Nenhum usuario encontrado", HttpStatus.NOT_FOUND);
        }
        return new UserResponseDTO(
            user.get().getId(),
            user.get().getName(),
            user.get().getEmail(),
            user.get().getPedidosId(),
            user.get().getEndereco()
        );
    }

    public UserResponseDTO deleteUser(String id){
        Optional<User> userToBeDeleted = repository.findById(id);
        if(userToBeDeleted.isEmpty()){
            throw new ApiException("Usuario não encontrado", HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return new UserResponseDTO(
            userToBeDeleted.get().getId(),
            userToBeDeleted.get().getName(),
            userToBeDeleted.get().getEmail(),
            userToBeDeleted.get().getPedidosId(),
            userToBeDeleted.get().getEndereco());
    }

    public List<UserResponseDTO> getAllUsers(){
        List<User> users = repository.findAll();

        if(users.isEmpty()){
            throw new ApiException("Não há nenhum registro de usuario ainda...", HttpStatus.NOT_FOUND);
        }

        return users.stream()
            .map(user -> new UserResponseDTO(user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPedidosId(),
            user.getEndereco()))
            .collect(Collectors.toList());
    }

    }

