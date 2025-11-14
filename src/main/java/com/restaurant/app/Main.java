package main.java.com.restaurant.app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== SISTEM RESTORAN =====");
            System.out.println("Pilih role:");
            System.out.println("1. Customer");
            System.out.println("2. Pelayan");
            System.out.println("3. Koki");
            System.out.println("4. Kasir");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            int pilih = sc.nextInt();

            switch (pilih) {
                case 1:
                    MainCustomer.main(null);
                    break;
                case 2:
                    MainPelayan.main(null);
                    break;
                case 3:
                    MainKoki.main(null);
                    break;
                case 4:
                    MainKasir.main(null);
                    break;
                case 0:
                    System.out.println("Keluar...");
                    sc.close();
                    return;
                default:
                    System.out.println("Pilihan tidak ada.");
            }
        }
    }
}
