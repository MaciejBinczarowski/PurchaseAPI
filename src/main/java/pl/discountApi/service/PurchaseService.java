package pl.discountApi.service;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private ProductService productService;
    @Autowired
    private PromoCodeService promoCodeService;

    public Optional<BigDecimal> calculateDiscountPrice(String productName, String promoCode) 
    {
        BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")).getPrice();
        BigDecimal discount = promoCodeService.getPromoCode(promoCode).get().getDiscount();
        String discountType = promoCodeService.getPromoCode(promoCode).get().getType();

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
            return Optional.of(BigDecimal.ZERO);
        }
        return Optional.of(discountPrice);
    }

    private BigDecimal calculateDiscountPriceNormal(BigDecimal regularPrice, BigDecimal discount) 
    {
        BigDecimal discountPrice = regularPrice.subtract(discount);

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0) 
        {
            return BigDecimal.ZERO;
        }
        return discountPrice;
    }

    private BigDecimal calculateDiscountPricePercentage(BigDecimal regularPrice, BigDecimal discount) 
    {
        BigDecimal discountPrice = regularPrice.subtract(regularPrice.multiply(discount.divide(BigDecimal.valueOf(100))));

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0) 
        {
            return BigDecimal.ZERO;
        }
        return discountPrice;
    }

    public Purchase purchaseProduct(String productName, String promoCode) 
    {
        BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getPrice();
        String currency = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getCurrency();
        BigDecimal discountPrice = calculateDiscountPrice(productName, promoCode).orElseThrow(() -> new RuntimeException("Error while calculating discount price"));
        
        // update promo code usage count
        promoCodeService.updatePromoCodeUsage(promoCode, promoCodeService.getPromoCode(promoCode).get().getUsageCount() + 1);

        Purchase purchase = new Purchase(productName, regularPrice, currency, discountPrice);
        purchaseRepository.save(purchase);
        return purchase;
    }

    public Purchase purchaseProduct(String productName) 
    {
        Product product = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found"));
    
        BigDecimal regularPrice = product.getPrice();
        String currency = product.getCurrency();
        
        Purchase purchase = new Purchase(productName, regularPrice, currency, regularPrice);

        purchaseRepository.save(purchase);
    
        return purchase;
    }

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