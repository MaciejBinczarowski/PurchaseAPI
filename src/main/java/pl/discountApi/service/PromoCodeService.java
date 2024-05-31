package pl.discountApi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.discountApi.repository.PromoCodeRepository;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.PromoCodeValidationException;

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

    public PromoCode updatePromoCodeUsage(PromoCode promoCode, Integer usage) 
    {
        promoCode.setUsageCount(usage);
        promoCodeRepository.save(promoCode);
        return promoCode;
    }

    //check if promo code is expired
    public Boolean isPromoCodeExpired(PromoCode promoCode) 
    {
        return promoCode.getExpirationDate().isBefore(java.time.LocalDate.now());
    }

    // validate promo code
    public void validatePromoCode(PromoCode promoCode, String productCurrency) 
    {
        if (isPromoCodeExpired(promoCode)) 
        {
            throw new PromoCodeValidationException("Promo code expired.");
        }
        if (!promoCode.getCurrency().equals(productCurrency)) 
        {
            throw new PromoCodeValidationException("Currency mismatch.");
        }
        if (promoCode.getUsageLimit() <= promoCode.getUsageCount()) 
        {
            throw new PromoCodeValidationException("Promo code usage limit reached.");
        }
    }

}
