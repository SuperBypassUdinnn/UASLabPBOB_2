package com.restaurant.app;

import java.util.List;
import java.util.Scanner;

import com.restaurant.model.pesanan.*;
import com.restaurant.model.menu.*;
import com.restaurant.model.transaksi.*;
import com.restaurant.model.Struk;
import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainCustomer {

    private static RestaurantSystem rs = RestaurantSystem.getInstance();
    private static Scanner sc = InputUtil.sc;

    public static void run() {
        while (true) {
            System.out.println("\n===== MENU CUSTOMER =====");
            System.out.println("1. Pilih Meja");
            System.out.println("2. Lihat Menu");
            System.out.println("3. Buat Pesanan");
            System.out.println("4. Lihat Status Pesanan");
            System.out.println("5. Bayar Pesanan");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");

            int pilih = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (pilih) {
                case 1:
                    pilihMeja();
                    break;
                case 2:
                    lihatMenu();
                    break;
                case 3:
                    buatPesanan();
                    break;
                case 4:
                    lihatStatus();
                    break;
                case 5:
                    bayarPesanan();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    // ========================================
    // CUSTOMER: PILIH MEJA
    // ========================================
    private static int mejaDipilih = -1;

    private static void pilihMeja() {
        System.out.println("=== Meja Kosong ===");
        List<Meja> kosong = rs.getMejaKosong();

        for (Meja m : kosong) {
            System.out.print(m.getNomor() + " ");
        }
        System.out.println();

        System.out.print("Pilih meja: ");
        int meja = sc.nextInt();
        sc.nextLine();

        boolean valid = kosong.stream().anyMatch(m -> m.getNomor() == meja);

        if (!valid) {
            System.out.println("Meja tidak tersedia!");
            return;
        }

        mejaDipilih = meja;
        System.out.println("Meja " + meja + " berhasil dipilih.");
    }

    // ========================================
    // LIHAT MENU
    // ========================================
    private static void lihatMenu() {
        System.out.println("\n=== MENU ===");
        rs.tampilMenu();
    }

    // ========================================
    // CUSTOMER: BUAT PESANAN (BISA BANYAK ITEM)
    // ========================================
    private static void buatPesanan() {

        if (mejaDipilih == -1) {
            System.out.println("Anda belum memilih meja!");
            return;
        }

        Pesanan p = rs.buatPesananKosong(mejaDipilih);

        while (true) {
            rs.tampilMenu();

            System.out.print("Pilih menu (nomor): ");
            int pilih = sc.nextInt() - 1;

            System.out.print("Jumlah: ");
            int jumlah = sc.nextInt();
            sc.nextLine();

            System.out.print("Catatan: ");
            String catatan = sc.nextLine();

            MenuItem item = rs.getMenu(pilih);
            p.tambahItem(new DetailPesanan(item, jumlah, catatan));

            System.out.print("Tambah item lagi? (y/n): ");
            String lagi = sc.nextLine();
            if (!lagi.equalsIgnoreCase("y"))
                break;
        }

        // Status MENUNGGU
        p.setStatus("MENUNGGU");

        rs.saveData();

        System.out.println("Pesanan berhasil dibuat! ID: " + p.getId());
    }

    // ========================================
    // LIHAT STATUS PESANAN
    // ========================================
    private static void lihatStatus() {
        if (mejaDipilih == -1) {
            System.out.println("Anda belum memilih meja!");
            return;
        }

        List<Pesanan> pes = rs.getPesananByMeja(mejaDipilih);

        if (pes.isEmpty()) {
            System.out.println("Belum ada pesanan.");
            return;
        }

        System.out.println("\n=== STATUS PESANAN ===");
        for (Pesanan p : pes) {
            System.out.println("ID: " + p.getId() + " | Status: " + p.getStatus());
        }
    }

    // ========================================
    // BAYAR PESANAN
    // ========================================
    private static void bayarPesanan() {

        if (mejaDipilih == -1) {
            System.out.println("Anda belum memilih meja!");
            return;
        }

        List<Pesanan> pes = rs.getPesananByMeja(mejaDipilih);

        Pesanan siap = pes.stream()
                .filter(p -> p.getStatus().equals("SELESAI DIMASAK"))
                .findFirst()
                .orElse(null);

        if (siap == null) {
            System.out.println("Belum ada pesanan yang selesai dimasak.");
            return;
        }

        System.out.println("Total bayar: Rp" + siap.getTotal());
        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. QRIS");
        System.out.print("Pilih: ");

        int m = sc.nextInt();
        sc.nextLine();

        Pembayaran pb;

        switch (m) {
            case 1:
                System.out.print("Uang diberikan: ");
                pb = new CashPayment(sc.nextDouble());
                sc.nextLine();
                break;
            case 2:
                pb = new CardPayment();
                break;
            case 3:
                pb = new QRISPayment();
                break;
            default:
                System.out.println("Metode tidak valid.");
                return;
        }

        Transaksi t = rs.buatTransaksi(siap, pb);

        if (t.konfirmasi()) {
            Struk.cetak(t);
            rs.updateStatusPesanan(siap.getId(), "SELESAI");
            System.out.println("Pembayaran berhasil!");
        } else {
            System.out.println("Pembayaran gagal.");
        }

        rs.saveData();
    }
}
