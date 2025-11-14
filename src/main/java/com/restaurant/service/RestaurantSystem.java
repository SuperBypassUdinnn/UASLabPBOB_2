package main.java.com.restaurant.service;

import java.util.*;
import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;
import main.java.com.restaurant.model.transaksi.*;

public class RestaurantSystem {

    private static RestaurantSystem instance = new RestaurantSystem();

    private List<MenuItem> menu;
    private List<Pesanan> daftarPesanan = new ArrayList<>();
    private int idCounter = 1;
    private int idTransaksiCounter = 1000;

    // draft customer → pelayan
    public DetailPesanan draftCustomer;

    private RestaurantSystem() {
        menu = FileStorageService.loadMenu();
    }

    public static RestaurantSystem getInstance() {
        return instance;
    }

    // ==============================
    // MENU
    // ==============================
    public void tampilMenu() {
        System.out.println("=== MENU ===");
        int no = 1;
        for (MenuItem m : menu) {
            System.out.println(no++ + ". " + m.getInfo());
        }
    }

    public MenuItem getMenu(int index) {
        return menu.get(index);
    }

    public List<MenuItem> getMenuList() {
        return menu;
    }

    // ==============================
    // PESANAN
    // ==============================
    public Pesanan buatPesanan(int meja) {
        Pesanan p = new Pesanan(idCounter++, new Meja(meja));
        daftarPesanan.add(p);
        return p;
    }

    public Pesanan getPesananById(int id) {
        return daftarPesanan.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void tampilPesananMenunggu() {
        System.out.println("=== PESANAN MENUNGGU DIMASAK ===");
        for (Pesanan p : daftarPesanan) {
            if (p.getStatus().equals("MENUNGGU")) {
                System.out.println("- ID " + p.getId() + " (Meja " + p.getMeja().getNomor() + ")");
            }
        }
    }

    public void tampilPesananSiapBayar() {
        System.out.println("=== PESANAN SIAP BAYAR ===");
        for (Pesanan p : daftarPesanan) {
            if (p.getStatus().equals("SELESAI DIMASAK")) {
                System.out.println("- ID " + p.getId());
            }
        }
    }

    // ==============================
    // TRANSAKSI
    // ==============================
    public Transaksi buatTransaksi(Pesanan p, Pembayaran pm) {
        return new Transaksi(idTransaksiCounter++, p, pm);
    }

    // ==============================
    // SIMPAN
    // ==============================
    public void saveData() {
        FileStorageService.savePesanan(daftarPesanan);
    }
}
