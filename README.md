# Revolut Money Transfer 2019
![Money Stack](https://github.com/peter-sattler/money-transfer/blob/master/img/money-stack.gif)

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Simplifications

:moneybag: There is only one bank: _Revolut World Banking Empire_. Since there's only one, might as well strive for world domination.  
:moneybag: There is only a single generic account type; no checking account, savings account, etc.  
:moneybag: Customers are allowed to open a new account with a zero balance.  
:moneybag: Transfers can only happen between accounts owned by the same customer.  
:moneybag: Transactions always clear immediately.  
:moneybag: All currency is in US Dollars (USD). 

## Getting Started

These instructions will get you a copy of this project up and running on your local machine. Once the REST service is running, see the [API section](#the-world-domination-api) for the available options.

```text
git clone https://github.com/peter-sattler/money-transfer
cd money-transfer
gradlew run
```

## Implementation Details

:moneybag: Implemented using Java API for RESTful Web Services (JAX-RS, defined in JSR 370)  
:moneybag: The bootstrap utility uses a restricted class from the _com.sun_ package. Normally, this is not recommended, but in the spirit of keeping this project simple and to the point, a compromise was made. As a result, you may need to adjust your integrated development environment (IDE) accordingly. Please see this [Stack Overflow](https://stackoverflow.com/questions/41099332/java-httpserver-error-access-restriction-the-type-httpserver-is-not-api) post for additional details.

## The World Domination API

Action              | Verb   | Resource Locator (URL)                                    | JSON Payload               | Status Codes
:-----------------  |:------ | :-------------------------------------------------------- | :------------------------- | :------------
Fetch bank details  | GET    | http://localhost:8080/api/money-transfer/bank             |                            | 200 (Success)
Fetch all customers | GET    | http://localhost:8080/api/money-transfer/customers        |                            | 200 (Success)
Fetch one customer  | GET    | http://localhost:8080/api/money-transfer/customer/{id}    |                            | 200 (Success)<br/>404 (Customer not found)
Add a customer      | POST   | http://localhost:8080/api/money-transfer/customer         | {<br/>"id":1,<br/>"firstName":"Barb",<br/>"lastName":"Wire"<br/>} | 201 (Success)<br/>409 (Customer exists)
Delete a customer   | DELETE | http://localhost:8080/api/money-transfer/customer/{id}    |                            | 204 (Success)<br/>404 (Customer not found)
Add an account      | POST   | http://localhost:8080/api/money-transfer/account          | {<br/>"customerId":1,<br/>"number":123,<br/>"balance":100.25<br/>} | 201 (Success)<br/>404 (Customer not found)<br/>409 (Account exists)
Delete an account   | DELETE | http://localhost:8080/api/money-transfer/account/{customerId}/{number} |               | 204 (Success)<br/>404 (Customer or account not found)
Account transfer    | PUT    | http://localhost:8080/api/money-transfer/account/transfer | {<br/>"customerId":1,<br/>"sourceNumber":123,<br/>"targetNumber":234,<br/>"amount":50<br/>} | 200 (Success)<br/>404 (Customer, source or target account not found)<br/>409 (Invalid amount)

## Given Requirements

### Explicit Requirements

:moneybag: You can use Java, Scala or Kotlin.  
:moneybag: Keep it simple and to the point (e.g. no need to implement any authentication).  
:moneybag: Assume the API is invoked by multiple systems and services on the behalf of end users.  
:moneybag: You can use frameworks/libraries if you like (except for Spring), but don't forget about requirement #2 - keep it simple and avoid heavy frameworks.  
:moneybag: The datastore should run in-memory for the sake of this test.  
:moneybag: The final result should be executable as a standalone program (should not require a pre-installed container/server).  
:moneybag: Demonstrate with tests that the API works as expected.

### Implicit Requirements

:moneybag: The code produced by you is expected to be of high quality.  
:moneybag: There are no detailed requirements, use common sense.

## May 2019 Enhancements (0.0.2-SNAPSHOT)
:moneybag: Send JSON payload instead of using query parameters  
:moneybag: Use account level locking  
:moneybag: Clarify behavior on account creation (JavaDoc only)  
:moneybag: Make idiomatic use of Java 8 _Optional_  
:moneybag: Use JCIP (Java Concurrency In Practice) annotations  
:moneybag: Fix CRLF  

## July 2019 Enhancements (0.0.3-SNAPSHOT)
:moneybag: Add integration test harness so API is covered  
:moneybag: Load data upon start-up  

Pete Sattler   
4 July 2019  
_peter@sattler22.net_  
