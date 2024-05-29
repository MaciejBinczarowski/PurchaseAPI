package pl.discountApi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.discountApi.repository.PromoCodeRepository;
import pl.discountApi.model.PromoCode;

@Service
public class PromoCodeService 
{
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    
    public PromoCode createPromoCode(PromoCode promoCode) 
    {
        return promoCodeRepository.save(promoCode);
    }

    public List<PromoCode> getAllPromoCodes() 
    {
        return promoCodeRepository.findAll();
    }

    public PromoCode getPromoCode(String code) 
    {
        return promoCodeRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Promo code not found"));
    }

}
