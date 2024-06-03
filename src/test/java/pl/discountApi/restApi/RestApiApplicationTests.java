package pl.discountApi.restApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.List;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import pl.discountApi.model.Product;
import pl.discountApi.model.PromoCode;
import pl.discountApi.model.Purchase;
import pl.discountApi.restApi.TestProductRepository;
import pl.discountApi.restApi.TestPromocoderepository;
import pl.discountApi.restApi.TestPurchaserepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestProductRepository productRepository;

	@Autowired
	private TestPromocoderepository promoCodeRepository;

	@Autowired
	private TestPurchaserepository purchaseRepository;

	private static RestTemplate restTemplate;

	@BeforeAll
	public static void setUp() 
	{
		restTemplate = new RestTemplate();
	}

	private String getRootUrl() 
	{
		return "http://localhost:" + port;
	}

	private String getRootUrl(String path) 
	{
		return "http://localhost:" + port + path;
	}

	@BeforeEach
	public void setUpEach() 
	{
		productRepository.deleteAll();
		promoCodeRepository.deleteAll();
		purchaseRepository.deleteAll();
	}


	/*
	 * 
	 * 
	 * Product tests
	 * 
	 */
	@Test
	public void addNormalProduct() 
	{
		Product product = new Product();
		product.setName("Test Product");
		product.setPrice(BigDecimal.valueOf(100));
		product.setCurrency("USD");
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
	}

	@Test
	public void addProductWithNegativePrice() 
	{
		Product product = new Product();
		product.setName("Test Product");
		product.setPrice(BigDecimal.valueOf(-100));
		product.setCurrency("USD");
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class));
	}

	@Test
	public void addProductWithoutName() 
	{
		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(100));
		product.setCurrency("USD");
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class));
	}

	@Test
	public void addProductWithoutCurrency() 
	{
		Product product = new Product();
		product.setName("Test Product");
		product.setPrice(BigDecimal.valueOf(100));
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class));
	}

	@Test
	public void getAllProducts() 
	{
		Product product = new Product();
		product.setName("Test Product");
		product.setPrice(BigDecimal.valueOf(100));
		product.setCurrency("USD");
		restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class);

		Product product2 = new Product();
		product2.setName("Test Product 2");
		product2.setPrice(BigDecimal.valueOf(200));
		product2.setCurrency("USD");
		restTemplate.postForEntity(getRootUrl("/api/products"), product2, Product.class);

		ResponseEntity<Product[]> response = restTemplate.getForEntity(getRootUrl("/api/products"), Product[].class);
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().length);
	}

	/*
	 * 
	 * 
	 * PromoCode tests
	 * 
	 */

	@Test
	public void addNormalPromoCode() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		ResponseEntity<PromoCode> postResponsePromo = restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class);
		assertNotNull(postResponsePromo);
		assertNotNull(postResponsePromo.getBody());
		assertEquals(HttpStatus.CREATED, postResponsePromo.getStatusCode());
	}

	@Test
	public void addPromoCodeWithInvalidCode() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TEST CODE!");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class));
	}

	@Test
	public void addPromoCodeWithInvalidDiscount() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(-10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class));
	}

	@Test
	public void addPromoCodeWithInvalidExpirationDate() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().minusDays(1));
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class));
	}

	@Test
	public void addPromoCodeWithInvalidUsageLimit() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		promoCode.setUsageLimit(0);
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class));
	}

	@Test
	public void addPromoCodeWithInvalidType() 
	{
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		promoCode.setType("invalid");
		assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class));
	}

	/*
	 * 
	 * 
	 * Purchase tests
	 * 
	 * 
	 */
	

	@Test
	public void calculateDiscountPrice() 
	{
		Product product = new Product();
		product.setName("TestProduct");
		product.setPrice(BigDecimal.valueOf(100));
		product.setCurrency("USD");
		restTemplate.postForEntity(getRootUrl("/api/products"), product, Product.class);

		PromoCode promoCode = new PromoCode();
		promoCode.setCode("TESTCODE");
		promoCode.setDiscount(BigDecimal.valueOf(10));
		promoCode.setCurrency("USD");
		promoCode.setExpirationDate(LocalDate.now().plusDays(1));
		restTemplate.postForEntity(getRootUrl("/api/promo-codes"), promoCode, PromoCode.class);

		//with invalid promo code
		assertThrows(HttpClientErrorException.class, () -> restTemplate.getForEntity(getRootUrl("/api/basket/calculate?productName=TestProduct&promoCode=INVALIDCODE"), BigDecimal.class));


		//with valid promo code
		ResponseEntity<BigDecimal> response = restTemplate.getForEntity(getRootUrl("/api/basket/calculate?productName=TestProduct&promoCode=TESTCODE"), BigDecimal.class);
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BigDecimal.valueOf(90), response.getBody().setScale(0));
	}
}
