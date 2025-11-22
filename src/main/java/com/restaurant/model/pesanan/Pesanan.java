package com.restaurant.model.pesanan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pesanan {

    private int id;
    private Meja meja;
    private String status; // MENUNGGU, SEDANG DIMASAK, SELESAI DIMASAK, SELESAI
    private List<DetailPesanan> items;
    
    // Tracking informasi untuk komunikasi yang lebih baik
    private LocalDateTime waktuDibuat;
    private LocalDateTime waktuStatusBerubah;
    private String diprosesOleh; // username yang memproses pesanan
    private String rolePemroses; // role user yang memproses

    public Pesanan(int id, Meja meja) {
        this.id = id;
        this.meja = meja;
        this.status = "MENUNGGU";
        this.items = new ArrayList<>();
        this.waktuDibuat = LocalDateTime.now();
        this.waktuStatusBerubah = LocalDateTime.now();
    }

    // ======================================
    // GETTER & SETTER
    // ======================================
    public int getId() {
        return id;
    }

    public Meja getMeja() {
        return meja;
    }

    public void setMeja(Meja meja) {
        this.meja = meja;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.waktuStatusBerubah = LocalDateTime.now();
    }
    
    public void setStatus(String status, String diprosesOleh, String rolePemroses) {
        this.status = status;
        this.diprosesOleh = diprosesOleh;
        this.rolePemroses = rolePemroses;
        this.waktuStatusBerubah = LocalDateTime.now();
    }

    public List<DetailPesanan> getItems() {
        return items;
    }
    
    // ======================================
    // TRACKING GETTERS
    // ======================================
    public LocalDateTime getWaktuDibuat() {
        return waktuDibuat;
    }
    
    public LocalDateTime getWaktuStatusBerubah() {
        return waktuStatusBerubah;
    }
    
    public String getDiprosesOleh() {
        return diprosesOleh;
    }
    
    public String getRolePemroses() {
        return rolePemroses;
    }
    
    /**
     * Menghitung berapa lama pesanan dalam status tertentu (dalam menit)
     */
    public long getDurasiStatus() {
        if (waktuStatusBerubah == null) return 0;
        java.time.Duration duration = java.time.Duration.between(waktuStatusBerubah, LocalDateTime.now());
        return duration.toMinutes();
    }

    // ======================================
    // TAMBAH ITEM
    // ======================================
    public void tambahItem(DetailPesanan dp) {
        items.add(dp);
    }

    // ======================================
    // HITUNG TOTAL
    // ======================================
    public double getTotal() {
        double total = 0;
        for (DetailPesanan d : items) {
            total += d.getSubtotal();
        }
        return total;
    }

    // ======================================
    // RENDER DETAIL PESANAN (untuk layar & struk)
    // ======================================
    public String renderDetail() {
        StringBuilder sb = new StringBuilder();
        for (DetailPesanan d : items) {
            sb.append("- ").append(d.toString()).append("\n");
        }
        return sb.toString();
    }

    // ======================================
    // TO STRING (UNTUK LIST PELAYAN/KOKI/KASIR)
    // ======================================
    @Override
    public String toString() {
        String info = "Pesanan #" + id +
                " | Meja " + meja.getNomor() +
                " | Status: " + status +
                " | Total: Rp" + getTotal();
        
        if (diprosesOleh != null) {
            info += " | Diproses: " + diprosesOleh;
        }
        
        long durasi = getDurasiStatus();
        if (durasi > 0) {
            info += " | Durasi: " + durasi + " menit";
        }
        
        return info;
    }
    
    /**
     * Format informasi lengkap untuk display
     */
    public String getInfoLengkap() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📋 Pesanan #").append(id).append("\n");
        sb.append("🪑 Meja: ").append(meja.getNomor()).append("\n");
        sb.append("📊 Status: ").append(status).append("\n");
        sb.append("💰 Total: Rp").append(getTotal()).append("\n");
        sb.append("🕐 Dibuat: ").append(waktuDibuat.format(formatter)).append("\n");
        sb.append("🕐 Status Berubah: ").append(waktuStatusBerubah.format(formatter)).append("\n");
        
        if (diprosesOleh != null) {
            sb.append("👤 Diproses oleh: ").append(diprosesOleh)
              .append(" (").append(rolePemroses).append(")\n");
        }
        
        long durasi = getDurasiStatus();
        if (durasi > 0) {
            sb.append("⏱️  Durasi status ini: ").append(durasi).append(" menit\n");
        }
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return sb.toString();
    }
}
