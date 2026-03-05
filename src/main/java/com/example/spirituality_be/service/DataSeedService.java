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
        if (libraryRepository.count() == 0) {
            seedLibrary();
        } else {
            System.out.println("Dữ liệu thư viện đã tồn tại, bỏ qua bước seeding.");
        }
    }

    private void seedLibrary() {
        List<Library> items = new ArrayList<>();


        items.add(createTarot(0, "The Fool", "Khởi đầu mới", "Sự ngây thơ, tiềm năng vô hạn.", "Positive|Khí"));
        items.add(createTarot(1, "The Magician", "Hành động có định hướng", "Kỹ năng, sức mạnh, hiện thực hóa.", "Positive|Hỏa"));
        items.add(createTarot(2, "The High Priestess", "Trực giác nhạy bén", "Bí mật, trí tuệ nội tâm.", "Positive|Thủy"));
        items.add(createTarot(3, "The Empress", "Sự sung túc và màu mỡ", "Sáng tạo, thiên nhiên, nuôi dưỡng.", "Positive|Thổ"));
        items.add(createTarot(4, "The Emperor", "Cấu trúc và uy quyền", "Kỷ luật, kiểm soát, cha chú.", "Neutral|Thổ"));
        items.add(createTarot(5, "The Hierophant", "Truyền thống và niềm tin", "Giáo dục, tinh thần, tổ chức.", "Neutral|Kim"));
        items.add(createTarot(6, "The Lovers", "Sự hòa hợp và lựa chọn", "Tình yêu, sự cân bằng.", "Positive|Khí"));
        items.add(createTarot(7, "The Chariot", "Chiến thắng và quyết tâm", "Sự tập trung, ý chí, du hành.", "Positive|Thủy"));
        items.add(createTarot(8, "Strength", "Sức mạnh nội tâm", "Can đảm, lòng trắc ẩn, nhẫn nại.", "Positive|Hỏa"));
        items.add(createTarot(9, "The Hermit", "Sự chiêm nghiệm", "Sự cô độc, tìm kiếm chân lý.", "Neutral|Thổ"));
        items.add(createTarot(10, "Wheel of Fortune", "Vận may xoay vần", "Chu kỳ, cơ hội, định mệnh.", "Positive|Khí"));
        items.add(createTarot(13, "Death", "Sự kết thúc và biến chuyển", "Buông bỏ, chuyển giao.", "Warning|Thủy"));
        items.add(createTarot(15, "The Devil", "Sự ràng buộc", "Cám dỗ, vật chất, nỗi sợ.", "Warning|Thổ"));
        items.add(createTarot(16, "The Tower", "Sụp đổ bất ngờ", "Sự rung chuyển, thay đổi đột ngột.", "Warning|Hỏa"));
        items.add(createTarot(18, "The Moon", "Sự ảo giác và bất an", "Tiềm thức, trực giác, nhầm lẫn.", "Neutral|Thủy"));
        items.add(createTarot(19, "The Sun", "Sự rạng rỡ và thành công", "Hạnh phúc, sức sống, sự thật.", "Positive|Hỏa"));
        items.add(createTarot(21, "The World", "Sự hoàn tất thịnh vượng", "Thành tựu, sự toàn vẹn.", "Positive|Thổ"));


        for (int i = 0; i <= 21; i++) {
            final int id = i;
            if (items.stream().noneMatch(item -> item.getItemId().equals("tarot_" + String.format("%02d", id)))) {
                items.add(createTarot(i, "Tarot Card " + i, "Ý nghĩa xuôi", "Ý nghĩa ngược", "Neutral"));
            }
        }


        items.add(createIching(1, "Thuần Càn", "Sáng tạo mãnh liệt, sức mạnh của Rồng.", "Positive|111111"));
        items.add(createIching(2, "Thuần Khôn", "Sự tiếp nhận bao dung, đất mẹ nâng đỡ.", "Positive|000000"));
        items.add(createIching(3, "Thủy Lôi Truân", "Gian nan ban đầu, cần kiên trì.", "Warning|100010"));
        items.add(createIching(11, "Địa Thiên Thái", "Thái bình thịnh vượng, hanh thông.", "Positive|111000"));
        items.add(createIching(12, "Thiên Địa Bĩ", "Sự bế tắc, chưa thuận lợi.", "Negative|000111"));
        items.add(createIching(24, "Địa Lôi Phục", "Sự hồi sinh, quay trở lại.", "Positive|100000"));
        items.add(createIching(30, "Thuần Ly", "Ánh sáng, sự thông suốt, rực rỡ.", "Positive|101101"));
        items.add(createIching(31, "Trạch Sơn Hàm", "Sự cảm thông, giao thoa tình cảm.", "Positive|001110"));
        items.add(createIching(63, "Thủy Hỏa Ký Tế", "Mọi việc đã hoàn tất, đỉnh cao thành tựu.", "Positive|101010"));
        items.add(createIching(64, "Hỏa Thủy Vị Tế", "Chưa xong, hi vọng về một tương lai mới.", "Neutral|010101"));
        items.add(createIching(20, "Phong Địa Quan", "Sự quan sát, xem xét kỹ lưỡng.", "Neutral|000011"));
        items.add(createIching(23, "Sơn Địa Bác", "Sự tiêu điều, tan rã.", "Negative|000001"));
        items.add(createIching(29, "Thuần Khảm", "Gian nan, hiểm trở, cần kiên trì.", "Negative|010010"));
        items.add(createIching(55, "Lôi Hỏa Phong", "Sự sung túc rực rỡ, phồn vinh.", "Positive|101100"));

        libraryRepository.saveAll(items);
    }

    private Library createTarot(int id, String name, String upright, String reversed, String correlation) {
        Library l = new Library();
        l.setCategory("tarot_card");
        l.setItemId("tarot_" + String.format("%02d", id));
        l.setName(name);
        l.setMeaningUpright(upright);
        l.setMeaningReversed(reversed);
        l.setCorrelation(correlation);
        l.setImageUrl("https://res.cloudinary.com/drac9ko3l/image/upload/v1772078973/tarot_" + id + ".png");
        return l;
    }

    private Library createIching(int id, String name, String desc, String correlation) {
        Library l = new Library();
        l.setCategory("iching_hexagram");
        l.setItemId("hex_" + String.format("%02d", id));
        l.setName(name);
        l.setDescription(desc);
        l.setCorrelation(correlation);
        l.setImageUrl("https://res.cloudinary.com/drac9ko3l/image/upload/v1772078973/hex_" + id + ".png");
        return l;
    }
}
