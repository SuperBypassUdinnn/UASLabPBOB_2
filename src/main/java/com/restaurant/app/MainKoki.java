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
            System.out.println("1. Lihat Pesanan DIPROSES (Masuk Dapur)");
            System.out.println("2. Mulai Memasak -> SEDANG DIMASAK");
            System.out.println("3. Lihat Pesanan SEDANG DIMASAK");
            System.out.println("4. Selesai Memasak -> SIAP DISAJIKAN");
            System.out.println("5. Lihat Notifikasi");
            System.out.println("6. Lihat Detail Pesanan");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatPesananDiproses();
                    break;
                case 2:
                    mulaiMemasak();
                    break;
                case 3:
                    lihatPesananSedangDimasak();
                    break;
                case 4:
                    selesaiDimasak();
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
        List<String> notifications = rs.getNotificationsForRole("koki");
        if (!notifications.isEmpty()) {
            System.out.println("\n📢 NOTIFIKASI TERBARU:");
            for (String notif : notifications) {
                System.out.println("  " + notif);
            }
        }
    }

    /**
     * Koki melihat pesanan yang baru masuk ke dapur (dari pelayan)
     */
    private static void lihatPesananDiproses() {
        System.out.println("\n=== PESANAN DIPROSES (Baru Masuk Dapur) ===");
        rs.tampilPesananDenganStatus("DIPROSES");
    }

    /**
     * Koki mulai memasak pesanan
     * Status: DIPROSES -> SEDANG DIMASAK
     */
    private static void mulaiMemasak() {
        System.out.println("\n=== PESANAN DIPROSES ===");
        rs.tampilPesananDenganStatus("DIPROSES");

        System.out.print("Masukkan ID pesanan yang akan mulai dimasak: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "SEDANG DIMASAK", currentUsername, currentRole);
        if (ok) {
            System.out.println("✅ Pesanan #" + id + " sedang dimasak (Status: SEDANG DIMASAK).");
            System.out.println("📤 Notifikasi telah dikirim ke Customer!");
        } else {
            System.out.println("❌ Pesanan dengan ID " + id + " tidak ditemukan atau status tidak valid.");
        }
    }

    private static void lihatPesananSedangDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");
    }

    /**
     * Koki selesai memasak
     * Status: SEDANG DIMASAK -> SIAP DISAJIKAN
     */
    private static void selesaiDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");

        System.out.print("Masukkan ID pesanan yang selesai dimasak: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "SIAP DISAJIKAN", currentUsername, currentRole);
        if (ok) {
            System.out.println("✅ Pesanan #" + id + " selesai dimasak, siap disajikan (Status: SIAP DISAJIKAN).");
            System.out.println("📤 Notifikasi telah dikirim ke Pelayan dan Customer!");
        } else {
            System.out.println("❌ Pesanan ID tidak ditemukan atau status tidak valid.");
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
