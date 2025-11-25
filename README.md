# 🍜 Sistem Manajemen Pesanan Restoran

<p align="center">
  Sebuah solusi berbasis Java untuk mengelola pesanan, menu, dan transaksi di restoran secara efisien.
</p>

---

## 👥 Anggota Tim

Proyek ini dikembangkan sebagai tugas Ujian Akhir Semester (UAS) oleh **Kelompok 2**:

| Nama | NIM |
| :--- | :--- |
| Fadhlurrahman Alaudin | 2408107010053 |
| Muhammad Fazel Mawla | 2408107010074 |
| Muhammad Anis Fathin | 2408107010045 |
| Syifa Salsabila | 2408107010018 |

---

## 📝 Deskripsi Singkat

Proyek ini merupakan **Sistem Manajemen Pesanan Restoran** yang dikembangkan menggunakan **Java (Pemrograman Berorientasi Objek - PBO)**. Sistem ini dirancang untuk membantu pegawai restoran dalam mengelola daftar menu dan mencatat transaksi pesanan secara terstruktur.

---

## 🎯 Kegunaan Sistem

Sistem Manajemen Pesanan Restoran ini melayani fungsionalitas utama sebagai berikut:

| Fitur | Deskripsi Fungsional |
| :--- | :--- |
| **Pencatatan Pesanan** | Mencatat pesanan baru dari pelanggan, termasuk pemilihan item dan jumlah (*quantity*). |
| **Integrasi Transaksi** | Secara otomatis menghitung subtotal, total biaya, dan menampilkan rincian pembayaran. |
| **Data History** | Menyimpan riwayat semua pesanan yang telah diselesaikan untuk kebutuhan laporan atau audit. |
| **Efisiensi Operasional** | Mempercepat alur kerja layanan dari pemesanan hingga pembayaran. |

---

## 🚀 Cara Menjalankan Kode

1. **Clone repository** ini ke komputer Anda.
2. **Masuk ke folder** proyek melalui terminal atau command prompt.
3. Jalankan perintah berikut untuk meng-compile dan menjalankan aplikasi sesuai sistem operasi Anda:

   **Di Windows (PowerShell):**
   ```powershell
   # Compile (Mengkompilasi semua file .java)
   javac (Get-ChildItem -Recurse -Filter *.java).FullName

   # Run (Menjalankan aplikasi)
   java -cp .\src\main\java\ com.restaurant.app.RestaurantDriver
   ```

  **Di Linux/Mac (Terminal):**
  ```bash
  # Compile (Mengkompilasi semua file .java)
  javac $(find . -name *.java)

  # Run (Menjalankan aplikasi)
  java -cp src/main/java com.restaurant.app.RestaurantDriver
  ```
