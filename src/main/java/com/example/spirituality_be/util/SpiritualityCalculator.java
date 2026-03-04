package com.example.spirituality_be.util;

import java.time.LocalDate;

public class SpiritualityCalculator {

    public static class HoaGiapInfo {
        public final String canChi;
        public final String napAm;
        public final String xungKhac;
        public final String nguHanh;

        public HoaGiapInfo(String canChi, String napAm, String xungKhac) {
            this.canChi = canChi;
            this.napAm = napAm;
            this.xungKhac = xungKhac;
            String[] words = napAm.split(" ");
            this.nguHanh = words[words.length - 1];
        }
    }

    private static final HoaGiapInfo[] LUC_THAP_HOA_GIAP = new HoaGiapInfo[] {
        new HoaGiapInfo("Giáp Tý", "Hải Trung Kim", "Mậu Ngọ, Nhâm Ngọ, Canh Dần, Canh Thân"),
        new HoaGiapInfo("Ất Sửu", "Hải Trung Kim", "Kỷ Mùi, Quý Mùi, Tân Mão, Tân Dậu"),
        new HoaGiapInfo("Bính Dần", "Lư Trung Hỏa", "Giáp Thân, Nhâm Thân, Nhâm Tuất, Nhâm Thìn"),
        new HoaGiapInfo("Đinh Mão", "Lư Trung Hỏa", "Ất Dậu, Quý Dậu, Quý Tỵ, Quý Hợi"),
        new HoaGiapInfo("Mậu Thìn", "Đại Lâm Mộc", "Canh Tuất, Bính Tuất"),
        new HoaGiapInfo("Kỷ Tỵ", "Đại Lâm Mộc", "Tân Hợi, Đinh Hợi"),
        new HoaGiapInfo("Canh Ngọ", "Lộ Bàng Thổ", "Nhâm Tý, Bính Tý, Giáp Thân, Giáp Dần"),
        new HoaGiapInfo("Tân Mùi", "Lộ Bàng Thổ", "Quý Sửu, Đinh Sửu, Ất Dậu, Ất Mão"),
        new HoaGiapInfo("Nhâm Thân", "Kiếm Phong Kim", "Bính Dần, Canh Dần, Bính Thân"),
        new HoaGiapInfo("Quý Dậu", "Kiếm Phong Kim", "Đinh Mão, Tân Mão, Đinh Dậu"),
        new HoaGiapInfo("Giáp Tuất", "Sơn Đầu Hỏa", "Nhâm Thìn, Canh Thìn, Canh Tuất"),
        new HoaGiapInfo("Ất Hợi", "Sơn Đầu Hỏa", "Quý Tỵ, Tân Tỵ, Tân Hợi"),
        new HoaGiapInfo("Bính Tý", "Giản Hạ Thủy", "Canh Ngọ, Mậu Ngọ"),
        new HoaGiapInfo("Đinh Sửu", "Giản Hạ Thủy", "Tân Mùi, Kỷ Mùi"),
        new HoaGiapInfo("Mậu Dần", "Thành Đầu Thổ", "Canh Thân, Giáp Thân"),
        new HoaGiapInfo("Kỷ Mão", "Thành Đầu Thổ", "Tân Dậu, Ất Dậu"),
        new HoaGiapInfo("Canh Thìn", "Bạch Lạp Kim", "Giáp Tuất, Mậu Tuất, Giáp Thìn"),
        new HoaGiapInfo("Tân Tỵ", "Bạch Lạp Kim", "Ất Hợi, Kỷ Hợi, Ất Tỵ"),
        new HoaGiapInfo("Nhâm Ngọ", "Dương Liễu Mộc", "Giáp Tý, Canh Tý, Bính Tuất, Bính Thìn"),
        new HoaGiapInfo("Quý Mùi", "Dương Liễu Mộc", "Ất Sửu, Tân Sửu, Đinh Hợi, Đinh Tỵ"),
        new HoaGiapInfo("Giáp Thân", "Tuyền Trung Thủy", "Mậu Dần, Bính Dần, Canh Ngọ, Canh Tý"),
        new HoaGiapInfo("Ất Dậu", "Tuyền Trung Thủy", "Kỷ Mão, Đinh Mão, Tân Mùi, Tân Sửu"),
        new HoaGiapInfo("Bính Tuất", "Ốc Thượng Thổ", "Mậu Thìn, Nhâm Thìn, Nhâm Ngọ, Nhâm Tý"),
        new HoaGiapInfo("Đinh Hợi", "Ốc Thượng Thổ", "Kỷ Tỵ, Quý Tỵ, Quý Mùi, Quý Sửu"),
        new HoaGiapInfo("Mậu Tý", "Tích Lịch Hỏa", "Bính Ngọ, Giáp Ngọ"),
        new HoaGiapInfo("Kỷ Sửu", "Tích Lịch Hỏa", "Đinh Mùi, Ất Mùi"),
        new HoaGiapInfo("Canh Dần", "Tùng Bách Mộc", "Nhâm Thân, Mậu Thân, Giáp Tý, Giáp Ngọ"),
        new HoaGiapInfo("Tân Mão", "Tùng Bách Mộc", "Quý Dậu, Kỷ Dậu, Ất Sửu, Ất Mùi"),
        new HoaGiapInfo("Nhâm Thìn", "Trường Lưu Thủy", "Bính Tuất, Giáp Tuất, Bính Dần"),
        new HoaGiapInfo("Quý Tỵ", "Trường Lưu Thủy", "Đinh Hợi, Ất Hợi, Đinh Mão"),
        new HoaGiapInfo("Giáp Ngọ", "Sa Trung Kim", "Mậu Tý, Nhâm Tý, Canh Dần, Nhâm Dần"),
        new HoaGiapInfo("Ất Mùi", "Sa Trung Kim", "Kỷ Sửu, Quý Sửu, Tân Mão, Tân Dậu"),
        new HoaGiapInfo("Bính Thân", "Sơn Hạ Hỏa", "Giáp Dần, Nhâm Thân, Nhâm Tuất, Nhâm Thìn"),
        new HoaGiapInfo("Đinh Dậu", "Sơn Hạ Hỏa", "Ất Mão, Quý Mão, Quý Tỵ, Quý Hợi"),
        new HoaGiapInfo("Mậu Tuất", "Bình Địa Mộc", "Canh Thìn, Bính Thìn"),
        new HoaGiapInfo("Kỷ Hợi", "Bình Địa Mộc", "Tân Tỵ, Đinh Tỵ"),
        new HoaGiapInfo("Canh Tý", "Bích Thượng Thổ", "Nhâm Ngọ, Bính Ngọ, Giáp Thân, Giáp Dần"),
        new HoaGiapInfo("Tân Sửu", "Bích Thượng Thổ", "Quý Mùi, Đinh Mùi, Ất Dậu, Ất Mão"),
        new HoaGiapInfo("Nhâm Dần", "Kim Bạch Kim", "Canh Thân, Bính Thân, Bính Dần"),
        new HoaGiapInfo("Quý Mão", "Kim Bạch Kim", "Tân Dậu, Đinh Dậu, Đinh Mão"),
        new HoaGiapInfo("Giáp Thìn", "Phú Đăng Hỏa", "Nhâm Tuất, Canh Tuất, Canh Thìn"),
        new HoaGiapInfo("Ất Tỵ", "Phú Đăng Hỏa", "Quý Hợi, Tân Hợi, Tân Tỵ"),
        new HoaGiapInfo("Bính Ngọ", "Thiên Hà Thủy", "Mậu Tý, Canh Tý"),
        new HoaGiapInfo("Đinh Mùi", "Thiên Hà Thủy", "Kỷ Sửu, Tân Sửu"),
        new HoaGiapInfo("Mậu Thân", "Đại Dịch Thổ", "Canh Dần, Giáp Dần"),
        new HoaGiapInfo("Kỷ Dậu", "Đại Dịch Thổ", "Tân Mão, Ất Mão"),
        new HoaGiapInfo("Canh Tuất", "Thoa Xuyến Kim", "Giáp Thìn, Mậu Thìn, Giáp Tuất"),
        new HoaGiapInfo("Tân Hợi", "Thoa Xuyến Kim", "Ất Tỵ, Kỷ Tỵ, Ất Hợi"),
        new HoaGiapInfo("Nhâm Tý", "Tang Đố Mộc", "Giáp Ngọ, Canh Ngọ, Bính Tuất, Bính Thìn"),
        new HoaGiapInfo("Quý Sửu", "Tang Đố Mộc", "Ất Mùi, Tân Mùi, Đinh Hợi, Đinh Tỵ"),
        new HoaGiapInfo("Giáp Dần", "Đại Khê Thủy", "Mậu Thân, Bính Thân, Canh Ngọ, Canh Tý"),
        new HoaGiapInfo("Ất Mão", "Đại Khê Thủy", "Kỷ Dậu, Đinh Dậu, Tân Mùi, Tân Sửu"),
        new HoaGiapInfo("Bính Thìn", "Sa Trung Thổ", "Mậu Tuất, Nhâm Tuất, Nhâm Ngọ, Nhâm Tý"),
        new HoaGiapInfo("Đinh Tỵ", "Sa Trung Thổ", "Kỷ Hợi, Quý Hợi, Quý Sửu, Quý Mùi"),
        new HoaGiapInfo("Mậu Ngọ", "Thiên Thượng Hỏa", "Bính Tý, Giáp Tý"),
        new HoaGiapInfo("Kỷ Mùi", "Thiên Thượng Hỏa", "Đinh Sửu, Ất Sửu"),
        new HoaGiapInfo("Canh Thân", "Thạch Lựu Mộc", "Nhâm Dần, Mậu Dần, Giáp Tý, Giáp Ngọ"),
        new HoaGiapInfo("Tân Dậu", "Thạch Lựu Mộc", "Quý Mão, Kỷ Mão, Ất Sửu, Ất Mùi"),
        new HoaGiapInfo("Nhâm Tuất", "Đại Hải Thủy", "Bính Thìn, Giáp Thìn, Bính Thân, Bính Dần"),
        new HoaGiapInfo("Quý Hợi", "Đại Hải Thủy", "Đinh Tỵ, Ất Tỵ, Đinh Mão, Đinh Dậu")
    };


    public static Integer calculateLifePathNumber(LocalDate dob) {
        if (dob == null) return null;

        int day = dob.getDayOfMonth();
        int month = dob.getMonthValue();
        int year = dob.getYear();

        int sum = sumDigits(day) + sumDigits(month) + sumDigits(year);


        while (sum > 9 && sum != 11 && sum != 22 && sum != 33) {
            sum = sumDigits(sum);
        }

        return sum;
    }

    public static Integer calculatePersonalDayNumber(Integer lifePathNumber, LocalDate targetDate) {
        if (lifePathNumber == null || targetDate == null) return null;
        int day = targetDate.getDayOfMonth();
        int month = targetDate.getMonthValue();
        int year = targetDate.getYear();

        int sum = lifePathNumber + sumDigits(day) + sumDigits(month) + sumDigits(year);


        while (sum > 9) {
            sum = sumDigits(sum);
        }
        return sum;
    }

    private static int sumDigits(int number) {
        int sum = 0;
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    public static String calculateElementsJson(LocalDate dob) {
        if (dob == null) return null;

        int year = dob.getYear();



        int offset = (year - 4) % 60;
        if (offset < 0) {
            offset += 60;
        }

        HoaGiapInfo info = LUC_THAP_HOA_GIAP[offset];


        return String.format(
            "{\"canChi\": \"%s\", \"napAm\": \"%s\", \"nguHanh\": \"%s\", \"tuoiXungKhac\": \"%s\"}",
            info.canChi, info.napAm, info.nguHanh, info.xungKhac
        );
    }
}