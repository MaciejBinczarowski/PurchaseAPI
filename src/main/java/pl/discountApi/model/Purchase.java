package pl.discountApi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Purchase 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate purchaseDate;

    private String productName;
    private BigDecimal regularPrice;
    private String currency;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() 
    {
        purchaseDate = LocalDate.now();
    }

    public Purchase(String productName, BigDecimal regularPrice, String currency, BigDecimal discountAmount) 
    {
        this.productName = productName;
        this.regularPrice = regularPrice;
        this.currency = currency;
        this.discountAmount = discountAmount;
    }
}
