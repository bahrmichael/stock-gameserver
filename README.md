# Stock Gameserver

Dividends are ignored.

Data provided for free by [IEX](https://iextrading.com/developer). View [IEX’s Terms of Use](https://iextrading.com/api-exhibit-a/).

## Authentication

The endpoints `/api/v1/isalive`, `/api/v1/quote` and `/api/v1/account` are not secured. All others require the userId in the path, as well as the secret via the Authorization header.

Example:
`Authorization: Secret as9-8dn8g08basnfd-asgdfsdfihsdf`

If the authentication failed, the status code `401` will be returned with an appropriate error message.

## API

The api uses the `/api/vX` versioning schema where `X` is a number. New endpoints, deprecations and removals will be announced/scheduled through the CHANGELOG.md file.
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

### GET /api/v1/balance/{userId}

Requires authentication, see chapter on Authentication.

Returns your balance.

Example:
```json
{
    "userId": "97asndyf9-sa90d7fnsad",
    "amount": 10000.0
}
```

### GET /api/v1/transactions/{userId}

Requires authentication, see chapter on Authentication.

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

### GET /api/v1/transactions/{userId}/{symbol}

Requires authentication, see chapter on Authentication.

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

### POST /api/v1/order/{userId}

Requires authentication, see chapter on Authentication.

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

