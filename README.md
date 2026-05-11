# Sistem Kasir Cafe - JavaFX Arsitektur MVC

Proyek ini adalah aplikasi manajemen kasir cafe yang dibangun menggunakan **JavaFX** sebagai antarmuka grafis dan **MySQL** sebagai sistem penyimpanan data permanen. Aplikasi ini dirancang dengan prinsip **Separation of Concerns** untuk memastikan kode mudah dikelola, diuji, dan dikembangkan secara tim.

## 📂 Struktur Folder dan Fungsi

Berdasarkan struktur proyek, aplikasi ini dibagi menjadi beberapa paket utama:

### 1. `com.cafe.config`
* **Fungsi:** Menyimpan konfigurasi infrastruktur aplikasi.
* **Isi Utama:** Kelas koneksi database (`DBConnection`).
* **Peran:** Menjadi jembatan antara aplikasi Java dan server database (MySQL/Docker). Segala pengaturan alamat IP, port, dan kredensial database dipusatkan di sini.

### 2. `com.cafe.model`
* **Fungsi:** Representasi data (Entity).
* **Peran:** Folder ini berisi Objek Java (POJO) yang mencerminkan struktur tabel di database. Folder ini hanya fokus pada definisi atribut data dan enkapsulasi (getter/setter).

### 3. `com.cafe.repository`
* **Fungsi:** Data Access Layer (DAO).
* **Peran:** Bertanggung jawab penuh atas komunikasi dengan database. Semua perintah SQL (`INSERT`, `SELECT`, `UPDATE`) diisolasi di dalam folder ini agar logika bisnis tidak tercampur dengan logika database.

### 4. `com.cafe.controller`
* **Fungsi:** Bridge (Jembatan) antara View dan Model.
* **Peran:** Menangani input pengguna dari antarmuka (klik tombol, input teks) dan memutuskan apa yang harus dilakukan oleh sistem. Controller memanggil *Repository* untuk mengolah data dan memperbarui tampilan *View*.

### 5. `resource`
* **Fungsi:** Antarmuka Grafis (View).
* **Isi Utama:** File `.fxml`.
* **Peran:** Menyimpan definisi tata letak UI yang dibuat melalui Scene Builder. Memisahkan desain visual dari logika pemrograman Java.

### 6. `init.sql`
* **Fungsi:** Skema Database.
* **Peran:** Berisi perintah SQL untuk membangun struktur tabel yang dibutuhkan aplikasi agar database siap digunakan saat pertama kali dijalankan.

---

## ⚙️ Konsep yang Digunakan

Aplikasi ini menerapkan beberapa konsep fundamental pengembangan perangkat lunak:

### 1. Model-View-Controller (MVC)
Arsitektur ini memisahkan aplikasi menjadi tiga komponen utama:
* **Model:** Data.
* **View:** Tampilan UI.
* **Controller:** Logika penghubung.
* *Manfaat:* Memungkinkan tim bekerja secara paralel (satu orang fokus di UI, satu orang di database).

### 2. Data Access Object (DAO) / Repository Pattern
Konsep ini digunakan untuk memisahkan logika akses data dari logika bisnis. Dengan *Repository*, aplikasi tidak peduli apakah data datang dari MySQL lokal atau Docker; aplikasi hanya memanggil fungsi yang tersedia di kelas Repository.

### 3. Encapsulation (OOP)
Penerapan hak akses `private` pada atribut dalam kelas Model untuk memastikan integritas data. Data hanya bisa dimodifikasi melalui metode yang valid (Setter/Getter).

### 4. Singleton / Static Factory (Database Connection)
Pengelolaan koneksi database dipusatkan pada satu pintu untuk memastikan efisiensi penggunaan sumber daya sistem dan mencegah kebocoran memori (*memory leak*) akibat terlalu banyak koneksi yang terbuka.