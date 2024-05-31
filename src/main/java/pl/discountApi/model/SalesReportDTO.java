package pl.discountApi.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportDTO 
{
    private String currency;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private long numberOfPurchases;

    public SalesReportDTO(String currency, BigDecimal totalAmount, BigDecimal totalDiscount, long numberOfPurchases) {
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.numberOfPurchases = numberOfPurchases;
    }
}
