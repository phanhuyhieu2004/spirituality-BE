package com.example.spirituality_be.service;

import com.example.spirituality_be.model.Library;
import com.example.spirituality_be.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSeedService implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    @Override
    public void run(String... args) {
        // Tạm dừng nạp dữ liệu theo yêu cầu người dùng
    }

    private void seedLibrary() {
        // Đã xóa dữ liệu mẫu
    }

    private Library createTarot(int id, String name, String upright, String reversed, String correlation) {
        return null;
    }

    private Library createIching(int id, String name, String desc, String correlation) {
        return null;
    }
}
