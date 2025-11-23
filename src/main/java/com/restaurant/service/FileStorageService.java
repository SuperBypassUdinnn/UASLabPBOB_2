package com.restaurant.service;

import java.io.*;
import java.util.*;

import com.restaurant.model.menu.*;
import com.restaurant.model.pesanan.*;
import com.restaurant.model.transaksi.*;
import com.restaurant.utils.JsonUtil;

public class FileStorageService {

    private static final String MENU_FILE = "src/main/resources/data/menu.json";
    private static final String PESANAN_FILE = "src/main/resources/data/pesanan.json";
    private static final String TRANSAKSI_FILE = "src/main/resources/data/transaksi.json";

    // -----------------------
    // LOAD MENU (JSON Format)
    // Format JSON: { "menu": [ { "jenis": "makanan", "nama": "...", "harga": 25000,
    // ... }, ... ] }
    // -----------------------
    public static List<MenuItem> loadMenu() {
        List<MenuItem> hasil = new ArrayList<>();
        String json = JsonUtil.readFile(MENU_FILE);

        if (json.equals("{}")) {
            // File tidak ada atau kosong, buat dummy
            hasil = dummyMenu();
            try {
                saveMenu(hasil);
            } catch (IOException e) {
                System.err.println("[FileStorageService] Gagal menulis menu dummy: " + e.getMessage());
            }
            return hasil;
        }

        // Parse array menu dari JSON
        List<String> menuObjects = JsonUtil.parseArray(json, "menu");

        for (String menuObj : menuObjects) {
            try {
                String jenis = JsonUtil.getString(menuObj, "jenis");
                String nama = JsonUtil.getString(menuObj, "nama");
                double harga = JsonUtil.getDouble(menuObj, "harga");

                if (jenis == null || nama == null)
                    continue;

                if ("makanan".equalsIgnoreCase(jenis)) {
                    String kategori = JsonUtil.getString(menuObj, "kategori");
                    String tingkatPedas = JsonUtil.getString(menuObj, "tingkat_pedas");
                    if (kategori != null && tingkatPedas != null) {
                        hasil.add(new Makanan(nama, harga, kategori, tingkatPedas));
                    }
                } else if ("minuman".equalsIgnoreCase(jenis)) {
                    String ukuran = JsonUtil.getString(menuObj, "ukuran");
                    String suhu = JsonUtil.getString(menuObj, "suhu");
                    if (ukuran != null && suhu != null) {
                        hasil.add(new Minuman(nama, harga, ukuran, suhu));
                    }
                }
            } catch (Exception e) {
                System.err.println("[FileStorageService] Error parsing menu: " + menuObj);
            }
        }

        if (hasil.isEmpty()) {
            hasil = dummyMenu();
            try {
                saveMenu(hasil);
            } catch (IOException e) {
                // log error
            }
        }
        return hasil;
    }

    /**
     * Save menu ke JSON format
     */
    public static void saveMenu(List<MenuItem> menu) throws IOException {
        List<String> menuJsonList = new ArrayList<>();

        for (MenuItem m : menu) {
            if (m instanceof Makanan) {
                Makanan mm = (Makanan) m;
                String jsonObj = JsonUtil.jsonObject(
                        "jenis", "makanan",
                        "nama", mm.getNama(),
                        "harga", String.valueOf((int) mm.getHarga()),
                        "kategori", mm.getKategori(),
                        "tingkat_pedas", mm.getTingkatPedas());
                menuJsonList.add(jsonObj);
            } else if (m instanceof Minuman) {
                Minuman mn = (Minuman) m;
                String jsonObj = JsonUtil.jsonObject(
                        "jenis", "minuman",
                        "nama", mn.getNama(),
                        "harga", String.valueOf((int) mn.getHarga()),
                        "ukuran", mn.getUkuran(),
                        "suhu", mn.getSuhu());
                menuJsonList.add(jsonObj);
            }
        }

        String jsonArray = JsonUtil.jsonArray(menuJsonList);
        String json = JsonUtil.jsonWithRoot("menu", jsonArray);

        JsonUtil.writeFile(MENU_FILE, json);
    }

    // -----------------------
    // LOAD PESANAN (JSON Format)
    // Format JSON: { "nextId": 3, "pesanan": [ { "id": 1, "meja": 10, "status":
    // "MENUNGGU", "items": [...] }, ... ] }
    // Hanya pesanan aktif (tidak LUNAS) yang disimpan
    // -----------------------
    public static List<Pesanan> loadPesanan() {
        List<Pesanan> list = new ArrayList<>();
        String json = JsonUtil.readFile(PESANAN_FILE);

        if (json.equals("{}")) {
            return list; // File tidak ada, return empty list
        }

        // Parse array pesanan dari JSON
        List<String> pesananObjects = JsonUtil.parseArray(json, "pesanan");

        for (String pesananObj : pesananObjects) {
            try {
                int id = JsonUtil.getInt(pesananObj, "id");
                int meja = JsonUtil.getInt(pesananObj, "meja");
                String status = JsonUtil.getString(pesananObj, "status");

                if (status == null || status.isEmpty())
                    continue;

                // Skip pesanan yang sudah LUNAS (tidak seharusnya ada di file)
                if ("LUNAS".equals(status)) {
                    continue;
                }

                Pesanan pes = new Pesanan(id, new Meja(meja));
                pes.setStatus(status);

                // Parse items - cari pattern items di dalam pesananObj
                try {
                    List<String> items = JsonUtil.parseArray(pesananObj, "items");
                    for (String itemObj : items) {
                        String nama = JsonUtil.getString(itemObj, "nama");
                        int jumlah = JsonUtil.getInt(itemObj, "jumlah");
                        String catatan = JsonUtil.getString(itemObj, "catatan");
                        if (catatan == null)
                            catatan = "";

                        if (nama != null && jumlah > 0) {
                            MenuItem mi = findMenuByName(nama);
                            if (mi != null) {
                                pes.tambahItem(new DetailPesanan(mi, jumlah, catatan));
                            } else {
                                System.err.println(
                                        "[FileStorageService] Menu '" + nama + "' tidak ditemukan saat load pesanan.");
                            }
                        }
                    }
                } catch (Exception e) {
                    // Items mungkin kosong, skip
                }

                list.add(pes);
            } catch (Exception e) {
                System.err.println("[FileStorageService] Gagal parse pesanan: " + pesananObj);
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * Load next ID dari JSON pesanan
     */
    public static int loadLastId() {
        String json = JsonUtil.readFile(PESANAN_FILE);
        if (json.equals("{}")) {
            return 1; // Default ID
        }

        int nextId = JsonUtil.getRootInt(json, "nextId");
        if (nextId == 0) {
            // Calculate dari ID pesanan terbesar + 1
            List<Pesanan> pesanan = loadPesanan();
            int maxId = 0;
            for (Pesanan p : pesanan) {
                if (p.getId() > maxId) {
                    maxId = p.getId();
                }
            }
            return maxId + 1;
        }
        return nextId;
    }

    // -----------------------
    // SAVE PESANAN (JSON Format)
    // Format JSON: { "nextId": 3, "pesanan": [ { "id": 1, "meja": 10, "status":
    // "MENUNGGU", "items": [...] }, ... ] }
    // Hanya pesanan aktif yang disimpan (status != LUNAS)
    // -----------------------
    public static void savePesanan(List<Pesanan> list, int nextId) {
        try {
            List<String> pesananJsonList = new ArrayList<>();

            for (Pesanan p : list) {
                // Skip pesanan yang sudah LUNAS (tidak disimpan di pesanan.json)
                if ("LUNAS".equals(p.getStatus())) {
                    continue;
                }

                // Build items array
                List<String> itemsJsonList = new ArrayList<>();
                for (DetailPesanan d : p.getItems()) {
                    String itemJson = JsonUtil.jsonObject(
                            "nama", d.getMenu().getNama(),
                            "jumlah", String.valueOf(d.getJumlah()),
                            "catatan", d.getCatatan() == null ? "" : d.getCatatan());
                    itemsJsonList.add(itemJson);
                }

                String itemsArray = JsonUtil.jsonArray(itemsJsonList);

                // Build pesanan JSON object
                String pesananJson = JsonUtil.jsonObject(
                        "id", String.valueOf(p.getId()),
                        "meja", String.valueOf(p.getMeja().getNomor()),
                        "status", p.getStatus(),
                        "items", itemsArray);

                pesananJsonList.add(pesananJson);
            }

            String pesananArray = JsonUtil.jsonArray(pesananJsonList);
            String json = JsonUtil.jsonWithRoot("nextId", nextId, "pesanan", pesananArray);

            JsonUtil.writeFile(PESANAN_FILE, json);
        } catch (Exception e) {
            System.err.println("[FileStorageService] Error saving pesanan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------
    // SAVE TRANSAKSI (JSON Format, append ke array)
    // Format JSON: { "transaksi": [ { "idPesanan": 1, "noMeja": 10, "total": 90000,
    // ... }, ... ] }
    // Transaksi disimpan sebagai riwayat permanen
    // -----------------------
    public static void saveTransaksi(Transaksi t) {
        try {
            // Load existing transaksi
            String json = JsonUtil.readFile(TRANSAKSI_FILE);
            List<String> transaksiJsonList = new ArrayList<>();

            if (!json.equals("{}")) {
                transaksiJsonList = JsonUtil.parseArray(json, "transaksi");
            }

            // Build transaksi JSON object
            String transaksiJson = JsonUtil.jsonObject(
                    "idPesanan", String.valueOf(t.getPesanan().getId()),
                    "noMeja", String.valueOf(t.getPesanan().getMeja().getNomor()),
                    "total", String.valueOf((int) t.getTotal()),
                    "jenisPembayaran", t.getPembayaran().getJenis(),
                    "waktu", t.getWaktuFormatted(),
                    "status", "LUNAS");

            transaksiJsonList.add(transaksiJson);

            String transaksiArray = JsonUtil.jsonArray(transaksiJsonList);
            String newJson = JsonUtil.jsonWithRoot("transaksi", transaksiArray);

            JsonUtil.writeFile(TRANSAKSI_FILE, newJson);
        } catch (Exception e) {
            System.err.println("[FileStorageService] Error saving transaksi: " + e.getMessage());
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
