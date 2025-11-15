package main.java.com.restaurant.service;

import java.io.*;
import java.util.*;

import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;
import main.java.com.restaurant.model.transaksi.*;

public class FileStorageService {

    private static final String MENU_FILE = "src/main/resources/data/menu.txt";
    private static final String PESANAN_FILE = "src/main/resources/data/pesanan.txt";
    private static final String TRANSAKSI_FILE = "src/main/resources/data/transaksi.txt";

    // =====================================================
    // =============== LOAD MENU =========================
    // =====================================================
    public static List<MenuItem> loadMenu() {
        List<MenuItem> hasil = new ArrayList<>();

        File f = new File(MENU_FILE);
        if (!f.exists()) {
            System.out.println("[WARNING] File menu.txt tidak ditemukan! Membuat data dummy.");
            return dummyMenu();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] data = line.split(";");
                String jenis = data[0].trim();

                if (jenis.equalsIgnoreCase("makanan")) {
                    hasil.add(new Makanan(
                            data[1], // nama
                            Double.parseDouble(data[2]), // harga
                            data[3], // kategori
                            data[4] // tingkat pedas
                    ));
                } else if (jenis.equalsIgnoreCase("minuman")) {
                    hasil.add(new Minuman(
                            data[1],
                            Double.parseDouble(data[2]),
                            data[3], // size: Small/Medium/Large
                            data[4] // panas/dingin
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
    }

    // =====================================================
    // =============== LOAD PESANAN =======================
    // =====================================================
    public static List<Pesanan> loadPesanan() {
        List<Pesanan> list = new ArrayList<>();

        File f = new File(PESANAN_FILE);
        if (!f.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                // Format:
                // id|meja|status|namaMenu,jumlah,catatan#namaMenu,jumlah,catatan
                String[] p = line.split("\\|");

                int id = Integer.parseInt(p[0]);
                int meja = Integer.parseInt(p[1]);
                String status = p[2];

                Pesanan pes = new Pesanan(id, new Meja(meja));
                pes.setStatus(status);

                if (p.length > 3 && !p[3].trim().isEmpty()) {
                    String[] items = p[3].split("#");

                    for (String item : items) {
                        String[] i = item.split(",");
                        String nama = i[0];
                        int jumlah = Integer.parseInt(i[1]);
                        String catatan = i[2];

                        // cari menu berdasarkan nama
                        MenuItem mi = findMenuByName(nama);
                        if (mi != null) {
                            pes.tambahItem(new DetailPesanan(mi, jumlah, catatan));
                        }
                    }
                }

                list.add(pes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =====================================================
    // =============== SAVE PESANAN =======================
    // =====================================================
    public static void savePesanan(List<Pesanan> list, int nextId) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PESANAN_FILE))) {

            for (Pesanan p : list) {
                StringBuilder sb = new StringBuilder();

                sb.append(p.getId()).append("|");
                sb.append(p.getMeja().getNomor()).append("|");
                sb.append(p.getStatus()).append("|");

                for (DetailPesanan d : p.getItems()) {
                    sb.append(d.getMenu().getNama()).append(",");
                    sb.append(d.getJumlah()).append(",");
                    sb.append(d.getCatatan());
                    sb.append("#");
                }

                pw.println(sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // save last ID
        try (PrintWriter idw = new PrintWriter(new FileWriter("src/main/resources/data/id.txt"))) {
            idw.println(nextId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // =============== LOAD LAST ID ========================
    // =====================================================
    public static int loadLastId() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/data/id.txt"))) {
            String s = br.readLine();
            if (s != null)
                return Integer.parseInt(s.trim());
        } catch (Exception e) {
        }
        return 1;
    }

    // =====================================================
    // SAVE TRANSAKSI
    // =====================================================
    public static void saveTransaksi(Transaksi t) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSAKSI_FILE, true))) {

            pw.println(
                    t.getPesanan().getId() + "|" +
                            t.getPesanan().getMeja().getNomor() + "|" +
                            t.getTotal() + "|" +
                            t.getPembayaran().getJenis());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // HELPER: cari menu berdasarkan nama
    // =====================================================
    private static MenuItem findMenuByName(String nama) {
        List<MenuItem> menu = RestaurantSystem.getInstance().getMenuList();

        for (MenuItem m : menu) {
            if (m.getNama().equalsIgnoreCase(nama)) {
                return m;
            }
        }

        return null;
    }

    // =====================================================
    // DATA MENU DUMMY (jika file menu.txt tidak ada)
    // =====================================================
    private static List<MenuItem> dummyMenu() {
        List<MenuItem> d = new ArrayList<>();

        d.add(new Makanan("Mie Aceh", 25000, "Main Course", "Sedang"));
        d.add(new Makanan("Ayam Tangkap", 35000, "Main Course", "Tidak pedas"));
        d.add(new Makanan("Sie Reuboh", 30000, "Traditional", "Pedas"));
        d.add(new Minuman("Kopi Aceh", 15000, "Medium", "Panas"));

        return d;
    }
}
