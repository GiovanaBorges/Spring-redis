package com.redis.redis.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import com.redis.redis.dto.UserRequestDTO;
import com.redis.redis.dto.UserResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.Endereco;
import com.redis.redis.models.User;
import com.redis.redis.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
   
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateProduct(){
        User user = new User(
            "user1",
            "user",
            "user@email.com",
            List.of(new Endereco()),
            List.of("pedido1","pedido2")
        );
        when(repository.save(any(User.class))).thenAnswer(i ->{
            User u = i.getArgument(0);
            u.setId("user1");
            return u;
        });
        UserResponseDTO response = service.saveUser(
            new UserRequestDTO(user.getName(),
                user.getEmail(), 
                user.getPedidosId(),
                user.getEndereco()));
        assertAll(
            () -> assertEquals(user.getEmail(),response.email()),
            () -> assertEquals(user.getEndereco(), response.endereco()),
            () -> assertEquals(user.getId(), response.id()),
            () -> assertEquals(user.getPedidosId(), response.idpedidos()),
            () -> assertEquals(user.getName(), response.name())
        );
    }

    @Test
    void shouldReturnErrorOnCreateUser(){
        UserRequestDTO request = new UserRequestDTO("user1",
         "useremail@email.com",
        List.of("pedido1","pedido2"),
        List.of());

        when(repository.save(any(User.class))).thenThrow(
            new RuntimeException("db falhou")
        );
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.saveUser(request);
        });

        assertAll(
            () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus()),
            () -> assertEquals("Erro ao salvar usuario", exception.getMessage())
        );
    }

    @Test
    void shouldFindUserById(){
         User user = new User(
            "user1",
            "user",
            "user@email.com",
            List.of(new Endereco()),
            List.of("pedido1","pedido2")
        );

        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        UserResponseDTO response = service.findUserById(user.getId());
        assertAll(
            () -> assertEquals(user.getId(), response.id()),
            () -> assertEquals(user.getEmail(), response.email()),
            () -> assertEquals(user.getName(), response.name()),
            () -> assertEquals(user.getEndereco(), response.endereco()),
            () -> assertEquals(user.getPedidosId(), response.idpedidos())
        );
    }

        @Test
        void shouldNotFindUserById(){
            when(repository.findById("usuarioX")).thenReturn(Optional.empty());
            ApiException exception = assertThrows(ApiException.class, () ->{
                service.findUserById("usuarioX");
            });
            assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Nenhum usuario encontrado", exception.getMessage())
            );
        }    
    @Test
    void shouldDeleteUser(){
         User user = new User(
            "user1",
            "user",
            "user@email.com",
            List.of(new Endereco()),
            List.of("pedido1","pedido2")
        );
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        UserResponseDTO response = service.deleteUser(user.getId());
        assertAll(
            () -> assertEquals(user.getEmail(), response.email()),
            () -> assertEquals(user.getEndereco(), response.endereco()),
            () -> assertEquals(user.getId(), response.id()),
            () -> assertEquals(user.getPedidosId(), response.idpedidos()),
            () -> assertEquals(user.getName(), response.name())
        );
    }

    @Test
    void shouldNotDeleteUser(){
        when(repository.findById("usuario1")).thenReturn(Optional.empty());
        
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.deleteUser("usuario1");
        });
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
            () -> assertEquals("Usuario não encontrado", exception.getMessage())
        );
    }

    @Test
    void shouldGetAllUsers(){
         User user = new User(
            "user1",
            "user",
            "user@email.com",
            List.of(new Endereco()),
            List.of("pedido1","pedido2")
        );

         User user1 = new User(
            "user2",
            "user2",
            "user2@email.com",
            List.of(new Endereco()),
            List.of("pedido3","pedido4")
        );

        when(repository.findAll()).thenReturn(List.of(user,user1));
        List<UserResponseDTO> response = service.getAllUsers();
        assertAll(
            () -> assertEquals(2, response.size()),
            () -> assertEquals("user", response.get(0).name())
        );
    }

    @Test
    void shouldNotGetAllUser(){
        when(repository.findAll()).thenReturn(List.of());

        ApiException exception = assertThrows(ApiException.class, () ->{
            service.getAllUsers();
        });
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
            () -> assertEquals("Não há nenhum registro de usuario ainda...", exception.getMessage())
        );
    }
}
