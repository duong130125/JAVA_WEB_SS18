package ra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.entity.Product;
import ra.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findActiveProducts() {
        return productRepository.findByIsActive(true);
    }

    public Product findById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.update(product);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    public void toggleProductStatus(Long id) {
        Product product = findById(id);
        if (product != null) {
            product.setIsActive(!product.getIsActive());
            updateProduct(product);
        }
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByName(keyword);
    }

    public void updateStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            updateProduct(product);
        }
    }
}