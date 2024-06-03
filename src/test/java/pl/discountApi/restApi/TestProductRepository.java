package pl.discountApi.restApi;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.discountApi.model.Product;

public interface TestProductRepository extends JpaRepository<Product, Long>
{

    
}
