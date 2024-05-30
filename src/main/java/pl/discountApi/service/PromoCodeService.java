package pl.discountApi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.discountApi.repository.PromoCodeRepository;
import pl.discountApi.model.PromoCode;

@Service
public class PromoCodeService 
{
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    
    // create new promo code
    public PromoCode createPromoCode(PromoCode promoCode) 
    {
        return promoCodeRepository.save(promoCode);
    }

    // get all promo codes
    public List<String> getAllPromoCodes() 
    {
        List<PromoCode> promoCodes = promoCodeRepository.findAll();
        List<String> promoCodesList = promoCodes.stream().map(promoCode -> promoCode.getCode()).toList();
        return promoCodesList;

    }

    // get promo code details by code
    public Optional<PromoCode> getPromoCode(String code) 
    {
        return promoCodeRepository.findByCode(code);
    }

    //check if promo code is expired
    public Boolean isPromoCodeExpired(String code) 
    {
        PromoCode promoCode = getPromoCode(code).orElseThrow(() -> new RuntimeException("Promo code not found"));
        return promoCode.getExpirationDate().isBefore(java.time.LocalDate.now());
    }

}
