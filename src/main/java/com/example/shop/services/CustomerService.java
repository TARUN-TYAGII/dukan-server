package com.example.shop.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dtos.CustomerDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Customer;
import com.example.shop.repositories.CustomerRepository;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .filter(customer -> customer.getIsActive())
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByEmail(String email) {
        return customerRepository.findByEmailAndIsActiveTrue(email)
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByPhone(String phone) {
        return customerRepository.findByPhoneAndIsActiveTrue(phone)
                .map(this::convertToDTO);
    }
    
    public List<CustomerDTO> getCustomersByType(Customer.CustomerType customerType) {
        return customerRepository.findByCustomerTypeAndIsActiveTrue(customerType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> getCustomersByCity(String city) {
        return customerRepository.findByCityAndIsActiveTrue(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<CustomerDTO> searchCustomers(SearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getSortDirection()), 
                           searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        return customerRepository.searchCustomers(
                searchRequest.getName(),
                searchRequest.getEmail(),
                searchRequest.getPhone(),
                searchRequest.getCustomerType(),
                pageable
        ).map(this::convertToDTO);
    }
    
    public List<CustomerDTO> getCustomersWithOrders() {
        return customerRepository.findCustomersWithOrders()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> getTopCustomers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return customerRepository.findTopCustomersByOrderValue(pageable)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check email uniqueness
        if (customerRepository.existsByEmailAndIsActiveTrue(customerDTO.getEmail())) {
            throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
        }
        
        // Check phone uniqueness
        if (customerRepository.existsByPhoneAndIsActiveTrue(customerDTO.getPhone())) {
            throw new RuntimeException("Customer with phone " + customerDTO.getPhone() + " already exists");
        }
        
        Customer customer = convertToEntity(customerDTO);
        customer.setIsActive(true);
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Check email uniqueness if changed
        if (!customerDTO.getEmail().equals(existingCustomer.getEmail())) {
            if (customerRepository.existsByEmailAndIsActiveTrue(customerDTO.getEmail())) {
                throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
            }
        }
        
        // Check phone uniqueness if changed
        if (!customerDTO.getPhone().equals(existingCustomer.getPhone())) {
            if (customerRepository.existsByPhoneAndIsActiveTrue(customerDTO.getPhone())) {
                throw new RuntimeException("Customer with phone " + customerDTO.getPhone() + " already exists");
            }
        }
        
        BeanUtils.copyProperties(customerDTO, existingCustomer, "id", "createdAt", "updatedAt");
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setIsActive(false);
        customerRepository.save(customer);
    }
    
    public boolean isEmailAvailable(String email) {
        return !customerRepository.existsByEmailAndIsActiveTrue(email);
    }
    
    public boolean isPhoneAvailable(String phone) {
        return !customerRepository.existsByPhoneAndIsActiveTrue(phone);
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .pincode(customer.getPincode())
                .country(customer.getCountry())
                .customerType(customer.getCustomerType())
                .institutionName(customer.getInstitutionName())
                .contactPerson(customer.getContactPerson())
                .gstNumber(customer.getGstNumber())
                .isActive(customer.getIsActive())
                .build();
        
        // Calculate order statistics
        if (customer.getOrders() != null) {
            dto.setTotalOrders(customer.getOrders().size());
            dto.setTotalOrderValue(customer.getOrders().stream()
                    .mapToDouble(order -> order.getFinalAmount().doubleValue())
                    .sum());
        }
        
        return dto;
    }
    
    private Customer convertToEntity(CustomerDTO dto) {
        return Customer.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .country(dto.getCountry())
                .customerType(dto.getCustomerType())
                .institutionName(dto.getInstitutionName())
                .contactPerson(dto.getContactPerson())
                .gstNumber(dto.getGstNumber())
                .isActive(dto.getIsActive())
                .build();
    }
}
