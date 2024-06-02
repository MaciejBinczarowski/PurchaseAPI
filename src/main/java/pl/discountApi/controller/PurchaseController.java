package pl.discountApi.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.PromoCodeValidationException;
import pl.discountApi.model.Purchase;
import pl.discountApi.model.SalesReportDTO;
import pl.discountApi.service.ProductService;
import pl.discountApi.service.PromoCodeService;
import pl.discountApi.service.PurchaseService;

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
        // get product details and check if product exists otherwise throw exception
        Optional<Product> productOptional = productService.getProductByName(productName);

        if (productOptional.isEmpty()) 
        {
            return new ResponseEntity<>( BigDecimal.valueOf(-1),HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();

        // get promoDetails and check if promo code exists otherwise throw exception
        Optional<PromoCode> promoCodeOptional = promoCodeService.getPromoCode(promoCode);

        if (promoCodeOptional.isEmpty()) 
        {
            return new ResponseEntity<>(BigDecimal.valueOf(-1),HttpStatus.NOT_FOUND);
        }
        PromoCode promocodeDetails = promoCodeOptional.get();

        HttpHeaders headers = new HttpHeaders();
        
        try 
        {
            // validate promo code
            promoCodeService.validatePromoCode(promocodeDetails, product.getCurrency());
            
            // calculate discount price
            BigDecimal discountPrice = purchaseService.calculateDiscountPrice(product, promocodeDetails);
            
            return new ResponseEntity<>(discountPrice, headers, HttpStatus.OK);
        } 
        catch (PromoCodeValidationException ex) 
        {
            headers.add("Warning", ex.getMessage());
            return new ResponseEntity<>(product.getPrice(), headers, HttpStatus.OK);
        }
    }

    @PostMapping("/purchase")
    public ResponseEntity<Purchase> purchaseProduct(@RequestParam String productName, @RequestParam Optional<String> promoCode)
    {
        // get product details and check if product exists otherwise throw exception
        Optional<Product> productOptional = productService.getProductByName(productName);

        if (productOptional.isEmpty()) 
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();

        // get promoDetails and check if promo code exists otherwise throw exception
        Optional<PromoCode> promoCodeOptional = promoCodeService.getPromoCode(promoCode.get());

        if (promoCodeOptional.isEmpty()) 
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        PromoCode promocodeDetails = promoCodeOptional.get();

        HttpHeaders headers = new HttpHeaders();
        
        if (promoCode.isPresent()) 
        {
            try
            {
                 // validate promo code
                promoCodeService.validatePromoCode(promocodeDetails, product.getCurrency());
                
                // purchase product with promo code
                Purchase purchase = purchaseService.purchaseProduct(product, promocodeDetails);
                return new ResponseEntity<>(purchase, HttpStatus.OK);
            } 
            catch (PromoCodeValidationException ex) 
            {
                // return product price if promo code is invalid
                headers.add("Warning", ex.getMessage());
                Purchase purchase = new Purchase(productName, product.getPrice(), product.getCurrency(), BigDecimal.ZERO);
                return new ResponseEntity<>(purchase, headers, HttpStatus.OK);
            }
        } 
        else 
        {
            // purchase product without promo code
            Purchase purchase = purchaseService.purchaseProduct(product);
            return new ResponseEntity<>(purchase, HttpStatus.OK);
        }
    }

    @GetMapping("/sales-report")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport()
    {
        // get sales report
        return new ResponseEntity<>(purchaseService.generateSalesReport(), HttpStatus.OK);
    }
    
}
