package com.restaurant.service;

import com.restaurant.model.akun.*;
import java.io.*;
import java.util.*;

public class AuthService {

    private static final String FILE_PATH = "src/main/resources/data/akun.txt";
    private List<Akun> akunList = new ArrayList<>();

    public AuthService() {
        loadAkun();
    }

    // ----------------------------- LOAD DATA -----------------------------
    private void loadAkun() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length < 5)
                    continue; // skip baris rusak

                String id = data[0];
                String nama = data[1];
                String username = data[2];
                String password = data[3];
                String email;
                String role;

                // Handle backward compatibility: format lama (5 kolom) vs format baru (6 kolom)
                if (data.length == 5) {
                    // Format lama: id;nama;username;password;role
                    role = data[4];
                    email = ""; // Default email kosong untuk data lama
                } else {
                    // Format baru: id;nama;username;password;email;role
                    email = data[4];
                    role = data[5];
                }

                if (role.equalsIgnoreCase("customer")) {
                    akunList.add(new Customer(id, nama, username, password, email));
                } else {
                    akunList.add(new Pegawai(id, nama, username, password, email, role));
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal membaca akun.txt: " + e.getMessage());
        }
    }

    // ----------------------------- SAVE DATA -----------------------------
    private void saveAkun() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Akun a : akunList) {
                bw.write(a.toFileFormat());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menulis akun.txt: " + e.getMessage());
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

    // ----------------------------- VALIDASI EMAIL DOMAIN -----------------------------
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
