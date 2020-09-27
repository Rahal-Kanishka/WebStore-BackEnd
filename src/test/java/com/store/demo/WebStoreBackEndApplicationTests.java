package com.store.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.demo.DTO.PriceDetailDTO;
import com.store.demo.entyties.Cart;
import com.store.demo.entyties.Product;
import com.store.demo.repositories.CartRepository;
import com.store.demo.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
class WebStoreBackEndApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CartRepository cartRepository;

    @Test
    void loadProducts() throws Exception {
        Cart mockCart = new Cart();
        Product product = new Product();
        List<Product> mockProductArray = new ArrayList<>();
        product.setId(1);
        product.setName("Penguin-ears");
        Product productTwo = new Product();
        productTwo.setId(2);
        productTwo.setName("Horseshoe");
        mockProductArray.add(product);
        mockProductArray.add(productTwo);
        String productsUrl = "/order/products";

        mockCart.setId(1);
        mockCart.setCapacity(20);
        mockCart.setPrice(175);
        mockCart.setProductId(product);
        Mockito.when(productRepository.findAll()).thenReturn(mockProductArray);

        MvcResult mvcResult = mockMvc.perform(get(productsUrl)).andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(mockProductArray);
        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @Test
    void loadCarts() throws Exception {
        Cart mockCart = new Cart();
        Cart mockCartTwo = new Cart();
        Product product = new Product();
        List<Cart> mockCartArray = new ArrayList<>();
        product.setId(1);
        product.setName("Penguin-ears");
        Product productTwo = new Product();
        productTwo.setId(2);
        productTwo.setName("Horseshoe");
        String cartsUrl = "/order/carts";

        mockCart.setId(1);
        mockCart.setCapacity(20);
        mockCart.setPrice(175);
        mockCart.setProductId(product);
        mockCartTwo.setId(1);
        mockCartTwo.setCapacity(5);
        mockCartTwo.setPrice(825);
        mockCartTwo.setProductId(productTwo);
        mockCartArray.add(mockCart);
        mockCartArray.add(mockCartTwo);
        Mockito.when(cartRepository.findAll()).thenReturn(mockCartArray);

        MvcResult mvcResult = mockMvc.perform(get(cartsUrl)).andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(mockCartArray);
        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @Test
    void calculateCost() throws Exception {
        String url = "/order/calculate_cost";
        Cart mockCart = new Cart();
        Product product = new Product();
        PriceDetailDTO mockResponse = new PriceDetailDTO();
        mockResponse.setProductId(1);
        mockResponse.setNumberOfCartons(1);
        mockResponse.setTotalUnits(22);
        mockResponse.setTotalDiscount(0.0);
        mockResponse.setProductName("Penguin-ears");
        mockResponse.setTotalCost(195.8);
        mockResponse.setProductId(1);
        mockCart.setId(1);
        mockCart.setCapacity(20);
        mockCart.setPrice(175);
        product.setId(1);
        product.setName("Penguin-ears");
        mockCart.setProductId(product);
        Mockito.when(cartRepository.findCartByProductId(1)).thenReturn(mockCart);
        Mockito.when(productRepository.findById(1)).thenReturn(java.util.Optional.of(product));

        MvcResult mvcResult = mockMvc.perform(get(url).param("units", "22").param("productId", "1"))
                .andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(mockResponse);
        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

}
