package pl.discountApi.restApi;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.discountApi.model.Purchase;

public interface TestPurchaserepository extends JpaRepository<Purchase, Long>
{
    
}
