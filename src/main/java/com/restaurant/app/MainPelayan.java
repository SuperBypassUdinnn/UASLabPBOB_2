package main.java.com.restaurant.app;

import java.util.Scanner;

import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.utils.InputUtil;

public class MainPelayan {

    private static Scanner sc = InputUtil.sc;
    private static RestaurantSystem rs = RestaurantSystem.getInstance();

    public static void run() {
        while (true) {
            System.out.println("\n===== MENU PELAYAN =====");
            System.out.println("1. Lihat Pesanan MENUNGGU");
            System.out.println("2. Proses Pesanan → SEDANG DIMASAK");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatMenunggu();
                    break;
                case 2:
                    prosesPesanan();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatMenunggu() {
        System.out.println("\n=== PESANAN MENUNGGU ===");
        rs.tampilPesananDenganStatus("MENUNGGU");
    }

    private static void prosesPesanan() {
        System.out.println("\n=== PESANAN MENUNGGU ===");
        rs.tampilPesananDenganStatus("MENUNGGGU");

        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();

        rs.updateStatusPesanan(id, "SEDANG DIMASAK");
        rs.saveData();

        System.out.println("Pesanan #" + id + " diteruskan ke koki.");
    }
}
