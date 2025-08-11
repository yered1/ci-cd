package com.secureapp.dto;

import jakarta.validation.constraints.*;
public class OrderPatch {
    @Size(max=255) public String itemName;
    @Min(1) @Max(1000) public Integer quantity;
    @Size(max=32) public String status;
}
