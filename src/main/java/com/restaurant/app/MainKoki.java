package com.restaurant.app;

import java.util.List;
import java.util.Scanner;

import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainKoki {

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
            
            System.out.println("\n===== MENU KOKI =====");
            System.out.println("Selamat datang, " + currentUsername + "!");
            System.out.println("1. Lihat Pesanan SEDANG DIMASAK");
            System.out.println("2. Tandai Pesanan SELESAI DIMASAK");
            System.out.println("3. Lihat Notifikasi");
            System.out.println("4. Lihat Detail Pesanan");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatPesananSedangDimasak();
                    break;
                case 2:
                    selesaiDimasak();
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
        List<String> notifications = rs.getNotificationsForRole("koki");
        if (!notifications.isEmpty()) {
            System.out.println("\n📢 NOTIFIKASI TERBARU:");
            for (String notif : notifications) {
                System.out.println("  " + notif);
            }
        }
    }

    private static void lihatPesananSedangDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");
    }

    private static void selesaiDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");

        System.out.print("Masukkan ID pesanan yang selesai dimasak: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "SELESAI DIMASAK", currentUsername, currentRole);
        if (ok) {
            System.out.println("✅ Pesanan #" + id + " sudah SELESAI DIMASAK.");
            System.out.println("📤 Notifikasi telah dikirim ke Pelayan dan Kasir!");
        } else {
            System.out.println("❌ Pesanan ID tidak ditemukan.");
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
