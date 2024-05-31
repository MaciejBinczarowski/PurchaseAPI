package pl.discountApi.service;

import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.Purchase;
import pl.discountApi.model.SalesReportDTO;
import pl.discountApi.repository.PurchaseRepository;

@Service
public class PurchaseService 
{
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PromoCodeService promoCodeService;

    public BigDecimal calculateDiscountPrice(Product product, PromoCode promoCode) 
    {
        BigDecimal regularPrice = product.getPrice();
        BigDecimal discount = promoCode.getDiscount();
        String discountType = promoCode.getType();

        BigDecimal discountPrice = BigDecimal.ZERO;

        if (discountType.equals("normal")) 
        {
            discountPrice = calculateDiscountPriceNormal(regularPrice, discount);
        } 
        else if (discountType.equals("percentage")) 
        {
            discountPrice = calculateDiscountPricePercentage(regularPrice, discount);
        }

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0) 
        {
            return BigDecimal.ZERO;
        }
        return discountPrice;
    }

    // calculate discount price with fixed amount
    private BigDecimal calculateDiscountPriceNormal(BigDecimal regularPrice, BigDecimal discount) 
    {
        BigDecimal discountPrice = regularPrice.subtract(discount);

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0) 
        {
            return BigDecimal.ZERO;
        }
        return discountPrice;
    }

    // calculate discount price with percentage
    private BigDecimal calculateDiscountPricePercentage(BigDecimal regularPrice, BigDecimal discount) 
    {
        BigDecimal discountPrice = regularPrice.subtract(regularPrice.multiply(discount.divide(BigDecimal.valueOf(100))));

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0) 
        {
            return BigDecimal.ZERO;
        }
        return discountPrice;
    }

    // simulate purchase with promo code
    public Purchase purchaseProduct(Product product, PromoCode promoCode) 
    {
        BigDecimal regularPrice = product.getPrice();
        String currency = product.getCurrency();
        BigDecimal discountPrice = calculateDiscountPrice(product, promoCode);
        
        // update promo code usage count
        promoCodeService.updatePromoCodeUsage(promoCode, promoCode.getUsageCount() + 1);

        Purchase purchase = new Purchase(product.getName(), regularPrice, currency, discountPrice);
        purchaseRepository.save(purchase);
        return purchase;
    }

    // simulate purchase without promo code
    public Purchase purchaseProduct(Product product) 
    {
        BigDecimal regularPrice = product.getPrice();
        String currency = product.getCurrency();
        
        Purchase purchase = new Purchase(product.getName(), regularPrice, currency, regularPrice);

        purchaseRepository.save(purchase);
    
        return purchase;
    }

    // generate sales report mentioned in the task
    public List<SalesReportDTO> generateSalesReport()
    {
        List<SalesReportDTO> salesReport = new ArrayList<>();

        purchaseRepository.findDistinctCurrency().forEach(currency -> {
            System.out.println("Currency: " + currency);

            //get sum of regular prices
            BigDecimal sumOfRegularPrices = purchaseRepository.sumRegularPriceByCurrency(currency);

            // sum of real paid value
            BigDecimal sumOfDiscountPrices = purchaseRepository.sumDiscountAmountByCurrency(currency);

            // total discount amount (e.g. regular_price = 100, discount_price = 80, discount_amount = 20)
            BigDecimal totalDiscount = sumOfRegularPrices.subtract(sumOfDiscountPrices);

            long noumberOfPurchases = purchaseRepository.countPurchasesByCurrency(currency);

            SalesReportDTO salesReportDTO = new SalesReportDTO(currency, sumOfDiscountPrices, totalDiscount, noumberOfPurchases);
            salesReport.add(salesReportDTO);
        });

        return salesReport;
    }
}