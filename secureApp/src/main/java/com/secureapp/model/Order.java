package com.secureapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Entity @Table(name="orders")
public class Order {
    @Id private UUID id;
    @Column(nullable=false) private UUID ownerId;
    @NotBlank @Size(max=255) private String itemName;
    @Min(1) @Max(1000) private int quantity;
    @NotBlank @Size(max=32) private String status;
    public Order() {}
    public UUID getId(){ return id; } public void setId(UUID id){ this.id=id; }
    public UUID getOwnerId(){ return ownerId; } public void setOwnerId(UUID o){ this.ownerId=o; }
    public String getItemName(){ return itemName; } public void setItemName(String i){ this.itemName=i; }
    public int getQuantity(){ return quantity; } public void setQuantity(int q){ this.quantity=q; }
    public String getStatus(){ return status; } public void setStatus(String s){ this.status=s; }
}
