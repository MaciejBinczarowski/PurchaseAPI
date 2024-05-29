package pl.discountApi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.discountApi.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{

}