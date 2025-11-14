package main.java.com.restaurant.model.transaksi;

public class QRISPayment implements Pembayaran {

    @Override
    public boolean proses(double total) {
        System.out.println("Scan QRIS berhasil (simulasi)");
        return true;
    }
}
