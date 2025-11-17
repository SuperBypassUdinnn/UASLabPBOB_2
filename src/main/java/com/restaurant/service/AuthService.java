package com.restaurant.service;

import main.java.com.restaurant.model.akun.*;
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

                if (data.length < 5) continue; // skip baris rusak

                String id = data[0];
                String nama = data[1];
                String username = data[2];
                String password = data[3];
                String role = data[4];

                if (role.equalsIgnoreCase("customer")) {
                    akunList.add(new Customer(id, nama, username, password));
                } else {
                    akunList.add(new Pegawai(id, nama, username, password, role));
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

    // ----------------------------- REGISTER CUSTOMER -----------------------------
    public boolean registerCustomer(String nama, String username, String password) {

        // cek username duplicate
        for (Akun a : akunList) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        String newId = generateIdCustomer();
        Customer customer = new Customer(newId, nama, username, password);
        akunList.add(customer);

        saveAkun();
        return true;
    }

    // ----------------------------- REGISTER PEGAWAI -----------------------------
    public boolean registerPegawai(String nama, String username, String password, String role) {

        // cek username duplicate
        for (Akun a : akunList) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        String newId = generateIdPegawai();
        Pegawai pegawai = new Pegawai(newId, nama, username, password, role);

        akunList.add(pegawai);
        saveAkun();

        return true;
    }

    // ----------------------------- AUTO-ID CUSTOMER -----------------------------
    private String generateIdCustomer() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("C")) count++;
        }
        return "C" + String.format("%02d", count + 1);
    }

    // ----------------------------- AUTO-ID PEGAWAI -----------------------------
    private String generateIdPegawai() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("P")) count++;
        }
        return "P" + String.format("%02d", count + 1);
    }
}


