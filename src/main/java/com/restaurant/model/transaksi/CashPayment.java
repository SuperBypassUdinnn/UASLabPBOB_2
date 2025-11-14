package main.java.com.restaurant.model.transaksi;

public class CashPayment implements Pembayaran {
    private double uangDiberikan;

    public CashPayment(double uangDiberikan) {
        this.uangDiberikan = uangDiberikan;
    }

    @Override
    public boolean proses(double total) {
        return uangDiberikan >= total;
    }

    public double getKembalian(double total) {
        return uangDiberikan - total;
    }
}
