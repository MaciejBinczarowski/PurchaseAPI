package pl.discountApi.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.Purchase;
import pl.discountApi.repository.PurchaseRepository;
import pl.discountApi.service.ProductService;
import pl.discountApi.service.PromoCodeService;

@Service
public class PurchaseService 
{
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PromoCodeService promoCodeService;

    public BigDecimal calculateDiscountPrice(String productName, String promoCode) 
    {
        if (productName == null) 
        {
            throw new RuntimeException("Product name is required");
        }

        if (promoCode == null) 
        {
            throw new RuntimeException("Promo code is required");
        }

        Product product = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found"));
        PromoCode promoCodeDetails = promoCodeService.getPromoCode(promoCode);

        if (promoCodeDetails == null) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Promo code not found");
        }

        if (promoCodeDetails.getCurrency().equals(product.getCurrency())) 
        {
            return product.getPrice().subtract(promoCodeDetails.getDiscount());
        } 
        else 
        {
            throw new RuntimeException("Currency mismatch");
        }
    }

    public void purchaseProduct(String productName, String promoCode) 
    {
        BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getPrice();
        String currency = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getCurrency();
        BigDecimal discountPrice = calculateDiscountPrice(productName, promoCode);
        Purchase purchase = new Purchase(productName, regularPrice, currency, discountPrice);
        purchaseRepository.save(purchase);
    }

    public void purchaseProduct(String productName) 
    {
        BigDecimal regularPrice = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getPrice();
        String currency = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found")).getCurrency();
        Purchase purchase = new Purchase(productName, regularPrice, currency, regularPrice);
        purchaseRepository.save(purchase);
    }
}