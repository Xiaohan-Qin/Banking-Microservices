package com.xiaohan.accounts.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaohan.accounts.model.Customer;
import com.xiaohan.accounts.model.Loans;

@FeignClient("loans")
public interface LoansFeignClient {

  @RequestMapping(method = RequestMethod.POST, value = "myLoans", consumes = "application/json")
  List<Loans> getLoansDetails(@RequestBody Customer customer);
}