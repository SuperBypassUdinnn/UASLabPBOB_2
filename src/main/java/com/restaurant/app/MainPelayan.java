package main.java.com.restaurant.app;

import java.util.Scanner;
import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.model.pesanan.*;

public class MainPelayan {

    public static void main(String[] args) {
        RestaurantSystem rs = RestaurantSystem.getInstance();
        Scanner sc = new Scanner(System.in);

        if (rs.draftCustomer == null) {
            System.out.println("Belum ada pesanan dari customer!");
            sc.close();
            return;
        }

        System.out.println("=== MENU PELAYAN ===");
        System.out.print("Masukkan nomor meja: ");
        int noMeja = sc.nextInt();

        Pesanan p = rs.buatPesanan(noMeja);
        p.tambahItem(rs.draftCustomer);

        System.out.println("Pesanan dibuat dengan ID: " + p.getId());
        rs.draftCustomer = null;

        sc.close();
    }
}
