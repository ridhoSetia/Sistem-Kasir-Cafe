package com.cafe.auth;

import com.cafe.model.User;
import com.cafe.model.Kasir;
import com.cafe.model.Manager;
import com.cafe.repository.UserRepository;

public class DatabaseAuth extends Auth {
    private final UserRepository userRepository;

    public DatabaseAuth() {
        this.userRepository = new UserRepository(); // Memanfaatkan repository data access layer
    }

    @Override
    public User login(String username, String password) {
        // Validasi filter input kosong sebelum memicu query SQL
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("[Security Warning] Deteksi percobaan login dengan input kosong.");
            return null;
        }

        // Ambil data user dari database melalui layer repository
        User user = userRepository.login(username, password);

        if (user != null) {
            System.out.println("[Auth Success] Pengguna " + user.getNamaLengkap() + " berhasil terautentikasi.");
            
            // Mengembalikan tipe child (Kasir/Manager) dalam bungkusan tipe parent (User)
            if (user.getRole().equalsIgnoreCase("Kasir")) {
                return new Kasir(user.getIdUser(), user.getUsername(), "", user.getNamaLengkap());
            } else if (user.getRole().equalsIgnoreCase("Manager")) {
                return new Manager(user.getIdUser(), user.getUsername(), "", user.getNamaLengkap());
            }
        }

        System.err.println("[Auth Failed] Percobaan login gagal untuk username: " + username);
        return null;
    }
}