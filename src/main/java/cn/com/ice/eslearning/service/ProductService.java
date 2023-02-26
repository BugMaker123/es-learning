package cn.com.ice.eslearning.service;

import cn.com.ice.eslearning.entity.Product;

import java.util.List;

public interface ProductService {
    String indexProduct(Product product) throws Exception;

    void deleteProduct(String productId) throws Exception;

    void updateProduct(Product product) throws Exception;

    Product getProductById(String productId) throws Exception;

    List<Product> searchProducts(String keyword) throws Exception;
}
