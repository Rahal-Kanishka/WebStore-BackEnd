package com.store.demo.controllers;

import com.store.demo.entyties.Product;
import com.store.demo.repositories.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private ProductRepository productRepository ;

    OrderController(ProductRepository repository){
        this.productRepository = repository;
    }

    @GetMapping("/price")
    public String getPrice() {
        return "FOC";
    }

    @GetMapping("/products")
    public List<Product> getProducts(){
        return this.productRepository.findAll();
    }
}
