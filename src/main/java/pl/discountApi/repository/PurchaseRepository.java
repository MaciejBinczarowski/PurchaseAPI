package pl.discountApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.discountApi.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>
{
    
}
