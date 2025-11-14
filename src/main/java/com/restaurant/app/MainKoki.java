package main.java.com.restaurant.app;

import java.util.Scanner;
import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.model.pesanan.Pesanan;

public class MainKoki {

    public static void main(String[] args) {
        RestaurantSystem rs = RestaurantSystem.getInstance();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== MENU KOKI ===");

        rs.tampilPesananMenunggu();

        System.out.print("Masukkan ID pesanan yang selesai dimasak: ");
        int id = sc.nextInt();

        Pesanan p = rs.getPesananById(id);
        if (p == null) {
            System.out.println("Pesanan tidak ditemukan!");
        } else {
            p.setStatus("SELESAI DIMASAK");
            System.out.println("Status pesanan diperbarui!");
        }

        sc.close();
    }
}
