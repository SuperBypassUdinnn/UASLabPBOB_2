package com.restaurant.app;

import java.util.List;
import java.util.Scanner;

import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainPelayan {

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
            
            System.out.println("\n===== MENU PELAYAN =====");
            System.out.println("Selamat datang, " + currentUsername + "!");
            System.out.println("1. Lihat Pesanan MENUNGGU (Pesanan Baru)");
            System.out.println("2. Terima Pesanan -> DIPROSES (Kirim ke Dapur)");
            System.out.println("3. Lihat Pesanan SIAP DISAJIKAN");
            System.out.println("4. Sajikan Pesanan ke Meja -> DISAJIKAN");
            System.out.println("5. Lihat Notifikasi");
            System.out.println("6. Lihat Detail Pesanan");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatPesananMenunggu();
                    break;
                case 2:
                    terimaPesanan();
                    break;
                case 3:
                    lihatPesananSiapDisajikan();
                    break;
                case 4:
                    sajikanPesanan();
                    break;
                case 5:
                    tampilNotifikasi();
                    break;
                case 6:
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
        List<String> notifications = rs.getNotificationsForRole("pelayan");
        if (!notifications.isEmpty()) {
            System.out.println("\n📢 NOTIFIKASI TERBARU:");
            for (String notif : notifications) {
                System.out.println("  " + notif);
            }
        }
    }

    private static void lihatPesananMenunggu() {
        System.out.println("\n=== PESANAN MENUNGGU (Pesanan Baru dari Customer) ===");
        rs.tampilPesananDenganStatus("MENUNGGU");
    }

    /**
     * Pelayan menerima pesanan dari customer dan mengirim ke dapur
     * Status: MENUNGGU -> DIPROSES
     */
    private static void terimaPesanan() {
        System.out.println("\n=== PESANAN MENUNGGU ===");
        rs.tampilPesananDenganStatus("MENUNGGU");

        System.out.print("Masukkan ID pesanan yang akan diterima dan dikirim ke dapur: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "DIPROSES", currentUsername, currentRole);
        if (ok) {
            System.out.println("✅ Pesanan #" + id + " diterima dan dikirim ke dapur (Status: DIPROSES).");
            System.out.println("📤 Notifikasi telah dikirim ke Koki!");
        } else {
            System.out.println("❌ Pesanan dengan ID " + id + " tidak ditemukan atau status tidak valid.");
        }
    }

    /**
     * Pelayan melihat pesanan yang sudah siap disajikan (selesai dimasak)
     */
    private static void lihatPesananSiapDisajikan() {
        System.out.println("\n=== PESANAN SIAP DISAJIKAN (Selesai Dimasak) ===");
        rs.tampilPesananDenganStatus("SIAP DISAJIKAN");
    }

    /**
     * Pelayan menyajikan pesanan ke meja customer
     * Status: SIAP DISAJIKAN -> DISAJIKAN
     */
    private static void sajikanPesanan() {
        System.out.println("\n=== PESANAN SIAP DISAJIKAN ===");
        rs.tampilPesananDenganStatus("SIAP DISAJIKAN");

        System.out.print("Masukkan ID pesanan yang akan disajikan ke meja: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "DISAJIKAN", currentUsername, currentRole);
        if (ok) {
            System.out.println("✅ Pesanan #" + id + " sudah disajikan ke meja (Status: DISAJIKAN).");
            System.out.println("📤 Notifikasi telah dikirim ke Customer dan Kasir!");
        } else {
            System.out.println("❌ Pesanan dengan ID " + id + " tidak ditemukan atau status tidak valid.");
        }
    }
    
    private static void lihatDetailPesanan() {
        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        com.restaurant.model.pesanan.Pesanan pesanan = rs.getPesananById(id);
        if (pesanan != null) {
            System.out.println("\n" + pesanan.getInfoLengkap());
        } else {
            System.out.println("Pesanan tidak ditemukan.");
        }
    }
}
