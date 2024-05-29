package pl.discountApi.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.discountApi.model.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer>
{
    //find PromoCode by code
    Optional<PromoCode> findByCode(String code);
}
