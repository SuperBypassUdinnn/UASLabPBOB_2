package com.restaurant.model.akun;

public class Customer extends Akun {

    public Customer(String id, String nama, String username, String password) {
        super(id, nama, username, password);
    }

    @Override
    public String getRole() {
        return "customer";
    }
}

