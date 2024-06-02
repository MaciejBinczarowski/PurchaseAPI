# Task for LAT

## Description

This is the REST API simulating purchasing with promocodes.

## Installation & run

```bash
git clone https://github.com/MaciejBinczarowski/PurchaseAPI.git

cd PurchaseApi

mvn spring-boot:run
```

## REST API endpoints

### Products

1. POST api/products - create new product

   example request body:
   ```bash
   {
      "name": "Bananna",
      "description": "Delicious fruit!",  #optional
      "price": 8.00,
      "currency": "EUR"
   }
   ```

2. GET api/products - returns list of products  
example respond:
   ```bash
   [
      {
         "id": 1,
         "name": "Apple",
         "description": null,
         "price": 10.00,
         "currency": "EUR"
      },
      {
         "id": 2,
         "name": "Bananna",
         "description": "Delicious fruit!",
         "price": 8.00,
         "currency": "EUR"
      }
   ]
   ```
3. GET api/products/{id} - get product by id
example respond:
   ```bash
         {
            "id": 1,
            "name": "Apple",
            "description": null,
            "price": 10.00,
            "currency": "EUR"
         }
   ```
3. GET api/products/{id} - update product by id

### Promocodes

1. POST api/promo-codes - create new promocode (promo types: normal, percentage)
   
   example request body:
   ```bash
   {
    "code": "BXV2OP91A",
    "expirationDate": "2024-12-31",
    "type": "percentage",  #normal or percentage
    "discount": "25",
    "currency": "EUR",
    "usageLimit" : "1",  #optional
   }
   ```

5. GET api/promo-codes - get all promocdes
   
   example respond:
   ```bash
   [
    "SUMMER2024",
    "89IUAPX320A19DFG",
    "BXV2OP91A"
   ]
   ```
6. GET api/promo-codes/{code} - get promocode details

   example respond:
   ```bash
   {
    "code": "BXV2OP91A",
    "expirationDate": "2024-12-31",
    "type": "percentage",
    "discount": "25",
    "currency": "EUR",
    "usageLimit" : "1",
    "usageCount":"0"
   }
   ```
### Purchase

1. GET api/basket/calculate?productName=&promoCode= calculate discount price

   example request:
   ``` api/basket/calculate?productName=Bananna&promoCode=BXV2OP91A  ```

   respond:
   ```bash
   6.00
   ```
8. POST api/basket/purchase?productName&promoCode= - simulate purchase (promocode is optional)

   example request: ```api/basket/purchase?productName=Bananna&promoCode=BXV2OP91A ```

   respond:
   ```bash
   {
    "id": 1,
    "purchaseDate": "2024-06-03",
    "productName": "Bananna",
    "regularPrice": 8.00,
    "currency": "EUR",
    "discountAmount": 6.00
   }
   ```
9. Get api/basket/sales-report - get sales report

   example:
   ```bash
   [
    {
        "currency": "Euro",
        "totalAmount": 32.00,
        "totalDiscount": 8.00,
        "numberOfPurchases": 5
    }
   ]
   ```
