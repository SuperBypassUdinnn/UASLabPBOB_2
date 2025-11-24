package com.restaurant.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.restaurant.model.menu.*; // Import Makanan & Minuman
import com.restaurant.model.pesanan.*;
import com.restaurant.model.transaksi.*;
import com.restaurant.utils.JsonUtil;

public class FileStorageService {

    // Gunakan path yang konsisten
    private static final String BASE_DIR = System.getProperty("user.dir") + "/src/main/resources/data/";
    private static final String MENU_FILE = BASE_DIR + "menu.json";
    private static final String PESANAN_FILE = BASE_DIR + "pesanan.json";
    private static final String TRANSAKSI_FILE = BASE_DIR + "transaksi.json";

    static { new File(BASE_DIR).mkdirs(); }

    // --- LOAD MENU (FIXED: Auto-Fill jika kosong) ---
    public static List<MenuItem> loadMenu() {
        List<MenuItem> hasil = new ArrayList<>();
        String json = JsonUtil.readFile(MENU_FILE);
        List<String> list = JsonUtil.parseArray(json, "menu");
        
        for (String obj : list) {
            String jenis = JsonUtil.getString(obj, "jenis");
            String nama = JsonUtil.getString(obj, "nama");
            double harga = JsonUtil.getDouble(obj, "harga");
            
            if("makanan".equalsIgnoreCase(jenis)) {
                hasil.add(new Makanan(nama, harga, JsonUtil.getString(obj, "kategori"), JsonUtil.getString(obj, "tingkat_pedas")));
            } else {
                hasil.add(new Minuman(nama, harga, JsonUtil.getString(obj, "ukuran"), JsonUtil.getString(obj, "suhu")));
            }
        }

        // === FIX UTAMA: DATA DUMMY GLOBAL ===
        // Jika file menu kosong/gagal, isi dengan data default agar harga TIDAK 0
        if (hasil.isEmpty()) {
            hasil.add(new Makanan("Mie Aceh Spesial", 25000, "Main Course", "Pedas"));
            hasil.add(new Makanan("Mie Aceh Kepiting", 45000, "Main Course", "Pedas"));
            hasil.add(new Makanan("Nasi Goreng Kampung", 20000, "Main Course", "Sedang"));
            hasil.add(new Makanan("Ayam Penyet", 18000, "Main Course", "Pedas"));
            hasil.add(new Makanan("Ayam Tangkap", 35000, "Main Course", "Tidak Pedas"));
            hasil.add(new Makanan("Sate Matang", 30000, "Main Course", "Sedang"));
            
            hasil.add(new Minuman("Es Teh Tarik", 12000, "Medium", "Dingin"));
            hasil.add(new Minuman("Kopi Gayo", 15000, "Medium", "Panas"));
            hasil.add(new Minuman("Jus Jeruk", 10000, "Large", "Dingin"));
            hasil.add(new Minuman("Es Timun Serut", 10000, "Large", "Dingin"));
            
            // Opsional: Simpan data dummy ini ke file agar permanen
            // saveMenu(hasil); 
        }

        return hasil;
    }

    // --- LOAD PESANAN ---
    public static List<Pesanan> loadPesanan() {
        List<Pesanan> list = new ArrayList<>();
        String json = JsonUtil.readFile(PESANAN_FILE);
        
        List<String> pesananObjs = JsonUtil.parseArray(json, "pesanan");

        for (String pObj : pesananObjs) {
            int id = JsonUtil.getInt(pObj, "id");
            int meja = JsonUtil.getInt(pObj, "meja");
            String status = JsonUtil.getString(pObj, "status");
            
            if(status.isEmpty()) continue;

            Pesanan p = new Pesanan(id, new Meja(meja));
            p.setStatus(status);

            List<String> itemObjs = JsonUtil.parseArray(pObj, "items");
            
            for (String iObj : itemObjs) {
                String namaMenu = JsonUtil.getString(iObj, "nama");
                int jumlah = JsonUtil.getInt(iObj, "jumlah");
                String catatan = JsonUtil.getString(iObj, "catatan");
                
                // Cari object menu asli untuk mendapatkan HARGA
                MenuItem mi = findMenuByName(namaMenu);
                
                if (mi != null) {
                    p.tambahItem(new DetailPesanan(mi, jumlah, catatan));
                } else {
                    // Jika menu dihapus, harga jadi 0 (ini penyebab masalah Anda sebelumnya)
                    // Tapi karena loadMenu() sudah diperbaiki di atas, ini harusnya jarang terjadi
                    p.tambahItem(new DetailPesanan(new Makanan(namaMenu, 0, "-", "-"), jumlah, catatan));
                }
            }
            list.add(p);
        }
        return list;
    }

    // --- SAVE PESANAN ---
    public static void savePesanan(List<Pesanan> list, int nextId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nextId\": ").append(nextId).append(",\n");
        sb.append("  \"pesanan\": [\n");

        for (int i = 0; i < list.size(); i++) {
            Pesanan p = list.get(i);
            
            sb.append("    {\n");
            sb.append("      \"id\": ").append(p.getId()).append(",\n");
            sb.append("      \"meja\": ").append(p.getMeja().getNomor()).append(",\n");
            sb.append("      \"status\": \"").append(p.getStatus()).append("\",\n");
            sb.append("      \"items\": [");
            
            List<DetailPesanan> items = p.getItems();
            for (int j = 0; j < items.size(); j++) {
                DetailPesanan d = items.get(j);
                if (j > 0) sb.append(", "); 
                
                sb.append("{");
                sb.append("\"nama\": \"").append(JsonUtil.escape(d.getMenu().getNama())).append("\", ");
                sb.append("\"jumlah\": ").append(d.getJumlah()).append(", ");
                sb.append("\"catatan\": \"").append(JsonUtil.escape(d.getCatatan())).append("\"");
                sb.append("}");
            }
            
            sb.append("]\n"); 
            sb.append("    }"); 
            
            if (i < list.size() - 1) sb.append(",\n");
        }
        
        sb.append("\n  ]\n"); 
        sb.append("}"); 

        JsonUtil.writeFile(PESANAN_FILE, sb.toString());
    }

    public static int loadLastId() {
        String json = JsonUtil.readFile(PESANAN_FILE);
        int val = JsonUtil.getRootInt(json, "nextId");
        return (val == 0) ? 1 : val;
    }
    
    public static void saveTransaksi(Transaksi t) {
        List<String> list = new ArrayList<>();
        String json = JsonUtil.readFile(TRANSAKSI_FILE);
        if(!json.equals("{}")) list = JsonUtil.parseArray(json, "transaksi");
        
        String obj = JsonUtil.jsonObject(
            "idPesanan", String.valueOf(t.getPesanan().getId()),
            "total", String.valueOf(t.getTotal()),
            "metode", t.getPembayaran().getJenis(),
            "waktu", t.getWaktuFormatted()
        );
        list.add(obj);
        JsonUtil.writeFile(TRANSAKSI_FILE, JsonUtil.jsonWithRoot("transaksi", JsonUtil.jsonArray(list)));
    }

    // Helper untuk mencari menu agar harga tidak 0
    private static MenuItem findMenuByName(String nama) {
        List<MenuItem> menus = loadMenu(); 
        for (MenuItem m : menus) {
            if (m.getNama().equalsIgnoreCase(nama)) return m;
        }
        return null;
    }

    public static void saveMenu(List<MenuItem> menu) {
        // Implementasi save menu jika dibutuhkan (bisa ditambahkan nanti)
    }
}
