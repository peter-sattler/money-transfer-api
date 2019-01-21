# Revolut Money Transfer 2019

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Explicit requirements:

1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on the behalf of end users.
4. You can use frameworks/libraries if you like (except for Spring), but don't forget about 
requirement #2 - keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

## Implicit requirements:

1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

## Assumptions

1. There is only one bank. (Revolut of course!!!) ;)
2. There is only a single generic account type; no checking account, savings account, etc.
3. Transactions always clear immediately.
4. All currency is US Dollars (USD).

Pete Sattler   
21 January 2019  
_peter@sattler22.net_  
