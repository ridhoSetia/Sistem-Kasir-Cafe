# ☕ Brew Society POS (Point of Sales)

Sistem Manajemen Kasir Cafe berbasis Desktop yang dirancang menggunakan **JavaFX** dan **MySQL**. Proyek ini secara ketat memisahkan antarmuka pengguna dari logika akses data menggunakan arsitektur **Model-View-Controller (MVC)**.

## Fitur Utama

### 🛒 Modul Kasir (Penjualan)
- Antarmuka keranjang belanja interaktif dengan *Live Calculation* (kembalian otomatis).
- Validasi ketersediaan stok *real-time* berbasis Java Stream.
- Pencetakan Struk / Nota Transaksi secara dinamis.
### 👥 Modul Manajer (Back-Office)
- **Manajemen Menu**: Operasi CRUD penuh untuk item makanan/minuman dengan penyesuaian harga dan visibilitas stok.
- **Manajemen Akun Kasir**: Pengaturan akses dan kredensial penugasan dengan sistem *Role-Based Access Control* (RBAC) pada *User Session*.
## 🛠️ Tech Stack & Requirements

- **Language:** Java (JDK 17 atau lebih baru disarankan)
- **GUI Framework:** JavaFX (CSS Styled)
- **Database:** MySQL (Direkomendasikan berjalan di atas kontainer Docker)
- **Driver:** MySQL Connector/J (JDBC)

## ⚙️ Cara Instalasi & Menjalankan

1. **Kloning Repositori:**
   ```
   git clone [https://github.com/UsernameAnda/sistem-kasir-cafe.git](https://github.com/UsernameAnda/sistem-kasir-cafe.git)
   cd sistem-kasir-cafe
    ```

2. **Konfigurasi Database:**
* Nyalakan server MySQL Anda.
* Buat database baru bernama `db_cafe`.
* Jalankan (*import*) berkas `src/init.sql` untuk membangun skema tabel relasional dan prosedur sistem.
* (Opsional) Jalankan berkas `src/dummy.sql` jika Anda membutuhkan data uji coba awal.


3. **Koneksi Kredensial:**
* Salin file konfigurasi koneksi: `cp src/com/cafe/config/DBConnection.java.example src/com/cafe/config/DBConnection.java`
* Sesuaikan `USER` dan `PASSWORD` di dalam `DBConnection.java` dengan konfigurasi MySQL lokal/Docker Anda.


4. **Kompilasi & Jalankan:**
* Buka proyek menggunakan IDE (VS Code / IntelliJ / Eclipse).
* Pastikan *library* JavaFX dan MySQL Connector telah terhubung pada *Build Path* atau modul konfigurasi Anda.
* Eksekusi berkas `src/Main.java`.



## 📸 Screenshots

* **Halaman Login & Autentikasi** <br> 
![Halaman login](/images-readme/login.png)
* **Halaman Menu Utama Manajer** <br>
![Halaman menu utama role manager](/images-readme/main-menu-manager.png)
* **Halaman Menu Utama Kasir** <br>
![Halaman menu utama role kasir](/images-readme/main-menu-kasir.png)
* **Halaman Kelola Pembayaran** <br>
![Halaman untuk pembayaran di kasir](/images-readme/kelola-pembayaran.png)
* **Halaman Kelola Menu** <br>
![Halaman kelola menu cafe (CRUD)](/images-readme/kelola-menu.png)
* **Halaman Kelola Akun Kasir** <br>
![Halaman kelola akun kasir (CRUD)](/images-readme/kelola-akun-kasir.png)
* **Halaman Riwayat Penjualan** <br>
![Halaman riwayat penjualan](/images-readme/riwayat-penjualan.png)
```