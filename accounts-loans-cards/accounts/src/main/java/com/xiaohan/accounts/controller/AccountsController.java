package com.xiaohan.accounts.controller;

import com.xiaohan.accounts.model.Cards;
import com.xiaohan.accounts.model.CustomerDetails;
import com.xiaohan.accounts.model.Loans;
import com.xiaohan.accounts.repository.AccountsRepository;
import com.xiaohan.accounts.service.client.CardsFeignClient;
import com.xiaohan.accounts.service.client.LoansFeignClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xiaohan.accounts.config.AccountsServiceConfig;
import com.xiaohan.accounts.model.Accounts;
import com.xiaohan.accounts.model.Customer;
import com.xiaohan.accounts.model.Properties;
import com.xiaohan.accounts.repository.AccountsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class AccountsController {

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  AccountsServiceConfig accountsConfig;

  @Autowired
  LoansFeignClient loansFeignClient;

  @Autowired
  CardsFeignClient cardsFeignClient;

  @PostMapping("/myAccount")
  public Accounts getAccountDetails(@RequestBody Customer customer) {

    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
    if (accounts != null) {
      return accounts;
    } else {
      return null;
    }

  }

  @GetMapping("/account/properties")
  public String getPropertyDetails() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
        accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
    String jsonStr = ow.writeValueAsString(properties);
    return jsonStr;
  }


  @PostMapping("/myCustomerDetails")
  @CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod="myCustomerDetailsFallBack")
  @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
  public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
    List<Loans> loans = loansFeignClient.getLoansDetails(customer);
    List<Cards> cards = cardsFeignClient.getCardDetails(customer);

    CustomerDetails customerDetails = new CustomerDetails();
    customerDetails.setAccounts(accounts);
    customerDetails.setLoans(loans);
    customerDetails.setCards(cards);

    return customerDetails;

  }

  private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
    List<Loans> loans = loansFeignClient.getLoansDetails(customer);
    CustomerDetails customerDetails = new CustomerDetails();
    customerDetails.setAccounts(accounts);
    customerDetails.setLoans(loans);
    return customerDetails;

  }

  @GetMapping("/sayHello")
  @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
  public String sayHello() {
    return "Hello, Welcome to XiaohanBank";
  }

  private String sayHelloFallback(Throwable t) {
    return "Hi, Welcome to XiaohanBank";
  }

}