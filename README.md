# Money Transfer REST API 2019
![Money Stack](https://github.com/peter-sattler/money-transfer/blob/master/img/money-stack.gif)

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Simplifications

:moneybag: There is only one bank; _Pete's World Banking Empire_. Since there's only one, might as well strive for world domination.  
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
:moneybag: Made use of Jersey's HK2 dependency injection framework  

## The World Domination API

Action              | Verb   | Resource Locator (URL)                                    | JSON Payload                    | Status Codes
:-----------------  |:------ | :-------------------------------------------------------- | :------------------------------ | :-------
Fetch bank details  | GET    | http://localhost:8080/api/money-transfer/bank             |                                 | 200 (Success)
Fetch all customers | GET    | http://localhost:8080/api/money-transfer/customers        |                                 | 200 (Success)
Fetch one customer  | GET    | http://localhost:8080/api/money-transfer/customer/{id}    |                                 | 200 (Success)<br>404 (Customer not found)
Add a customer      | POST   | http://localhost:8080/api/money-transfer/customer         | {<br>"id": 1,<br>"firstName": "Barb",<br>"lastName": "Wire",<br>"gender": "FEMALE",<br>"address": {<br>"street": "55 Water St",<br>"city": "New York",<br>"state": "NY",<br>"zip": 10004<br>},<br>"phone": "(212) 623-5089",<br>"email": "barb.wire@fences.cow",<br>"birthDate": "1963-10-28"<br>}      | 201 (Success)<br>409 (Customer exists)
Delete a customer   | DELETE | http://localhost:8080/api/money-transfer/customer/{id}    |                                 | 204 (Success)<br>404 (Customer not found)
Add an account      | POST   | http://localhost:8080/api/money-transfer/account          | {<br>"customerId": 1,<br>"number": 123,<br>"balance": 100.25<br>} | 201 (Success)<br>404 (Customer not found)<br>409 (Account exists)
Delete an account   | DELETE | http://localhost:8080/api/money-transfer/account/{customerId}/{number} |                    | 204 (Success)<br>404 (Customer or account not found)
Account transfer    | PUT    | http://localhost:8080/api/money-transfer/account/transfer | {<br>"customerId": 1,<br>"sourceNumber": 123,<br>"targetNumber": 234,<br>"amount": 50<br>}.     | 200 (Success)<br>404 (Customer, source or target account not found)<br>409 (Invalid amount)

## Given Requirements

### Explicit Requirements
 
:moneybag: Keep it simple and to the point (e.g. no need to implement any authentication).  
:moneybag: Assume the API is invoked by multiple systems and services on the behalf of end users.  
:moneybag: You can use frameworks/libraries if you like (except for Spring), but don't forget about requirement #2 - keep it simple and avoid heavy frameworks.  
:moneybag: The data store should run in-memory for the sake of this test.  
:moneybag: The final result should be executable as a stand-alone program (should not require a pre-installed container/server).  
:moneybag: Demonstrate with tests that the API works as expected.

### Implicit Requirements

:moneybag: The code produced by you is expected to be of high quality.  
:moneybag: There are no detailed requirements, use common sense.  

## [Version 0.0.2] May 2019 Enhancements
:moneybag: Send JSON payload instead of using query parameters  
:moneybag: Use account level locking  
:moneybag: Clarify behavior on account creation (JavaDoc only)  
:moneybag: Make idiomatic use of Java 8 _Optional_  
:moneybag: Use JCIP (Java Concurrency In Practice) annotations  
:moneybag: Fix CRLF  

## [Version 0.0.3] July 2019 Enhancements
:moneybag: Add integration test harness so API is fully covered  
:moneybag: Remove restricted class usage from the bootstrap utility  
:moneybag: Inject transfer service implementation using Jersey's HK2 dependency injection (DI) framework  
:moneybag: Added gender, address, phone, email, birth date, and joined date fields to Customer  
:moneybag: Added checking and savings account types  

Pete Sattler  
26 July 2019  
_peter@sattler22.net_  
