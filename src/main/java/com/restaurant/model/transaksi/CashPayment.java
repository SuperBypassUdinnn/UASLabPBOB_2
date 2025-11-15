package main.java.com.restaurant.model.transaksi;

public class CashPayment extends Pembayaran {

    private double jumlahUang;

    public CashPayment(double jumlahUang) {
        this.jumlahUang = jumlahUang;
        this.jenisPembayaran = "Cash";
    }

    @Override
    public boolean prosesPembayaran(double total) {
        if (jumlahUang < total) {
            System.out.println("Uang tidak cukup! Total: Rp" + total + ", Anda bayar: Rp" + jumlahUang);
            return false;
        }

        double kembalian = jumlahUang - total;
        System.out.println("Pembayaran cash berhasil.");
        System.out.println("Kembalian: Rp" + kembalian);
        return true;
    }
}
