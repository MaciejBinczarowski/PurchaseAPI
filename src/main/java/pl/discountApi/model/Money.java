package pl.discountApi.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Money 
{
    private String currency;
    private BigDecimal amount;

    public Money(String currency, BigDecimal amount) 
    {
        this.currency = currency;
        this.amount = amount;
    }

    public Boolean currencyMatches(Money money) 
    {
        return this.currency.equals(money.getCurrency());
    }

    public void sub(Money money) 
    {
        amount = amount.subtract(money.getAmount());
    }
}

