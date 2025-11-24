package com.restaurant.service;

import com.restaurant.model.akun.*;
import java.util.*;
import com.restaurant.utils.JsonUtil;

public class AuthService {

    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null)
            instance = new AuthService();
        return instance;
    }

    private static final String FILE_PATH = "src/main/resources/data/akun.json";
    private List<Akun> akunList = new ArrayList<>();

    private AuthService() {
        loadAkun();
    }

    private void loadAkun() {
        // ... (kode load sama seperti sebelumnya, tidak berubah)
        // Agar ringkas, saya tidak copy ulang bagian loadAkun & saveAkun yang tidak
        // berubah
        // Asumsikan logic load/save JSON tetap sama
        String json = JsonUtil.readFile(FILE_PATH);
        if (json.equals("{}"))
            return;
        List<String> akunObjects = JsonUtil.parseArray(json, "akun");
        for (String akunObj : akunObjects) {
            try {
                String id = JsonUtil.getString(akunObj, "id");
                String nama = JsonUtil.getString(akunObj, "nama");
                String username = JsonUtil.getString(akunObj, "username");
                String password = JsonUtil.getString(akunObj, "password");
                String email = JsonUtil.getString(akunObj, "email");
                String role = JsonUtil.getString(akunObj, "role");

                if (role.equalsIgnoreCase("customer")) {
                    akunList.add(new Customer(id, nama, username, password, email));
                } else {
                    akunList.add(new Pegawai(id, nama, username, password, email, role));
                }
            } catch (Exception e) {
            }
        }
    }

    // Perlu method saveAkun() tetap ada (copy dari kode Anda sebelumnya)
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
            JsonUtil.writeFile(FILE_PATH, JsonUtil.jsonWithRoot("akun", JsonUtil.jsonArray(akunJsonList)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Akun login(String username, String password) {
        for (Akun akun : akunList) {
            if (akun.getUsername().equals(username) && akun.getPassword().equals(password)) {
                return akun;
            }
        }
        return null;
    }

    public boolean registerCustomer(String nama, String username, String password, String email) {
        for (Akun a : akunList)
            if (a.getUsername().equalsIgnoreCase(username))
                return false;

        // Customer bebas email
        String newId = generateIdCustomer();
        Customer customer = new Customer(newId, nama, username, password, email);
        akunList.add(customer);
        saveAkun();
        return true;
    }

    public boolean registerPegawai(String nama, String username, String password, String email, String role) {
        // UPDATE VALIDASI: Domain harus rasaaceh.id
        if (!isValidEmailDomain(email, "rasaaceh.id"))
            return false;

        for (Akun a : akunList)
            if (a.getUsername().equalsIgnoreCase(username))
                return false;

        String newId = generateIdPegawai();
        Pegawai pegawai = new Pegawai(newId, nama, username, password, email, role);
        akunList.add(pegawai);
        saveAkun();
        return true;
    }

    public boolean isValidEmailDomain(String email, String requiredDomain) {
        if (email == null || !email.contains("@"))
            return false;
        String domain = email.substring(email.lastIndexOf("@") + 1);
        return domain.equalsIgnoreCase(requiredDomain);
    }

    private String generateIdCustomer() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("C"))
                count++;
        }
        return "C" + String.format("%02d", count + 1);
    }

    private String generateIdPegawai() {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("P"))
                count++;
        }
        return "P" + String.format("%02d", count + 1);
    }
}