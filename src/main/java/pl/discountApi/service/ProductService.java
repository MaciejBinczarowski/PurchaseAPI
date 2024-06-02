package pl.discountApi.service;

import pl.discountApi.model.Product;
import pl.discountApi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService 
{

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) 
    {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() 
    {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) 
    {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product productDetails) 
    {
        if (!productRepository.existsById(id)) 
        {
            createProduct(productDetails);
            
        }
        Product product = productRepository.findById(id).get();
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCurrency(productDetails.getCurrency());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) 
    {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) 
        {
            return;
        }
        productRepository.delete(product.get());
    }

    public Optional<Product> getProductByName(String name) 
    {
        return productRepository.findByName(name);
    }
}
