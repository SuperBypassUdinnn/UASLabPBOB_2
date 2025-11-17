package com.restaurant.model.akun;

public abstract class Akun {
    private String id;
    private String nama;
    private String username;
    private String password;

    public Akun(String id, String nama, String username, String password) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public abstract String getRole();

    // Format untuk menyimpan ke file
    public String toFileFormat() {
        return id + ";" + nama + ";" + username + ";" + password + ";" + getRole();
    }
}
