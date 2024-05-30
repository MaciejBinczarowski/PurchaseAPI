package pl.discountApi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        PromoCode createdPromocode = promoCodeService.createPromoCode(promocode);
        return ResponseEntity.ok(createdPromocode);
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
