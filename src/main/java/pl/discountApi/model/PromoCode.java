package pl.discountApi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder.Default;

@Entity
@Getter
@Setter
public class PromoCode 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 24, message = "Code length must be between 3 and 24 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Code must contain only letters and numbers")
    private String code;

    @Column(nullable = false)
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    @Column(nullable = false)
    private String type = "normal";

    @Column(nullable = false)
    private BigDecimal discount;

    // change it to enum in the future
    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, columnDefinition = "int default 1")
    private int usageLimit = 1;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int usageCount;

    @PrePersist
    @PreUpdate
    public void validateDiscount() 
    {
        if (!"percentage".equals(type) && !"normal".equals(type)) 
        {
            throw new IllegalArgumentException("Type must be either 'percentage' or 'normal'");
        }

        if ("percentage".equals(type) && (discount.compareTo(BigDecimal.valueOf(100)) > 0)) 
        {
            throw new IllegalArgumentException("Discount cannot be greater than 100 when type is percentage");
        }
    }
}
