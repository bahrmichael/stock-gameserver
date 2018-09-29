# Changelog

## 2018-09-29

**Breaking change**

The following routes' paths were updated.

* `/api/v1/isalive` --> `/isalive/` 
* `/api/v1/quote/{symbol}` --> `/api/v1/quote/{symbol}/`
* `/api/v1/account/{name}` --> `/api/v1/account/{name}/`
* `/api/v1/balance/{userId}` --> `/api/v1/balance/`
* `/api/v1/transactions/{userId` --> `/api/v1/transactions/`
* `/api/v1/transactions/{userId}/{symbol}` --> `/api/v1/transactions/{symbol}/`
* `/api/v1/order/{userId}` --> `/api/v1/order/`
* `/api/v1/analyses` --> `/api/v1/analyses/`

## 2018-06-29

All routes were extended with the `v1` version to enable later versioning of routes.

**NEW**

* `/api/v1/isalive`
* `/api/v1/quote/{symbol}`
* `/api/v1/account/{name}`
* `/api/v1/balance/{userId}`
* `/api/v1/transactions/{userId`
* `/api/v1/transactions/{userId}/{symbol}`
* `/api/v1/order/{userId}`
* `/api/v1/analyses`
