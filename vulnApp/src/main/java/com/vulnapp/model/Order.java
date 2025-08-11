package com.vulnapp.model;

import java.io.Serializable;
import java.util.UUID;

public class Order implements Serializable {
    public UUID id;
    public UUID ownerId;
    public String itemName;
    public int quantity;
    public String status;
    public boolean isAdminFlag;
    public Order() {}
}
