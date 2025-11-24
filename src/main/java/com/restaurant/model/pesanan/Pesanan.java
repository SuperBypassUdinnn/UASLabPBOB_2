package com.restaurant.model.pesanan;

import java.util.ArrayList;
import java.util.List;

public class Pesanan {

    private int id;
    private Meja meja;
    private String namaPelanggan;
    private String status;
    private String catatan; // PINDAHAN: Catatan global per pesanan
    private List<DetailPesanan> items;

    public Pesanan(int id, Meja meja, String namaPelanggan) {
        this.id = id;
        this.meja = meja;
        this.namaPelanggan = namaPelanggan;
        this.status = "MENUNGGU";
        this.catatan = "-";
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Meja getMeja() {
        return meja;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<DetailPesanan> getItems() {
        return items;
    }

    public void tambahItem(DetailPesanan dp) {
        items.add(dp);
    }

    public double getTotal() {
        double total = 0;
        for (DetailPesanan d : items) {
            total += d.getSubtotal();
        }
        return total;
    }

    public String renderDetail() {
        StringBuilder sb = new StringBuilder();
        if (catatan != null && !catatan.isEmpty() && !catatan.equals("-")) {
            sb.append("Note: ").append(catatan).append("\n");
        }
        for (DetailPesanan d : items) {
            sb.append("- ").append(d.toString()).append("\n");
        }
        return sb.toString();
    }
}