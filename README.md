# Revolut Money Transfer 2019

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Simplifications and Assumptions:

1. There is only one bank: _Revolut World Banking Empire_ 
2. There is only a single generic account type; no checking account, savings account, etc.
3. Customers are allowed to open a new account with a zero balance.
4. Transactions always clear immediately.
5. All currency is in US Dollars (USD).
6. Implemented using Java API for RESTful Web Services (JAX-RS, defined in JSR 370)
7. The bootstrap utility uses a restricted class from the _com.sun_ package. Normally, this is not recommended, but in the spirit of keeping this project simple and to the point, a compromise was made. As a result, you may need to adjust your integrated development environment (IDE) accordingly. Please see this [Stack Overflow](https://stackoverflow.com/questions/41099332/java-httpserver-error-access-restriction-the-type-httpserver-is-not-api) post for additional details.

## The World Domination API

Action              | Verb   | Resource Locator (URL)                                               | Status Code(s)
:-----              |:------ | :------------------------------------------------------------------  | :-------------
Fetch bank details  | GET    | http://localhost:8080/api/money-transfer/bank                        | 200 (Success)
Add a customer      | POST   | http://localhost:8080/api/money-transfer/customer/{id}/{firstName}/{lastName} | 200 (Success)<br/>409 (Already exists)
Delete a customer   | DELETE | http://localhost:8080/api/money-transfer/customer/{id}               | 204 (Success)<br/>404 (Does not exist)
Add an account      | POST   | http://localhost:8080/api/money-transfer/account?number={number]&customerId={customerId}&balance={balance} | 200 (Success)<br/>409 (Already exists)

## Explicit Requirements:

1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on the behalf of end users.
4. You can use frameworks/libraries if you like (except for Spring), but don't forget about 
requirement #2 - keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

## Implicit Requirements:

1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

Pete Sattler   
25 January 2019  
_peter@sattler22.net_  
