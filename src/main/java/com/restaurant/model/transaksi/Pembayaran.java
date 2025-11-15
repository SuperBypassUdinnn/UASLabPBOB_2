package main.java.com.restaurant.model.transaksi;

public abstract class Pembayaran {

    protected String jenisPembayaran;

    public String getJenis() {
        return jenisPembayaran;
    }

    // proses pembayaran → return true jika berhasil
    public abstract boolean prosesPembayaran(double total);
}
