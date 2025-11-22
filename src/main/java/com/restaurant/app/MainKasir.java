package com.restaurant.app;

import java.util.List;
import java.util.Scanner;

import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.model.transaksi.*;
import com.restaurant.model.Struk;
import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainKasir {

    private static Scanner sc = InputUtil.sc;
    private static RestaurantSystem rs = RestaurantSystem.getInstance();
    private static String currentUsername;
    private static String currentRole;

    public static void run(String username, String role) {
        currentUsername = username;
        currentRole = role;
        
        while (true) {
            // Tampilkan notifikasi terbaru
            tampilNotifikasi();
            
            System.out.println("\n===== MENU KASIR =====");
            System.out.println("Selamat datang, " + currentUsername + "!");
            System.out.println("1. Lihat Pesanan Siap Bayar (DISAJIKAN)");
            System.out.println("2. Proses Pembayaran");
            System.out.println("3. Lihat Notifikasi");
            System.out.println("4. Lihat Detail Pesanan");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatSiapBayar();
                    break;
                case 2:
                    prosesPembayaran();
                    break;
                case 3:
                    tampilNotifikasi();
                    break;
                case 4:
                    lihatDetailPesanan();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void tampilNotifikasi() {
        List<String> notifications = rs.getNotificationsForRole("kasir");
        if (!notifications.isEmpty()) {
            System.out.println("\n📢 NOTIFIKASI TERBARU:");
            for (String notif : notifications) {
                System.out.println("  " + notif);
            }
        }
    }

    private static void lihatSiapBayar() {
        System.out.println("\n=== PESANAN DISAJIKAN (Siap untuk Pembayaran) ===");
        rs.tampilPesananDenganStatus("DISAJIKAN");
    }

    /**
     * Kasir memproses pembayaran pesanan yang sudah disajikan
     * Status: DISAJIKAN -> LUNAS
     */
    private static void prosesPembayaran() {
        System.out.println("\n=== PILIH PESANAN UNTUK DIBAYAR ===");
        System.out.println("(Hanya pesanan yang sudah DISAJIKAN bisa dibayar)");
        rs.tampilPesananDenganStatus("DISAJIKAN");

        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();

        Pesanan p = rs.getPesananById(id);
        if (p == null) {
            System.out.println("❌ ID tidak ditemukan.");
            return;
        }
        
        // Validasi: hanya pesanan yang sudah disajikan yang bisa dibayar
        if (!p.getStatus().equals("DISAJIKAN")) {
            System.out.println("❌ Pesanan belum disajikan. Status saat ini: " + p.getStatus());
            System.out.println("   Hanya pesanan dengan status DISAJIKAN yang bisa dibayar.");
            return;
        }

        System.out.println("\n" + p.getInfoLengkap());
        System.out.println("\nTotal bayar: Rp" + p.getTotal());
        System.out.println("Metode Pembayaran:");
        System.out.println("1. Cash (Tunai)");
        System.out.println("2. Card (Kartu)");
        System.out.println("3. QRIS");

        System.out.print("Pilih metode: ");
        int m = sc.nextInt();
        sc.nextLine();

        Pembayaran pb;

        switch (m) {
            case 1:
                System.out.print("Masukkan uang: ");
                double uang = sc.nextDouble();
                sc.nextLine();
                pb = new CashPayment(uang);
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

        Transaksi t = rs.buatTransaksi(p, pb);

        if (t.konfirmasi()) {
            Struk.cetak(t);
            rs.updateStatusPesanan(id, "LUNAS", currentUsername, currentRole);
            rs.saveData();
            System.out.println("✅ Pembayaran berhasil! Pesanan #" + id + " status menjadi LUNAS.");
            System.out.println("📤 Notifikasi telah dikirim ke Pelayan dan Customer!");
        } else {
            System.out.println("❌ Pembayaran gagal.");
        }
    }
    
    private static void lihatDetailPesanan() {
        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        Pesanan pesanan = rs.getPesananById(id);
        if (pesanan != null) {
            System.out.println("\n" + pesanan.getInfoLengkap());
        } else {
            System.out.println("Pesanan tidak ditemukan.");
        }
    }
}
