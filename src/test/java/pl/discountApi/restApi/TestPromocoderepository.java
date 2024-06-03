package pl.discountApi.restApi;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.discountApi.model.PromoCode;

public interface TestPromocoderepository  extends JpaRepository<PromoCode, Long>
{   
}
