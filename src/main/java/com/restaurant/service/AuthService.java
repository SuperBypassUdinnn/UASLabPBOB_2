package com.restaurant.service;

import com.restaurant.model.akun.*;
import java.util.*;
import com.restaurant.utils.JsonUtil;

public class AuthService {

    private static final String FILE_PATH = "src/main/resources/data/akun.json";
    private List<Akun> akunList = new ArrayList<>();

    public AuthService() {
        loadAkun();
    }

    // ----------------------------- LOAD DATA (JSON Format)
    // -----------------------------
    // Format JSON: { "akun": [ { "id": "C01", "nama": "...", "username": "...",
    // "password": "...", "email": "...", "role": "customer" }, ... ] }
    // Untuk customer: email bisa kosong
    // Untuk pegawai: email harus dengan domain usk.ac.id
    private void loadAkun() {
        String json = JsonUtil.readFile(FILE_PATH);

        if (json.equals("{}")) {
            return; // File tidak ada atau kosong
        }

        // Parse array akun dari JSON
        List<String> akunObjects = JsonUtil.parseArray(json, "akun");

        for (String akunObj : akunObjects) {
            try {
                String id = JsonUtil.getString(akunObj, "id");
                String nama = JsonUtil.getString(akunObj, "nama");
                String username = JsonUtil.getString(akunObj, "username");
                String password = JsonUtil.getString(akunObj, "password");
                String email = JsonUtil.getString(akunObj, "email");
                String role = JsonUtil.getString(akunObj, "role");

                if (id == null || nama == null || username == null || password == null || role == null) {
                    continue; // Skip jika data tidak lengkap
                }

                if (email == null)
                    email = ""; // Default email kosong

                if (role.equalsIgnoreCase("customer")) {
                    akunList.add(new Customer(id, nama, username, password, email));
                } else {
                    akunList.add(new Pegawai(id, nama, username, password, email, role));
                }
            } catch (Exception e) {
                System.err.println("[AuthService] Error parsing akun: " + e.getMessage());
            }
        }
    }

    // ----------------------------- SAVE DATA -----------------------------
    // Format: id;nama;username;password;email;role
    private void saveAkun() {
        try {
            List<String> akunJsonList = new ArrayList<>();

            for (Akun a : akunList) {
                String akunJson = JsonUtil.jsonObject(
                        "id", a.getId(),
                        "nama", a.getNama(),
                        "username", a.getUsername(),
                        "password", a.getPassword(),
                        "email", a.getEmail() == null ? "" : a.getEmail(),
                        "role", a.getRole());
                akunJsonList.add(akunJson);
            }

            String akunArray = JsonUtil.jsonArray(akunJsonList);
            String json = JsonUtil.jsonWithRoot("akun", akunArray);

            JsonUtil.writeFile(FILE_PATH, json);
        } catch (Exception e) {
            System.out.println("Gagal menulis akun.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------------------- LOGIN -----------------------------
    public Akun login(String username, String password) {
        for (Akun akun : akunList) {
            if (akun.getUsername().equals(username) &&
                    akun.getPassword().equals(password)) {
                return akun;
            }
        }
        return null; // login gagal
    }

    // ----------------------------- VALIDASI EMAIL DOMAIN
    // -----------------------------
    public boolean isValidEmailDomain(String email, String requiredDomain) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.lastIndexOf("@") + 1);
        return domain.equalsIgnoreCase(requiredDomain);
    }

    // ----------------------------- REGISTER CUSTOMER -----------------------------
    public boolean registerCustomer(String nama, String username, String password, String email) {

        // cek username duplicate
        for (Akun a : akunList) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        String newId = generateIdCustomer();
        Customer customer = new Customer(newId, nama, username, password, email);
        akunList.add(customer);

        saveAkun();
        return true;
    }

    // ----------------------------- REGISTER PEGAWAI -----------------------------
    public boolean registerPegawai(String nama, String username, String password, String email, String role) {

        // Validasi email domain untuk pegawai
        if (!isValidEmailDomain(email, "usk.ac.id")) {
            return false; // Email tidak valid untuk registrasi pegawai
        }

        // cek username duplicate
        for (Akun a : akunList) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        String newId = generateIdPegawai();
        Pegawai pegawai = new Pegawai(newId, nama, username, password, email, role);

        akunList.add(pegawai);
        saveAkun();

        return true;
    }

    // ----------------------------- AUTO-ID CUSTOMER -----------------------------
    private String generateIdCustomer() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("C"))
                count++;
        }
        return "C" + String.format("%02d", count + 1);
    }

    // ----------------------------- AUTO-ID PEGAWAI -----------------------------
    private String generateIdPegawai() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("P"))
                count++;
        }
        return "P" + String.format("%02d", count + 1);
    }
}
