package main.java.com.restaurant.model.pesanan;

import java.util.ArrayList;
import java.util.List;

public class Pesanan {
    private int id;
    private Meja meja;
    private String status = "MENUNGGU";
    private List<DetailPesanan> detail = new ArrayList<>();

    public Pesanan(int id, Meja meja) {
        this.id = id;
        this.meja = meja;
    }

    public void tambahItem(DetailPesanan dp) {
        detail.add(dp);
    }

    public double getTotal() {
        return detail.stream().mapToDouble(DetailPesanan::getSubtotal).sum();
    }

    public List<DetailPesanan> getDetail() {
        return detail;
    }

    public int getId() {
        return id;
    }

    public Meja getMeja() {
        return meja;
    }
}
