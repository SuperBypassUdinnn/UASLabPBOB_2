package main.java.com.restaurant.app;

import java.util.Scanner;
import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;

public class MainCustomer {

    public static void main(String[] args) {
        RestaurantSystem rs = RestaurantSystem.getInstance();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== MENU CUSTOMER ===");
        rs.tampilMenu();

        System.out.print("Pilih menu (nomor): ");
        int pilih = sc.nextInt() - 1;

        System.out.print("Jumlah: ");
        int jumlah = sc.nextInt();
        sc.nextLine();

        System.out.print("Catatan: ");
        String catatan = sc.nextLine();

        MenuItem item = rs.getMenu(pilih);

        // simpan draft pesanan customer
        rs.draftCustomer = new DetailPesanan(item, jumlah, catatan);

        System.out.println("Pesanan dikirim ke Pelayan!");
        return;
    }
}
