package pl.discountApi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.Purchase;
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

    // public void purchaseProduct(String productName, String promoCode) 
    // {
    //     BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getPrice();
    //     String currency = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getCurrency();
    //     BigDecimal discountPrice = calculateDiscountPrice(productName, promoCode);
    //     Purchase purchase = new Purchase(productName, regularPrice, currency, discountPrice);
    //     purchaseRepository.save(purchase);
    // }

    // public void purchaseProduct(String productName) 
    // {
    //     BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getPrice();
    //     String currency = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getCurrency();
    //     Purchase purchase = new Purchase(productName, regularPrice, currency, regularPrice);
    //     purchaseRepository.save(purchase);
    // }
}