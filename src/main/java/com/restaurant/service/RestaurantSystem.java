package com.restaurant.service;

import java.util.ArrayList;
import java.util.List;

import com.restaurant.model.OrderEvent;
import com.restaurant.model.menu.*;
import com.restaurant.model.pesanan.*;
import com.restaurant.model.transaksi.*;

public class RestaurantSystem {

    // =============================
    // SINGLETON BENAR-BENAR FINAL
    // =============================
    private static final RestaurantSystem instance = new RestaurantSystem();

    public static RestaurantSystem getInstance() {
        return instance;
    }

    private RestaurantSystem() {
        initMeja();
        menu = FileStorageService.loadMenu();
        daftarPesanan = FileStorageService.loadPesanan();
        idCounter = FileStorageService.loadLastId();
    }

    // =============================
    // ATRIBUT SISTEM
    // =============================
    private List<MenuItem> menu = new ArrayList<>();
    private List<Pesanan> daftarPesanan = new ArrayList<>();
    private List<Meja> mejaList = new ArrayList<>();
    private int idCounter = 1; // ID pesanan autoincrement

    // =============================
    // INISIALISASI 30 MEJA
    // =============================
    private void initMeja() {
        for (int i = 1; i <= 30; i++) {
            mejaList.add(new Meja(i));
        }
    }

    // =============================
    // AKSES MENU
    // =============================
    public List<MenuItem> getMenuList() {
        return menu;
    }

    public MenuItem getMenu(int index) {
        if (index < 0 || index >= menu.size())
            return null;
        return menu.get(index);
    }

    public void tampilMenu() {
        int i = 1;
        for (MenuItem m : menu) {
            System.out.println(i + ". " + m.toString());
            i++;
        }
    }

    // =============================
    // MEJA KOSONG
    // =============================
    public List<Meja> getMejaKosong() {
        List<Meja> kosong = new ArrayList<>();

        for (Meja m : mejaList) {
            boolean dipakai = false;

            for (Pesanan p : daftarPesanan) {
                if (p.getMeja().getNomor() == m.getNomor() &&
                        !p.getStatus().equals("SELESAI")) {
                    dipakai = true;
                    break;
                }
            }

            if (!dipakai)
                kosong.add(m);
        }

        return kosong;
    }

    // =============================
    // PESANAN
    // =============================
    public Pesanan buatPesananKosong(int noMeja) {
        Pesanan p = new Pesanan(idCounter++, new Meja(noMeja));
        p.setStatus("MENUNGGU");
        daftarPesanan.add(p);

        // Notifikasi untuk Pelayan bahwa ada pesanan baru
        OrderEvent event = new OrderEvent(p.getId(), null, "MENUNGGU", "customer", "customer");
        NotificationService.getInstance().addEvent(event);

        saveData();
        return p;
    }

    public List<Pesanan> getPesananByMeja(int meja) {
        List<Pesanan> hasil = new ArrayList<>();

        for (Pesanan p : daftarPesanan) {
            if (p.getMeja().getNomor() == meja) {
                hasil.add(p);
            }
        }

        return hasil;
    }

    public Pesanan getPesananById(int id) {
        for (Pesanan p : daftarPesanan) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }

    // =============================
    // UPDATE STATUS PESANAN
    // =============================
    public boolean updateStatusPesanan(int id, String statusBaru) {
        Pesanan p = getPesananById(id);
        if (p == null)
            return false;

        p.setStatus(statusBaru);
        saveData();
        return true;
    }

    /**
     * Update status pesanan dengan tracking user dan notifikasi
     */
    public boolean updateStatusPesanan(int id, String statusBaru, String username, String role) {
        Pesanan p = getPesananById(id);
        if (p == null)
            return false;

        String statusLama = p.getStatus();

        // Update status dengan tracking user
        p.setStatus(statusBaru, username, role);

        // Buat event untuk notifikasi
        OrderEvent event = new OrderEvent(id, statusLama, statusBaru, role, username);
        NotificationService.getInstance().addEvent(event);

        saveData();
        return true;
    }

    /**
     * Mendapatkan notifikasi untuk role tertentu
     */
    public List<String> getNotificationsForRole(String role) {
        return NotificationService.getInstance().getNotificationsForRole(role);
    }

    // =============================
    // TAMPIL PESANAN PER STATUS
    // =============================
    public void tampilPesananDenganStatus(String status) {
        boolean found = false;
        for (Pesanan p : daftarPesanan) {
            if (p.getStatus().equalsIgnoreCase(status)) {
                System.out.println(
                        "ID:" + p.getId() + " | Meja:" + p.getMeja().getNomor() + " | Total: Rp" + p.getTotal());
                System.out.println(p.renderDetail());
                found = true;
            }
        }
        if (!found)
            System.out.println("Tidak ada pesanan dengan status: " + status);
    }

    // =============================
    // TRANSAKSI PEMBAYARAN
    // =============================
    public Transaksi buatTransaksi(Pesanan p, Pembayaran pb) {
        Transaksi t = new Transaksi(p, pb);
        FileStorageService.saveTransaksi(t);
        return t;
    }

    // =============================
    // PERSISTENSI
    // =============================
    public void saveData() {
        FileStorageService.savePesanan(daftarPesanan, idCounter);
    }
}
