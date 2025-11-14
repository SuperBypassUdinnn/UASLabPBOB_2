package main.java.com.restaurant.model;

import main.java.com.restaurant.model.transaksi.Transaksi;
import main.java.com.restaurant.model.pesanan.DetailPesanan;

public class Struk {

    public static void cetak(Transaksi t) {
        System.out.println("====== STRUK PEMBAYARAN ======");
        System.out.println("ID Transaksi: " + t.getId());
        System.out.println("Meja: " + t.getPesanan().getMeja().getNomor());
        System.out.println();

        for (DetailPesanan dp : t.getPesanan().getDetail()) {
            System.out.println(dp);
        }

        System.out.println("------------------------------");
        System.out.println("Total: Rp" + t.getPesanan().getTotal());
        System.out.println("==============================");
    }
}
