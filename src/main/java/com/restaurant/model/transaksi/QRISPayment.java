package main.java.com.restaurant.model.transaksi;

public class QRISPayment extends Pembayaran {

    public QRISPayment() {
        this.jenisPembayaran = "QRIS";
    }

    @Override
    public boolean prosesPembayaran(double total) {
        System.out.println("QRIS berhasil diproses.");
        return true;
    }
}
