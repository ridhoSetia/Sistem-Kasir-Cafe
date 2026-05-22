package com.cafe.repository;

import java.util.List;

/**
 * Kontrak Abstraksi Generik untuk Data Access Layer.
 * @param <T>  Tipe Entitas / Model (contoh: Menu, User, Transaksi)
 * @param <ID> Tipe Data Primary Key
 */
public interface IRepository<T, ID> {
    
    // Create (C)
    boolean simpan(T entitas);
    
    // Read (R)
    List<T> ambilSemua();
    
    // Update (U)
    boolean perbarui(T entitas);
    
    // Delete (D)
    boolean hapus(ID id);

    // Search (S)
    // Memaksa semua repository memiliki fitur pencarian berbasis teks dinamis
    List<T> cari(String keyword);
}