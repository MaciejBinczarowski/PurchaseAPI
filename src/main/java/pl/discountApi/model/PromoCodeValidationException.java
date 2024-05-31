package pl.discountApi.model;

public class PromoCodeValidationException extends RuntimeException
{
    public PromoCodeValidationException(String message) 
    {
        super(message);
    }
}
