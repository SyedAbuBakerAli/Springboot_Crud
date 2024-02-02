package com.example.sunbasedata.controller;

import com.example.sunbasedata.entity.Customer;
import com.example.sunbasedata.repository.UserRepository;
import com.example.sunbasedata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

//
//    @PostMapping("/adduser")
//    public Customer addUser(@RequestBody Customer user){
//        return service.saveUser(user);
//    }
//
//    @GetMapping("/users")
//    public List<Customer> findAllUser(){
//        return service.getCustomer();
//    }
//
//    @GetMapping("/user/{id}")
//    public Customer UserById(@PathVariable int id){
//        return service.getUserById(id);
//    }
//
//    @PutMapping("/update")
//    public Customer updateUser(@RequestBody Customer user){
//        return service.updateUser(user);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public String deleteUser(@PathVariable int id){
//        return service.deleteUser(id);
//    }

    @GetMapping("/customers")
    public ResponseEntity<Object> getCustomers() {
        // Step 1: Obtain authentication token
        String token = getAuthToken();
        if (token == null) {
            return new ResponseEntity<>("Failed to obtain authentication token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Step 2: Access API endpoint with token
        String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("cmd", "get_customer_list");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Customer[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer[].class);

        // Check if the request was successful (status code 200)
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the response and return it
            repository.saveAll(Arrays.asList(response.getBody()));

            // Return the fetched users
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        } else {
            // Return an error response if the request was not successful
            return new ResponseEntity<>("Failed to fetch customer list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getAuthToken() {
        return "dGVzdEBzdW5iYXNlZGF0YS5jb206VGVzdEAxMjM=";

    }

    //display list of customers
    @GetMapping("/")
    public String viewHomePage(Model model){
       return findPaginated(1,"firstName","asc", model);
    }

    @GetMapping("/newCustomerForm")
    public String showNewCustomerForm(Model model){
      Customer customer = new Customer();
      model.addAttribute("customer",customer);
      return "new_customer";
    }


    @PostMapping("/saveCustomer")
    public String saveCustomer(@ModelAttribute("customer") Customer customer){
        service.saveUser(customer);
        return "redirect:/";
    }

    @GetMapping("/showUpdateFrom/{id}")
    public String showUpdateForm(@PathVariable (value = "id") int id, Model model){

        Customer customer = service.getUserById(id);
        model.addAttribute("customer",customer);
        return "update_customer";

    }

    @GetMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable (value = "id") int id){
        service.deleteUser(id);
        return "redirect:/";
    }

    // /page/1?sortField=name&sortDir=asc
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField ,
                                @RequestParam("sortDir") String sortDir,Model model){
        int pageSize = 5;


        Page<Customer>    page = service.findPaginated(pageNo, pageSize, sortField, sortDir);

        List<Customer> listOfCustomers = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
       model.addAttribute("totalItems",page.getTotalElements());
       model.addAttribute("sortField",sortField);
       model.addAttribute("sortDir",sortDir);
       model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
       model.addAttribute("listOfCustomers",listOfCustomers);
       return "index";
    }





}
