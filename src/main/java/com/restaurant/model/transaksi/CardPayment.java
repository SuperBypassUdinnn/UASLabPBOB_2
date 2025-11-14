package main.java.com.restaurant.model.transaksi;

public class CardPayment implements Pembayaran {

    @Override
    public boolean proses(double total) {
        System.out.println("Pembayaran kartu berhasil (simulasi)");
        return true;
    }
}
