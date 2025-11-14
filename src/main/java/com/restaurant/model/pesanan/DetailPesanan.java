package main.java.com.restaurant.model.pesanan;

import main.java.com.restaurant.model.menu.MenuItem;

public class DetailPesanan {
    private MenuItem item;
    private int jumlah;
    private String catatan;

    public DetailPesanan(MenuItem item, int jumlah, String catatan) {
        this.item = item;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    public double getSubtotal() {
        return item.getHarga() * jumlah;
    }

    public String toString() {
        return item.getNama() + " x" + jumlah + " = Rp" + getSubtotal();
    }
}
