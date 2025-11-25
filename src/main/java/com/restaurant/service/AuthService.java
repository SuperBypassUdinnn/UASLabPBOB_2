package com.restaurant.service;

import com.restaurant.model.akun.*;
import com.restaurant.utils.JsonUtil;
import java.util.*;

public class AuthService {

    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null)
            instance = new AuthService();
        return instance;
    }

    private static final String FILE_PATH = "src/main/resources/data/akun.json";
    private final List<Akun> akunList = new ArrayList<>();

    private AuthService() {
        loadAkun();
    }

    private void loadAkun() {
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

    public int register(String nama, String username, String password, String email, String role) {
        // Validasi username
        if (!isValidUsername(username))
            return 1;
        if (isUsernameTaken(username))
            return 2;

        // Validasi email
        if (!isValidEmailDomain(email))
            return 3;
        if (!role.equalsIgnoreCase("customer")) {
            // Domain harus rasaaceh.id
            if (!isHasAccessEmail(email, "rasaaceh.id"))
                return 4;
        }
        if (isEmailTaken(email))
            return 5;

        // Validasi kekuatan password
        if (!isStrongPassword(password))
            return 6;

        String newId = generateId(role);
        if (role.equalsIgnoreCase("customer")) {
            Customer customer = new Customer(newId, nama, username, password, email);
            akunList.add(customer);
        } else {
            Pegawai pegawai = new Pegawai(newId, nama, username, password, email, role);
            akunList.add(pegawai);
        }
        saveAkun();
        return 0;
    }

    public boolean isValidUsername(String username) {
        return username.length() >= 5 && username.length() <= 15
                && username.matches("^[a-zA-Z0-9_]+$");
    }

    public boolean isUsernameTaken(String username) {
        for (Akun a : akunList) {
            if (a.getUsername().equalsIgnoreCase(username))
                return true;
        }
        return false;
    }

    public boolean isValidEmailDomain(String email) {
        String[] domains = { "rasaaceh.id", "gmail.com", "yahoo.com", "yahoo.co.id", "outlook.com", "hotmail.com",
                "live.com",
                "icloud.com", "proton.me", "protonmail.com", "zoho.com", "gmx.com", "gmx.de" };
        if (!email.contains("@"))
            return false;
        for (String domain : domains) {
            if (email.endsWith(domain))
                return true;
        }
        return false;
    }

    public boolean isHasAccessEmail(String email, String requiredDomain) {
        String domain = email.substring(email.lastIndexOf("@") + 1);
        return domain.equalsIgnoreCase(requiredDomain);
    }

    public boolean isEmailTaken(String email) {
        for (Akun a : akunList) {
            if (a.getEmail().equalsIgnoreCase(email))
                return true;
        }
        return false;
    }

    public boolean isStrongPassword(String password) {
        if (password.length() < 8)
            return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isLowerCase(c))
                hasLower = true;
            else if (Character.isDigit(c))
                hasDigit = true;
            else
                hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private String generateId(String role) {
        int count = 0;
        for (Akun a : akunList) {
            if (a.getId().startsWith("P"))
                count++;
        }
        if (role.equalsIgnoreCase("customer")) {
            return "C" + String.format("%02d", count + 1);
        } else {
            return "P" + String.format("%02d", count + 1);
        }
    }
}