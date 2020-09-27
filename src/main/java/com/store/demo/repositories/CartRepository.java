package com.store.demo.repositories;

import com.store.demo.entyties.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT A FROM Cart A where A.id = ?1")
    public Cart findCartByProductId(int productId);
}
