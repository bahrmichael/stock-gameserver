# Stock Gameserver

Dividends are ignored.

Data provided for free by [IEX](https://iextrading.com/developer). View [IEX’s Terms of Use](https://iextrading.com/api-exhibit-a/).

## Authentication

To authenticate requests, you must first create an account at the `/api/v1/account` endpoint. You can create as many accounts as you want to.

Authorization HTTP header example:
* `Authorization: Basic M3JkcGFydHlfY2xpZW50aWQ6amtmb3B3a21pZjkwZTB3b21rZXBvd2U5aXJram8zcDlta2Z3ZQ==`

Basic access authentication is used, with the user ID as the username and secret as the password. The header should be the string `Basic ` plus the Base64 encoded string {user_id}:{secret}.
It is very important to make sure there is no whitespace around or in the string that is being Base64 encoded. If in doubt, try to Base64 encode the sample values provided below and compare them to the results:
* Example user ID: `3rdparty_clientid`
* Example secret: `jkfopwkmif90e0womkepowe9irkjo3p9mkfwe`
* Concatenated to become: `3rdparty_clientid:jkfopwkmif90e0womkepowe9irkjo3p9mkfwe`
* Base64 encoded to: `M3JkcGFydHlfY2xpZW50aWQ6amtmb3B3a21pZjkwZTB3b21rZXBvd2U5aXJram8zcDlta2Z3ZQ==`
* Resulting in the Authorization header: `Basic M3JkcGFydHlfY2xpZW50aWQ6amtmb3B3a21pZjkwZTB3b21rZXBvd2U5aXJram8zcDlta2Z3ZQ==`

Endpoints which require authentication are marked as such in the description below.

If the authentication failed, the status code `401` will be returned.

## API

The api uses the `/api/vX` versioning schema where `X` is a number. New versions will be created for breaking changes. New endpoints, deprecations and removals will be announced/scheduled through the CHANGELOG.md file.
There is also a `X-STOCKS-DEPRECATION: Date` header which contains the date on which the endpoint will be removed.

All endpoints return json.

### GET /api/v1/isalive

Returns a 200 if alive.

### GET /api/v1/quote/{symbol}

Returns a quote for the given symbol if the according information could be retrieved. All stocks from https://iextrading.com/apps/stocks are supported, feel free to use their API for your purposes.

Example:
```json
{
    "symbol": "AAPL",
    "latestPrice": 187.59
}
```

### POST /api/v1/account/{name}

Create an account with the given name and an initial balance of 10000. Please use your real name. Names are not unique, so you can create as many accounts as you want.

No request body.

Returns the userId and a secret that you must remember for further calls. You have to start over if you lose them.

Example:
```json
{
    "userId": "97asndyf9-sa90d7fnsad",
    "secret": "as9-8dn8g08basnfd-asgdfsdfihsdf",
    "name": "Ryan Reynolds"
}
```

_If you lose those credentials, then there is no way to retrieve them._

### GET /api/v1/balance/

**Requires authentication**, see chapter on Authentication.

Returns your balance.

Example:
```json
{
    "userId": "97asndyf9-sa90d7fnsad",
    "amount": 10000.0
}
```

### GET /api/v1/transactions/

**Requires authentication**, see chapter on Authentication.

Returns a list of all your transactions. Transactions are comleted buy or sell orders.

Example:
```json
[
    {
        "id": "0asb7dfy087asbdf808sadfbs",
        "userId": "97asndyf9-sa90d7fnsad",
        "pricePerUnit": 175.87,
        "amount": 10,
        "symbol": "AAPL",
        "isBuy": true,
        "instant": Date
    }, ...
]
```

### GET /api/v1/transactions/{symbol}

**Requires authentication**, see chapter on Authentication.

Same as above, but filtered by symbol.

Example:
```json
[
    {
        "id": "0asb7dfy087asbdf808sadfbs",
        "userId": "97asndyf9-sa90d7fnsad",
        "pricePerUnit": 175.87,
        "amount": 10,
        "symbol": "AAPL",
        "isBuy": true,
        "instant": Date
    }, ...
]
```

### POST /api/v1/order/

**Requires authentication**, see chapter on Authentication.

An order may either succeed and result in a transaction or fail with an error code and an error message. Contrary to stock exchanges, orders are processed immediately and have no delayable aspect.

Example Request:
```json
{
    "symbol": "AAPL",
    "isBuy": true,
    "amount": 10
}
```

Example Response:
```json
{
    "id": "0asb7dfy087asbdf808sadfbs",
    "userId": "97asndyf9-sa90d7fnsad",
    "pricePerUnit": 175.87,
    "amount": 10,
    "symbol": "AAPL",
    "isBuy": true,
    "instant": Date
}
```

Repeated calls will try to place multiple orders.

#### Possible Error Codes

Error messages are provided in the body.

`400`: Insufficient Funds. You don't have the required <amount>

`400`: Insufficient Shares. For the given symbol you only have <amount> shares.

`404`: The symbol <symbol> could not be found. If it's not a typo, then please report this to the admin.

### GET /api/v1/analyses

Returns a list of analyses as seen on [finanzen.net](https://www.finanzen.net/analysen/) if that stock has a symbol (open a report and look under the headline).

`v1` returns all analyses that were tracked since the app's go-live. Futures versions may have date filters.

Example Response:
```json
[
    {
        "symbol": "NKE",
        "buy": 14,
        "hold": 7,
        "sell": 1,
        "source": "https://www.finanzen.net/analyse/Nike_buy-Stifel_Nicolaus__Co__Inc__647337",
        "instant": Date
    }, ...
]
```
