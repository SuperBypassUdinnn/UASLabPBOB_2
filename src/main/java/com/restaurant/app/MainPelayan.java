package com.restaurant.app;

import java.util.Scanner;

import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainPelayan {

    private static Scanner sc = InputUtil.sc;
    private static RestaurantSystem rs = RestaurantSystem.getInstance();

    public static void run() {
        while (true) {
            System.out.println("\n===== MENU PELAYAN =====");
            System.out.println("1. Lihat Pesanan MENUNGGU");
            System.out.println("2. Proses Pesanan -> SEDANG DIMASAK");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    rs.tampilPesananDenganStatus("MENUNGGU");
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

    private static void prosesPesanan() {
        System.out.println("\n=== PESANAN MENUNGGU ===");
        rs.tampilPesananDenganStatus("MENUNGGU");

        System.out.print("Masukkan ID pesanan yang akan diproses: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean ok = rs.updateStatusPesanan(id, "SEDANG DIMASAK");
        if (ok) {
            System.out.println("Pesanan #" + id + " status menjadi SEDANG DIMASAK.");
        } else {
            System.out.println("Pesanan dengan ID " + id + " tidak ditemukan.");
        }
    }
}
