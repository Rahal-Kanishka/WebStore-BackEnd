package com.store.demo.controllers;

import com.store.demo.DTO.PriceDetailDTO;
import com.store.demo.entyties.Cart;
import com.store.demo.entyties.Product;
import com.store.demo.repositories.CartRepository;
import com.store.demo.repositories.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/order")
public class OrderController {

    private ProductRepository productRepository;
    private CartRepository cartRepository;

    OrderController(ProductRepository repository, CartRepository cartRepository) {
        this.productRepository = repository;
        this.cartRepository = cartRepository;
    }

    /**
     * get price detials for all the products available
     *
     * @return
     */
    @GetMapping("/price_details")
    public List<PriceDetailDTO> getPrice() {
        List<Product> products = this.productRepository.findAll();
        List<PriceDetailDTO> priceList = new ArrayList<>();
        if (products != null && products.size() > 0) {
            for (Product product : products) {
                PriceDetailDTO priceDetailDTO = new PriceDetailDTO();
                priceDetailDTO = this.calculatePrice(150, product.getId());
                priceList.add(priceDetailDTO);
            }
        }
        return priceList;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return this.productRepository.findAll();
    }

    @GetMapping("/calculate_cost")
    @ResponseBody
    public PriceDetailDTO calculateTotalCost(@RequestParam(name = "units") String numberOfUnits, @RequestParam(name = "productId") String productId) {
        return this.calculatePrice(Integer.parseInt(numberOfUnits), Integer.parseInt(productId));
    }

    @GetMapping("/total_cost")
    @ResponseBody
    public List<PriceDetailDTO> calculateTotalCostOnAllProducts(@RequestParam(name = "products") String products,@RequestParam(name = "quantities") String units) {
        return calculateCostForMultipleProducts(products.split(","),units.split(","));
    }

    private List<PriceDetailDTO> calculateCostForMultipleProducts(String[] products, String[] units) {
        List<PriceDetailDTO> priceDetailDTOList = new ArrayList<>();
        for (int i = 0; i < products.length; i++) {
            priceDetailDTOList.add(this.calculatePrice(Integer.valueOf(units[i]),Integer.valueOf(products[i])));
        }
        return priceDetailDTOList;
    }

    /**
     * calculate total price forgiven product type
     *
     * @param numberOfUnits units to be purchased
     * @param productId     product id which the unit is belonging to
     * @return total cost after discounts are applied
     */
    private PriceDetailDTO calculatePrice(int numberOfUnits, int productId) {
        PriceDetailDTO priceDetailDTO = new PriceDetailDTO();
        // get cart size
        Cart cart = this.cartRepository.findCartByProductId(productId);
        Product product = this.productRepository.findById(productId).get();
        double totalCost = 0;
        int numberOfCarts = 0;

        if (cart != null) {
            System.out.println("cart found: " + cart.getId() + " capacity: " + cart.getCapacity() + " price: " + cart.getPrice());
            // get carts need
            numberOfCarts = numberOfUnits / cart.getCapacity();
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
            priceDetailDTO.setTotalDiscount(numberOfCarts >= 3 ? (numberOfCarts * cart.getPrice() * 0.1) : 0);
        } else {
            System.out.println("Could not find a cart for productId: " + productId);
        }
        priceDetailDTO.setProductId(productId);
        priceDetailDTO.setProductName(product.getName());
        priceDetailDTO.setTotalCost(totalCost);
        priceDetailDTO.setNumberOfCartons(numberOfCarts);
        priceDetailDTO.setTotalUnits(numberOfUnits);
        System.out.println("totalCost: " + totalCost);
        return priceDetailDTO;
    }
}
