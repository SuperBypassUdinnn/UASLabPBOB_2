package main.java.com.restaurant.app;

import java.util.Scanner;

import main.java.com.restaurant.model.*;
import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;
import main.java.com.restaurant.model.transaksi.*;
import main.java.com.restaurant.service.RestaurantSystem;

public class Main {
    public static void main(String[] args) {

        RestaurantSystem rs = new RestaurantSystem();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== SISTEM RESTORAN ===");

        rs.tampilMenu();

        System.out.print("Pilih menu (index): ");
        int index = sc.nextInt() - 1;

        System.out.print("Jumlah: ");
        int jumlah = sc.nextInt();

        MenuItem item = rs.getMenu(index);

        Pesanan p = rs.buatPesanan(1, 3);
        p.tambahItem(new DetailPesanan(item, jumlah, ""));

        System.out.println("Total pesanan: Rp" + p.getTotal());
        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. QRIS");
        int pay = sc.nextInt();

        Pembayaran metode;

        if (pay == 1) {
            System.out.print("Masukkan uang: ");
            metode = new CashPayment(sc.nextDouble());
        } else if (pay == 2) {
            metode = new CardPayment();
        } else {
            metode = new QRISPayment();
        }

        Transaksi t = new Transaksi(1001, p, metode);

        if (t.konfirmasi()) {
            Struk.cetak(t);
        } else {
            System.out.println("Pembayaran gagal!");
        }

        sc.close();
    }
}
