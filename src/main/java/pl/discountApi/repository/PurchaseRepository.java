package pl.discountApi.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.discountApi.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>
{
    public List<Purchase> findByCurrency(String currency);
    
    @Query("SELECT DISTINCT currency FROM Purchase")
    public List<String> findDistinctCurrency();

    @Query("SELECT SUM(regularPrice) FROM Purchase WHERE currency = ?1")
    public BigDecimal sumRegularPriceByCurrency(String currency);

    @Query("SELECT SUM(discountAmount) FROM Purchase WHERE currency = ?1")
    public BigDecimal sumDiscountAmountByCurrency(String currency);

    @Query("SELECT COUNT(id) FROM Purchase WHERE currency = ?1")
    public Long countPurchasesByCurrency(String currency);
}
