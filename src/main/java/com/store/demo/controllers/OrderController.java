package com.store.demo.controllers;

import com.store.demo.entyties.Cart;
import com.store.demo.entyties.Product;
import com.store.demo.repositories.CartRepository;
import com.store.demo.repositories.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private ProductRepository productRepository;
    private CartRepository cartRepository;

    OrderController(ProductRepository repository, CartRepository cartRepository) {
        this.productRepository = repository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/price")
    public String getPrice() {
        return "FOC";
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return this.productRepository.findAll();
    }

    @GetMapping("/calculate_cost")
    @ResponseBody
    public Double calculateTotalCost(@RequestParam(name = "units") int numberOfUnits, @RequestParam(name = "productId") int productId) {
        return this.calculatePrice(numberOfUnits, productId);
    }

    /**
     * calculate total price forgiven product type
     *
     * @param numberOfUnits units to be purchased
     * @param productId     product id which the unit is belonging to
     * @return total cost after discounts are applied
     */
    private double calculatePrice(int numberOfUnits, int productId) {

        // get cart size
        List<Cart> carts = this.cartRepository.findCartsByProductId(productId);
        double totalCost = 0;

        if (carts != null && carts.size() > 0) {
            for (Cart cart : carts) {
                if (cart != null) {
                    System.out.println("cart found: " + cart.getId() + " capacity: " + cart.getCapacity() + " price: " + cart.getPrice());
                    // get carts need
                    int numberOfCarts = numberOfUnits / cart.getCapacity();
                    // left over units
                    int numberOfLeftOverUnits = numberOfUnits % cart.getCapacity();
                    //cost calculation
                    if (numberOfLeftOverUnits > 0) {
                        // per-unit cost
                        double perUnitCost = cart.getPrice() / cart.getCapacity();
                        totalCost = (perUnitCost * 1.3 * numberOfLeftOverUnits) +
                                (numberOfCarts >= 3 ? (numberOfCarts * cart.getPrice() * 0.9) : numberOfCarts * cart.getPrice());
                    } else {
                        totalCost = (numberOfCarts >= 3 ? (numberOfCarts * cart.getPrice() * 0.9) : numberOfCarts * cart.getPrice());
                    }
                }
            }
        } else {
            System.out.println("Could not find a cart for productId: " + productId);
        }
        System.out.println("totalCost: " + totalCost);
        return totalCost;
    }
}
