package main.java.com.restaurant.app;

import java.util.Scanner;

import main.java.com.restaurant.service.RestaurantSystem;
import main.java.com.restaurant.model.*;
import main.java.com.restaurant.model.pesanan.*;
import main.java.com.restaurant.model.transaksi.*;

public class MainKasir {

    public static void main(String[] args) {
        RestaurantSystem rs = RestaurantSystem.getInstance();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== MENU KASIR ===");

        rs.tampilPesananSiapBayar();

        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();

        Pesanan p = rs.getPesananById(id);
        if (p == null || !p.getStatus().equals("SELESAI DIMASAK")) {
            System.out.println("Pesanan tidak siap dibayar.");
            sc.close();
            return;
        }

        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. QRIS");
        int metode = sc.nextInt();

        Pembayaran pm;

        switch (metode) {
            case 1:
                System.out.print("Uang diberikan: ");
                pm = new CashPayment(sc.nextDouble());
                break;
            case 2:
                pm = new CardPayment();
                break;
            default:
                pm = new QRISPayment();
                break;
        }

        Transaksi t = rs.buatTransaksi(p, pm);

        if (t.konfirmasi()) {
            Struk.cetak(t);
            p.setStatus("SELESAI");
        } else {
            System.out.println("Pembayaran gagal!");
        }

        sc.close();
    }
}
