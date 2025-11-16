package com.restaurant.app;

import java.util.Scanner;

import com.restaurant.utils.InputUtil;

public class Main {

    public static void main(String[] args) {

        Scanner sc = InputUtil.sc;

        while (true) {
            System.out.println("\n===== SISTEM RESTORAN =====");
            System.out.println("1. Customer");
            System.out.println("2. Pelayan");
            System.out.println("3. Koki");
            System.out.println("4. Kasir");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");

            if (!sc.hasNextInt()) {
                System.out.println("Input tidak valid!");
                sc.nextLine();
                continue;
            }

            int pilih = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (pilih) {
                case 1:
                    MainCustomer.run();
                    break;

                case 2:
                    MainPelayan.run();
                    break;

                case 3:
                    MainKoki.run();
                    break;

                case 4:
                    MainKasir.run();
                    break;

                case 0:
                    System.out.println("Terima kasih! Program selesai.");
                    return;

                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
}
