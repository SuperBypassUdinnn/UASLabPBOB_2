package com.restaurant.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.restaurant.model.menu.*;
import com.restaurant.model.pesanan.*;
import com.restaurant.model.transaksi.*;

public class FileStorageService {

    private static final String MENU_FILE = "src/main/resources/data/menu.txt";
    private static final String PESANAN_FILE = "src/main/resources/data/pesanan.txt";
    private static final String TRANSAKSI_FILE = "src/main/resources/data/transaksi.txt";
    private static final String ID_FILE = "src/main/resources/data/id.txt";

    // -----------------------
    // LOAD MENU
    // -----------------------
    public static List<MenuItem> loadMenu() {
        List<MenuItem> hasil = new ArrayList<>();
        File f = new File(MENU_FILE);
        if (!f.exists() || f.length() == 0) {
            // buat dummy & tulis file
            hasil = dummyMenu();
            try {
                saveMenu(hasil);
            } catch (IOException e) {
                System.err.println("[FileStorageService] Gagal menulis menu dummy: " + e.getMessage());
            }
            return hasil;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] arr = line.split(";");
                if (arr.length < 5) {
                    System.err.println("[FileStorageService] Baris menu invalid, dilewati: " + line);
                    continue;
                }
                String jenis = arr[0].trim().toLowerCase();
                String nama = arr[1].trim();
                double harga;
                try {
                    harga = Double.parseDouble(arr[2].trim());
                } catch (NumberFormatException ex) {
                    System.err.println("[FileStorageService] Harga invalid: " + line);
                    continue;
                }

                if ("makanan".equals(jenis)) {
                    hasil.add(new Makanan(nama, harga, arr[3].trim(), arr[4].trim()));
                } else if ("minuman".equals(jenis)) {
                    hasil.add(new Minuman(nama, harga, arr[3].trim(), arr[4].trim()));
                } else {
                    System.err.println("[FileStorageService] Jenis menu tidak dikenal: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hasil.isEmpty()) {
            hasil = dummyMenu();
            try {
                saveMenu(hasil);
            } catch (IOException e) {
                /* log */ }
        }
        return hasil;
    }

    public static void saveMenu(List<MenuItem> menu) throws IOException {
        File f = new File(MENU_FILE);
        f.getParentFile().mkdirs();
        // atomic write: tulis ke temp lalu pindahkan
        Path temp = Files.createTempFile(f.getParentFile().toPath(), "menu", ".tmp");
        try (PrintWriter pw = new PrintWriter(new FileWriter(temp.toFile()))) {
            for (MenuItem m : menu) {
                if (m instanceof Makanan) {
                    Makanan mm = (Makanan) m;
                    pw.printf("makanan;%s;%.0f;%s;%s%n", mm.getNama(), mm.getHarga(), mm.getKategori(),
                            mm.getTingkatPedas());
                } else if (m instanceof Minuman) {
                    Minuman mn = (Minuman) m;
                    pw.printf("minuman;%s;%.0f;%s;%s%n", mn.getNama(), mn.getHarga(), mn.getUkuran(), mn.getSuhu());
                } else {
                    pw.printf("lain;%s;%.0f;-%n", m.getNama(), m.getHarga());
                }
            }
        }
        Files.move(temp, f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.ATOMIC_MOVE);
    }

    // -----------------------
    // LOAD PESANAN
    // format: id|meja|status|nama,jumlah,catatan#nama,jumlah,catatan
    // -----------------------
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
                try {
                    String[] p = line.split("\\|", -1);
                    int id = Integer.parseInt(p[0].trim());
                    int meja = Integer.parseInt(p[1].trim());
                    String status = p[2].trim();

                    Pesanan pes = new Pesanan(id, new Meja(meja));
                    pes.setStatus(status);

                    if (p.length > 3 && !p[3].trim().isEmpty()) {
                        String[] items = p[3].split("#");
                        for (String item : items) {
                            if (item.trim().isEmpty())
                                continue;
                            String[] parts = item.split(",", -1);
                            // parts: nama, jumlah, catatan
                            String nama = parts[0];
                            int jumlah = Integer.parseInt(parts[1]);
                            String catatan = parts.length > 2 ? parts[2] : "";

                            MenuItem mi = findMenuByName(nama);
                            if (mi != null) {
                                pes.tambahItem(new DetailPesanan(mi, jumlah, catatan));
                            } else {
                                System.err.println(
                                        "[FileStorageService] Menu '" + nama + "' tidak ditemukan saat load pesanan.");
                            }
                        }
                    }
                    list.add(pes);
                } catch (Exception e) {
                    System.err.println("[FileStorageService] Gagal parse baris pesanan (dilewati): " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // -----------------------
    // SAVE PESANAN (atomic, overwrite keseluruhan daftar)
    // -----------------------
    public static void savePesanan(List<Pesanan> list, int nextId) {
        try {
            File f = new File(PESANAN_FILE);
            f.getParentFile().mkdirs();
            Path temp = Files.createTempFile(f.getParentFile().toPath(), "pesanan", ".tmp");
            try (PrintWriter pw = new PrintWriter(new FileWriter(temp.toFile()))) {
                for (Pesanan p : list) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.getId()).append("|");
                    sb.append(p.getMeja().getNomor()).append("|");
                    sb.append(p.getStatus()).append("|");
                    // items
                    boolean first = true;
                    for (DetailPesanan d : p.getItems()) {
                        if (!first)
                            sb.append("#");
                        // escape commas/pipes if necessary (simple replace)
                        String nama = d.getMenu().getNama().replace("|", " ").replace(",", " ");
                        String cat = d.getCatatan() == null ? "" : d.getCatatan().replace("|", " ").replace(",", " ");
                        sb.append(nama).append(",").append(d.getJumlah()).append(",").append(cat);
                        first = false;
                    }
                    pw.println(sb.toString());
                }
            }
            Files.move(temp, f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);

            // simpan nextId
            File idf = new File(ID_FILE);
            try (PrintWriter idw = new PrintWriter(new FileWriter(idf))) {
                idw.println(nextId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // LOAD LAST ID
    // -----------------------
    public static int loadLastId() {
        File f = new File(ID_FILE);
        if (!f.exists())
            return 1;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String s = br.readLine();
            if (s != null && !s.trim().isEmpty())
                return Integer.parseInt(s.trim());
        } catch (Exception e) {
        }
        return 1;
    }

    // -----------------------
    // SAVE TRANSAKSI (append, tiap transaksi satu baris)
    // format: idPesanan|noMeja|total|jenis|waktu
    // -----------------------
    public static void saveTransaksi(Transaksi t) {
        try {
            File f = new File(TRANSAKSI_FILE);
            f.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                pw.printf("%d|%d|%.0f|%s|%s%n",
                        t.getPesanan().getId(),
                        t.getPesanan().getMeja().getNomor(),
                        t.getTotal(),
                        t.getPembayaran().getJenis(),
                        t.getWaktuFormatted());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // HELPER: cari menu by name
    // -----------------------
    private static MenuItem findMenuByName(String nama) {
        List<MenuItem> menu = RestaurantSystem.getInstance().getMenuList();
        for (MenuItem m : menu) {
            if (m.getNama().equalsIgnoreCase(nama))
                return m;
        }
        return null;
    }

    // -----------------------
    // Dummy menu
    // -----------------------
    private static List<MenuItem> dummyMenu() {
        List<MenuItem> d = new ArrayList<>();
        d.add(new Makanan("Mie Aceh", 25000, "Main Course", "Sedang"));
        d.add(new Makanan("Mie Aceh Kepiting", 45000, "Main Course", "Pedas"));
        d.add(new Minuman("Es Teh", 8000, "Medium", "Dingin"));
        return d;
    }
}
