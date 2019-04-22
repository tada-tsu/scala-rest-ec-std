# 1. Functional Specification

# 2. TOC

<!-- TOC -->

- [1. Functional Specification](#1-functional-specification)
- [2. TOC](#2-toc)
- [3. Resources (Models)](#3-resources-models)
    - [3.1. users](#31-users)
    - [3.2. login_tokens](#32-login_tokens)
    - [3.3. products](#33-products)
    - [3.4. product_thumbnails](#34-product_thumbnails)
    - [3.5. carts](#35-carts)
    - [3.6. orders](#36-orders)
    - [3.7. order_cart_records](#37-order_cart_records)
    - [3.8. payment](#38-payment)
    - [3.9. assets](#39-assets)
    - [3.10. settings](#310-settings)
- [4. Controllers, endpoints](#4-controllers-endpoints)
    - [4.1. User](#41-user)
        - [4.1.1. Front](#411-front)
        - [4.1.2. Admin](#412-admin)
    - [4.2. Product](#42-product)
        - [4.2.1. Front](#421-front)
        - [4.2.2. Admin](#422-admin)
    - [4.3. Cart](#43-cart)
        - [4.3.1. Front](#431-front)
    - [4.4. Order](#44-order)
        - [4.4.1. Front](#441-front)
        - [4.4.2. Admin](#442-admin)
    - [4.5. Payment](#45-payment)
        - [4.5.1. Front](#451-front)
        - [4.5.2. Admin](#452-admin)
    - [4.6. Setting](#46-setting)
        - [4.6.1. Front](#461-front)
        - [4.6.2. Admin](#462-admin)
- [5. DB](#5-db)
    - [5.1. Migrations](#51-migrations)

<!-- /TOC -->

# 3. Resources (Models)

## 3.1. users

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|name|string(32)|user name|
|screen_name|string(16)|user screen name|
|email|string|user email address|
|thumbnail_id|string|user thumbnail asset id|
|password|string(64)|user password hash?|


## 3.2. login_tokens

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|token|string|token hash|
|user_id|unsignedInteger|user id|


## 3.3. products

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|title|string|product title|
|description|text|product details explaination|
|stock|unsignedInteger||
|price|integer|product price|
|sale_price|integer|product sale price|


## 3.4. product_thumbnails

|column|type|description|
|-- |--|-- |
|id|unsignedInteger|not required|
|product_id|unsignedInteger||
|asset_id|unsignedInteger||


## 3.5. carts

|column|type|description|
|-- |--|-- |
|id|unsignedInteger|not required|
|user_id|unsignedInteger||
|product_id|unsignedInteger||
|quantity|unsignedInteger||


## 3.6. orders

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|user_id|unsignedInteger||
|status|string||
|amount|integer||


## 3.7. order_cart_records

|column|type|description|
|-- |--|-- |
|id|unsignedInteger|Not required|
|user_id|unsignedInteger||
|order_id|unsignedInteger||
|product_id|unsignedInteger||
|quantity|unsignedInteger||
|sell_price|integer||


## 3.8. payment

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|type|string||
|expired_at|timestamp||
|token|string||
|user_id|unsignedInteger||
|amount|ineger||
|order_id|unsignedInteger||


## 3.9. assets

|column|type|description|
|-- |--|--|
|id|unsignedInteger||
|path|text|path to asset|
|filename|text|file name|
|type|text|type of used file|


## 3.10. settings

|column|type|description|
|-- |--|-- |
|id|unsignedInteger||
|key|string|setting key|
|value|string|setting value|


# 4. Controllers, endpoints

--- 

## 4.1. User

### 4.1.1. Front

- /login
- /me
- /me/thumbnail
- /me/profile

### 4.1.2. Admin

- /admin/user
- /admin/user/{user_id}


--- 

## 4.2. Product

### 4.2.1. Front

- /store/product
- /store/product/{product_id}

### 4.2.2. Admin

- /admin/product
- /admin/product/{product_id}


--- 

## 4.3. Cart

### 4.3.1. Front

- /store/cart
- /store/cart/{product_id}


--- 

## 4.4. Order

### 4.4.1. Front

- /store/order
- /store/order/{order_id}

### 4.4.2. Admin

- /admin/order
- /admin/order/{order_id}


--- 

## 4.5. Payment

### 4.5.1. Front

- /payment/{order_id}

### 4.5.2. Admin

- /admin/payment
- /admin/payment/{payment_id}


--- 

## 4.6. Setting

### 4.6.1. Front

- /store/overview

### 4.6.2. Admin

- /admin/overview

--- 

# 5. DB

> [ERD](https://www.erdcloud.com/d/gMqcAxwGoTcnftwMp)

## 5.1. Migrations

- [flyway-play](https://github.com/flyway/flyway-play)


