package com.restaurant.app;

import java.util.Scanner;

import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.model.transaksi.*;
import com.restaurant.model.Struk;
import com.restaurant.service.RestaurantSystem;
import com.restaurant.utils.InputUtil;

public class MainKasir {

    private static Scanner sc = InputUtil.sc;
    private static RestaurantSystem rs = RestaurantSystem.getInstance();

    public static void run() {
        while (true) {
            System.out.println("\n===== MENU KASIR =====");
            System.out.println("1. Lihat Pesanan Siap Bayar (SELESAI DIMASAK)");
            System.out.println("2. Proses Pembayaran");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");

            int p = sc.nextInt();
            sc.nextLine();

            switch (p) {
                case 1:
                    lihatSiapBayar();
                    break;
                case 2:
                    prosesPembayaran();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSiapBayar() {
        System.out.println("\n=== PESANAN SELESAI DIMASAK ===");
        rs.tampilPesananDenganStatus("SELESAI DIMASAK");
    }

    private static void prosesPembayaran() {
        System.out.println("\n=== PILIH PESANAN UNTUK DIBAYAR ===");
        rs.tampilPesananDenganStatus("SELESAI DIMASAK");

        System.out.print("Masukkan ID pesanan: ");
        int id = sc.nextInt();
        sc.nextLine();

        Pesanan p = rs.getPesananById(id);
        if (p == null) {
            System.out.println("ID tidak ditemukan.");
            return;
        }

        System.out.println("Total bayar: Rp" + p.getTotal());
        System.out.println("Metode:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. QRIS");

        System.out.print("Pilih metode: ");
        int m = sc.nextInt();
        sc.nextLine();

        Pembayaran pb;

        switch (m) {
            case 1:
                System.out.print("Masukkan uang: ");
                double uang = sc.nextDouble();
                sc.nextLine();
                pb = new CashPayment(uang);
                break;
            case 2:
                pb = new CardPayment();
                break;
            case 3:
                pb = new QRISPayment();
                break;
            default:
                System.out.println("Metode tidak valid.");
                return;
        }

        Transaksi t = rs.buatTransaksi(p, pb);

        if (t.konfirmasi()) {
            Struk.cetak(t);
            rs.updateStatusPesanan(id, "SELESAI");
            rs.saveData();
            System.out.println("Pembayaran berhasil!");
        } else {
            System.out.println("Pembayaran gagal.");
        }
    }
}
