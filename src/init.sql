-- Tabel Users (Inheritance User -> Kasir/Manager)
CREATE TABLE IF NOT EXISTS users (
    id_user INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Kasir', 'Manager') NOT NULL
);

-- Tabel Menu (Mendukung Stok dan Kategori)
CREATE TABLE IF NOT EXISTS menu (
    id_menu INT PRIMARY KEY AUTO_INCREMENT,
    nama_menu VARCHAR(100) NOT NULL,
    harga DOUBLE NOT NULL,
    kategori VARCHAR(50),
    stok INT NOT NULL DEFAULT 0
);

-- Tabel Transaksi (Utama)
CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi INT PRIMARY KEY AUTO_INCREMENT,
    tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_harga DOUBLE NOT NULL,
    status_pembayaran ENUM('Pending', 'Lunas', 'Batal') DEFAULT 'Pending'
);

-- Tabel Detail Transaksi (Rincian Item per Transaksi)
-- Menghubungkan Transaksi dengan Menu (Relasi Many-to-Many)
CREATE TABLE IF NOT EXISTS detail_transaksi (
    id_detail INT PRIMARY KEY AUTO_INCREMENT,
    id_transaksi INT NOT NULL,
    id_menu INT NOT NULL,
    jumlah INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi) ON DELETE CASCADE,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu)
);