package main.java.com.restaurant.model.transaksi;

public class CardPayment extends Pembayaran {

    public CardPayment() {
        this.jenisPembayaran = "Kartu Debit/Kredit";
    }

    @Override
    public boolean prosesPembayaran(double total) {
        System.out.println("Pembayaran dengan kartu berhasil.");
        return true;
    }
}
