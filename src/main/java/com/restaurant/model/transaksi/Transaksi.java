package main.java.com.restaurant.model.transaksi;

import main.java.com.restaurant.model.pesanan.Pesanan;

public class Transaksi {
    private int id;
    private Pesanan pesanan;
    private Pembayaran pembayaran;

    public Transaksi(int id, Pesanan pesanan, Pembayaran pembayaran) {
        this.id = id;
        this.pesanan = pesanan;
        this.pembayaran = pembayaran;
    }

    public boolean konfirmasi() {
        return pembayaran.proses(pesanan.getTotal());
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public int getId() {
        return id;
    }
}
