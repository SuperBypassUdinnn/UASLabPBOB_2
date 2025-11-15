package main.java.com.restaurant.app;

import java.util.Scanner;

import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.utils.InputUtil;

public class MainKoki {

    private static Scanner sc = InputUtil.sc;
    private static RestaurantSystem rs = RestaurantSystem.getInstance();

    public static void run() {
        while (true) {
            System.out.println("\n===== MENU KOKI =====");
            System.out.println("1. Lihat Pesanan SEDANG DIMASAK");
            System.out.println("2. Tandai Pesanan Selesai");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatDimasak();
                    break;
                case 2:
                    selesaiDimasak();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");
    }

    private static void selesaiDimasak() {
        System.out.println("\n=== PESANAN SEDANG DIMASAK ===");
        rs.tampilPesananDenganStatus("SEDANG DIMASAK");

        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();

        rs.updateStatusPesanan(id, "SELESAI DIMASAK");
        rs.saveData();

        System.out.println("Pesanan #" + id + " selesai dimasak dan siap diantar.");
    }
}
