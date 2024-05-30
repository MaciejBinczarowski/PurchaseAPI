package pl.discountApi.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.service.ProductService;
import pl.discountApi.service.PromoCodeService;
import pl.discountApi.service.PurchaseService;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/basket")
public class PurchaseController 
{
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PromoCodeService promoCodeService;

    @GetMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateDiscountPrice(@RequestParam String productName, @RequestParam String promoCode)
    {
        // get promoDetails and check if promo code exists otherwise throw exception
        PromoCode promocodeDetails = promoCodeService.getPromoCode(promoCode).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promo code not found"));

        // get product details and check if product exists otherwise throw exception
        Product product = productService.getProductByName(productName).orElseThrow(() -> new RuntimeException("Product not found"));

        HttpHeaders headers = new HttpHeaders();

        // check if promo code is expired
        if(promoCodeService.isPromoCodeExpired(promoCode))
        {
            // warning: "Promo code expired" 
            headers.add("Warning", "Promo code expired.");
            return new ResponseEntity<>(product.getPrice(), headers, HttpStatus.OK);
        }
        // check if currency matches       
        else if(!promocodeDetails.getCurrency().equals(product.getCurrency()))
        {
            // warning: "Currency mismatch" 
            headers.add("Warning", "Currency mismatch.");
            return new ResponseEntity<>(product.getPrice(), headers, HttpStatus.OK);
        }
        else if (promocodeDetails.getUsageLimit() <= promocodeDetails.getUsageCount())
        {
            // warning: "Promo code usage limit reached"
            headers.add("Warning", "Promo code usage limit reached.");
            return new ResponseEntity<>(product.getPrice(), headers, HttpStatus.OK);
        }
        else
        {
            // calculate discount price
            BigDecimal discountPrice = purchaseService.calculateDiscountPrice(productName, promoCode).orElseThrow(() -> new RuntimeException("Error while calculating discount price"));
            return new ResponseEntity<>(discountPrice, headers, HttpStatus.OK);
        }
    }
}