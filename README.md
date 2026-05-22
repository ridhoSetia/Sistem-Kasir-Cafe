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

# Implementasi 4 Pilar OOP dalam Program Kasir Cafe

## 1. Encapsulation (Enkapsulasi)

Menyembunyikan data internal dan hanya mengeksposnya lewat method yang terkontrol.

**`User.java`** — semua atribut bersifat `private`, hanya bisa diakses via getter:

```java
private int idUser;
private String namaLengkap;
private String username;
// ...
public int getIdUser() { return idUser; }
public String getNamaLengkap() { return namaLengkap; }
```

**`Transaksi.java`** — list detail dikunci agar tidak bisa dimodifikasi dari luar:

```java
public List<DetailTransaksi> getlistDetail() {
    return Collections.unmodifiableList(listDetail); // read-only
}
```

**`UserSession.java`** — constructor private mencegah instansiasi langsung (Singleton):
```java
private UserSession() {} // tidak bisa di-new dari luar
public static UserSession getInstance() { ... }
```

---

## 2. Inheritance (Pewarisan)

Kelas anak mewarisi properti dan method dari kelas induk.

**`Kasir.java` dan `Manager.java`** mewarisi `User.java`:

```java
public class Kasir extends User {
    public Kasir(int idUser, String username, String password, String namaLengkap) {
        super(idUser, username, password, "Kasir", namaLengkap); // memanggil constructor induk
    }
}
```

**`DatabaseAuth.java`** mewarisi `Auth.java`:

```java
public class DatabaseAuth extends Auth {
    @Override
    public User login(String username, String password) { ... }
}
```

---

## 3. Polymorphism (Polimorfisme)

Satu interface/tipe induk, banyak bentuk implementasi yang berbeda.

**`LoginController.java`** — variabel bertipe `Auth` (induk), tapi diisi `DatabaseAuth` (anak). Saat `login()` dipanggil, yang berjalan adalah versi `DatabaseAuth`:

```java
private final Auth authSystem = new DatabaseAuth(); // deklarasi induk, isi anak
// ...
User userLogOn = authSystem.login(username, password); // jalankan versi DatabaseAuth
```

**`DatabaseAuth.java`** — hasil login dikembalikan sebagai tipe `User`, tapi isinya bisa `Kasir` atau `Manager`:
```java

if (user.getRole().equalsIgnoreCase("Kasir")) {
    return new Kasir(...);  // tipe child, dibungkus tipe parent
} else if (user.getRole().equalsIgnoreCase("Manager")) {
    return new Manager(...);
}
```

**`MainMenuController.java`** — `konfigurasiHakAkses()` dipanggil dari tipe `User`, tapi perilakunya berbeda tergantung apakah isinya `Kasir` atau `Manager`:

```java
userAktif.konfigurasiHakAkses(btnKelolaKasir, btnKelolaMenu);
```

---

## 4. Abstraction (Abstraksi)

Menyembunyikan detail implementasi, hanya menampilkan "kontrak" yang wajib dipenuhi.

**`User.java`** — abstract class dengan method abstract yang memaksa setiap subclass mendefinisikan aturan akses mereka sendiri:

```java
public abstract class User {
    public abstract void konfigurasiHakAkses(Button btnKelolaKasir, Button btnKelolaMenu);
}
```

**`Auth.java`** — abstract class yang memaksa implementasi metode login:

```java
public abstract class Auth {
    public abstract User login(String username, String password);
}
```

**`IRepository.java`** — interface yang menjadi kontrak CRUD untuk semua repository:

```java
public interface IRepository<T, ID> {
    boolean simpan(T entitas);
    List<T> ambilSemua();
    boolean perbarui(T entitas);
    boolean hapus(ID id);
    List<T> cari(String keyword);
}
```

Diimplementasikan oleh `MenuRepository`, `UserRepository`, dan `TransaksiRepository` — masing-masing dengan logika SQL yang berbeda, tapi kontrak method-nya sama persis.

---

## Ringkasan Lokasi

| Pilar | File Utama |
|---|---|
| Encapsulation | `User.java`, `Transaksi.java`, `UserSession.java` |
| Inheritance | `Kasir.java`, `Manager.java`, `DatabaseAuth.java` |
| Polymorphism | `LoginController.java`, `DatabaseAuth.java`, `MainMenuController.java` |
| Abstraction | `User.java`, `Auth.java`, `IRepository.java` |