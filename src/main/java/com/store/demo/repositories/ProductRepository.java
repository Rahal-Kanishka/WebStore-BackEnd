package com.store.demo.repositories;

import com.store.demo.entyties.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
