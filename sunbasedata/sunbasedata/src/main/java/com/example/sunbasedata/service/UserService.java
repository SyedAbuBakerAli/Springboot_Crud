package com.example.sunbasedata.service;

import com.example.sunbasedata.entity.Customer;
import com.example.sunbasedata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public Customer saveUser(Customer user){
        return repository.save(user);
    }

    public List<Customer> getCustomer(){

            return repository.findAll();


    }

    public Customer getUserById(int id){
        return repository.findById(id).orElse(null);
    }

    public String deleteUser(int id){
        repository.deleteById(id);
        return "User deleted";
    }

    public Customer updateUser(Customer user){
        Customer existingUser = repository.findById(user.getId()).orElse(null);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setStreet(user.getStreet());
        existingUser.setAddress(user.getAddress());
        existingUser.setCity(user.getCity());
        existingUser.setState(user.getState());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        return repository.save(existingUser);

    }

    public Page<Customer> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

      return  repository.findAll(pageable);

    }









}
