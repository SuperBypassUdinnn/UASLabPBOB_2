package com.restaurant.model.pesanan;

import com.restaurant.model.menu.MenuItem;

public class DetailPesanan {

    private final MenuItem menu;
    private int jumlah;
    // Catatan dihapus dari sini, pindah ke class Pesanan

    public DetailPesanan(MenuItem menu, int jumlah) {
        this.menu = menu;
        this.jumlah = jumlah;
    }

    public MenuItem getMenu() {
        return menu;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getSubtotal() {
        return menu.getHarga() * jumlah;
    }

    @Override
    public String toString() {
        return menu.getNama() + " x" + jumlah + " (Rp" + (int) getSubtotal() + ")";
    }
}