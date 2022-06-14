### Created three microservices specific to Accounts, Loans, and Cards

Description:
* This repository has three maven projects with the names of accounts, loans, and cards.
* These three projects act as microservices and are built by taking XiaohanBank as an example Bank application.
* The URLs of H2 databases of all the three microservices are http://localhost:8080/h2-console/, http://localhost:8090/h2-console/, and http://localhost:9000/h2-console/ respectively.
* Invoke the REST APIs http://localhost:8080/myAccount, http://localhost:8090/myLoans, and http://localhost:9000/myCards through Postman by passing the below request in JSON format. Response from the corresponding microservices should be received.
```
{
    "customerId": 1
}
```
