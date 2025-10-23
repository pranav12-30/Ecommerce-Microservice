package com.ecommerce.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
