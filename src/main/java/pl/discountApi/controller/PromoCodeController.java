package pl.discountApi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.discountApi.service.PromoCodeService;
import pl.discountApi.model.PromoCode;

@RestController
@RequestMapping("/api/promo-codes")
public class PromoCodeController 
{
    @Autowired
    private PromoCodeService promoCodeService;

@PostMapping
    public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCode promocode)
    {
        String code = promocode.getCode();

        // check if promo code already exists
        if (promoCodeService.getPromoCode(code).isEmpty()) 
        {
            // check if promo code is valid
            if (code.length() >= 3 && 
                code.length() <= 24 && promoCodeService.containsInvalidCharacters(code)  && 
                promocode.getExpirationDate().isAfter(LocalDate.now())                   &&
                promocode.getDiscount().compareTo(BigDecimal.ZERO) > 0                   &&
                promocode.getUsageLimit() > 0                                            &&
                promocode.getUsageCount() < promocode.getUsageLimit()                    &&
                promocode.getUsageCount() >= 0                                           &&
                (
                    "percentage".equals(promocode.getType()) || 
                    "normal".equals(promocode.getType())
                )                                                                        &&
                (
                    "percentage".equals(promocode.getType())                             &&
                    promocode.getDiscount().compareTo(BigDecimal.valueOf(100)) <= 0
                )


                ) 
            {
                PromoCode createdPromocode = promoCodeService.createPromoCode(promocode);
                return new ResponseEntity<>(createdPromocode, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(promocode, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(promocode, HttpStatus.CONFLICT);
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllPromoCodes()
    {
        List<String> promoCodes = promoCodeService.getAllPromoCodes();
        return ResponseEntity.ok(promoCodes);
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCode> getPromoCode(@PathVariable String code)
    {
        PromoCode promoCode = promoCodeService.getPromoCode(code).orElseThrow(() -> new RuntimeException("Promo code not found"));
        return ResponseEntity.ok(promoCode);
    }
}
