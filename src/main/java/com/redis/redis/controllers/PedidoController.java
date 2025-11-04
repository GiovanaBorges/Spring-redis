package com.redis.redis.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.redis.dto.PedidoRequestDTO;
import com.redis.redis.dto.PedidoResponseDTO;
import com.redis.redis.services.PedidoService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/pedido")
public class PedidoController {
    @Autowired private PedidoService service;

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getOrderById(@PathVariable String id) {
        PedidoResponseDTO pedidoFound = service.findByOrderId(id);
        return ResponseEntity.ok().body(pedidoFound);
    }

    @GetMapping("/")
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedido() {
        List<PedidoResponseDTO> response = service.getAllOrders();
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/save")
    public ResponseEntity<PedidoResponseDTO> savePedido(@RequestBody PedidoRequestDTO pedido) {
        PedidoResponseDTO response = service.savePedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> deletePedido(@PathVariable String id){
        PedidoResponseDTO response = service.deletePedido(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/bydate")
    public ResponseEntity<PedidoResponseDTO> getOrderByDate(@RequestParam 
    @DateTimeFormat(pattern = "dd/MM/yyyy")  LocalDate data) {
        PedidoResponseDTO response = service.getOrderByDate(data);
        return ResponseEntity.ok().body(response);
    }
    
    
    
}
