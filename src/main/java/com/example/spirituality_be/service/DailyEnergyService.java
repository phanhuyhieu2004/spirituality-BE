package com.example.spirituality_be.service;

import com.example.spirituality_be.dto.DailyEnergyDto;
import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.model.DailyEnergy;
import com.example.spirituality_be.model.Profiles;
import com.example.spirituality_be.repository.DailyEnergyRepository;
import com.example.spirituality_be.repository.ProfilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DailyEnergyService {

    private final DailyEnergyRepository dailyEnergyRepository;
    private final ProfilesRepository profilesRepository;

    public DailyEnergyDto getDailyEnergy(Accounts account, boolean forceRefresh) {
        Profiles profile = profilesRepository.findByAccountId(account.getId())
                .orElse(null);

        if (profile == null || profile.getDob() == null || profile.getGodNumber() == null) {
            return createEmptyEnergyDto();
        }

        LocalDate today = LocalDate.now();
        Optional<DailyEnergy> existing = dailyEnergyRepository.findFirstByAccountIdAndDateOfRecordOrderByCreatedAtDesc(account.getId(), today);

        if (existing.isPresent() && !forceRefresh) {
            return mapToDto(existing.get(), true, profile);
        }

        DailyEnergy newEnergy = calculateAndSaveEnergy(account, profile, today, existing.orElse(null));
        return mapToDto(newEnergy, false, profile);
    }

    private DailyEnergy calculateAndSaveEnergy(Accounts account, Profiles profile, LocalDate date, DailyEnergy existingRecord) {
        Random random = new Random((long) date.getDayOfYear() * account.getId());

        int personalDay = calculatePersonalDay(profile.getGodNumber(), date);
        String dailyElement = getElementFromNumber(personalDay);
        String userElement = extractCoreElement(profile.getElements());

        int interactionScore = calculateElementInteraction(userElement, dailyElement);

        int lpBonus = profile.getGodNumber() > 9 ? 10 : profile.getGodNumber();

        int mental = bindTo100(50 + interactionScore + lpBonus + random.nextInt(15) - 5);
        int emotional = bindTo100(50 + interactionScore + (userElement.equals("Thủy") ? 10 : 0) + random.nextInt(15) - 5);
        int focus = bindTo100(50 + interactionScore + (userElement.equals("Kim") ? 10 : 0) + random.nextInt(15) - 5);
        int action = bindTo100(50 + interactionScore + (userElement.equals("Hỏa") ? 10 : 0) + random.nextInt(15) - 5);
        int social = bindTo100(50 + interactionScore + (userElement.equals("Mộc") ? 10 : 0) + random.nextInt(15) - 5);

        DailyEnergy energy = existingRecord != null ? existingRecord : new DailyEnergy();
        energy.setAccount(account);
        energy.setDateOfRecord(date);
        energy.setMentalEnergy(mental);
        energy.setEmotionalBalance(emotional);
        energy.setFocusLevel(focus);
        energy.setActionReadiness(action);
        energy.setSocialHarmony(social);
        energy.setPersonalDayNumber(personalDay);

        String affirmation = generateAffirmation(userElement, personalDay, mental);
        energy.setAffirmation(affirmation);

        return dailyEnergyRepository.save(energy);
    }

    private int calculatePersonalDay(int lifePath, LocalDate date) {
        int sum = lifePath + date.getDayOfMonth() + date.getMonthValue() + date.getYear();
        return reduceToSingleDigit(sum);
    }

    private int reduceToSingleDigit(int num) {
        while (num > 9 && num != 11 && num != 22 && num != 33) {
            int temp = 0;
            while (num > 0) {
                temp += num % 10;
                num /= 10;
            }
            num = temp;
        }
        return num > 9 ? reduceToSingleDigit(num / 10 + num % 10) : num;
    }

    private String getElementFromNumber(int num) {
        if (num == 1 || num == 2) return "Mộc";
        if (num == 3 || num == 4) return "Hỏa";
        if (num == 5 || num == 6) return "Thổ";
        if (num == 7 || num == 8) return "Kim";
        return "Thủy";
    }

    private String extractCoreElement(String elementsJson) {
        if (elementsJson == null || elementsJson.isEmpty()) return "Thổ";
        String lower = elementsJson.toLowerCase();
        if (lower.contains("kim")) return "Kim";
        if (lower.contains("mộc") || lower.contains("moc")) return "Mộc";
        if (lower.contains("thủy") || lower.contains("thuy")) return "Thủy";
        if (lower.contains("hỏa") || lower.contains("hoa")) return "Hỏa";
        return "Thổ";
    }

    private int calculateElementInteraction(String userElement, String dailyElement) {
        if (
            (userElement.equals("Mộc") && dailyElement.equals("Thủy")) ||
            (userElement.equals("Hỏa") && dailyElement.equals("Mộc")) ||
            (userElement.equals("Thổ") && dailyElement.equals("Hỏa")) ||
            (userElement.equals("Kim") && dailyElement.equals("Thổ")) ||
            (userElement.equals("Thủy") && dailyElement.equals("Kim")) ||
            (userElement.equals("Thủy") && dailyElement.equals("Mộc")) ||
            (userElement.equals("Mộc") && dailyElement.equals("Hỏa")) ||
            (userElement.equals("Hỏa") && dailyElement.equals("Thổ")) ||
            (userElement.equals("Thổ") && dailyElement.equals("Kim")) ||
            (userElement.equals("Kim") && dailyElement.equals("Thủy"))
        ) {
            return 15;
        }

        if (
            (userElement.equals("Mộc") && (dailyElement.equals("Kim") || dailyElement.equals("Thổ"))) ||
            (userElement.equals("Hỏa") && (dailyElement.equals("Thủy") || dailyElement.equals("Kim"))) ||
            (userElement.equals("Thổ") && (dailyElement.equals("Mộc") || dailyElement.equals("Thủy"))) ||
            (userElement.equals("Kim") && (dailyElement.equals("Hỏa") || dailyElement.equals("Mộc"))) ||
            (userElement.equals("Thủy") && (dailyElement.equals("Thổ") || dailyElement.equals("Hỏa")))
        ) {
            return -10;
        }

        return 0;
    }

    private int bindTo100(int val) {
        if (val > 95) return 95;
        if (val < 20) return 20;
        return val;
    }

    private String generateAffirmation(String element, int personalDay, int mentalEnergy) {
        if (mentalEnergy > 80) {
            return "Năng lượng " + element + " đang rất dồi dào. Ngày " + personalDay + " này, trực giác của bạn cực kỳ nhạy bén, hãy tự tin ra quyết định lớn.";
        } else if (mentalEnergy < 40) {
            return "Sóng năng lượng ngày " + personalDay + " đang chững lại đối với người mệnh " + element + ". Hãy dành thời gian nghỉ ngơi, quay về bên trong và tránh xung đột.";
        }
        return "Một ngày cân bằng cho người mang mệnh " + element + " trong chu kỳ " + personalDay + ". Hãy bước đi chậm rãi và đón nhận những tín hiệu từ vũ trụ.";
    }

    private DailyEnergyDto mapToDto(DailyEnergy energy, boolean fromCache, Profiles profile) {
        DailyEnergyDto dto = new DailyEnergyDto();
        dto.setMentalEnergy(energy.getMentalEnergy());
        dto.setEmotionalBalance(energy.getEmotionalBalance());
        dto.setFocusLevel(energy.getFocusLevel());
        dto.setActionReadiness(energy.getActionReadiness());
        dto.setSocialHarmony(energy.getSocialHarmony());

        int total = (energy.getMentalEnergy() + energy.getEmotionalBalance() + energy.getFocusLevel() + energy.getActionReadiness() + energy.getSocialHarmony()) / 5;
        dto.setTotalEnergy(total);

        dto.setAffirmation(energy.getAffirmation());

        String userElement = extractCoreElement(profile.getElements());
        String dailyElement = getElementFromNumber(energy.getPersonalDayNumber());
        int interactionScore = calculateElementInteraction(userElement, dailyElement);

        dto.setUserElement(userElement);
        dto.setDailyElement(dailyElement);
        dto.setInteractionScore(interactionScore);
        dto.setFromCache(fromCache);

        dto.setRadarData(List.of(
            energy.getMentalEnergy(),
            energy.getEmotionalBalance(),
            energy.getActionReadiness(),
            energy.getSocialHarmony(),
            energy.getFocusLevel()
        ));

        return dto;
    }

    private DailyEnergyDto createEmptyEnergyDto() {
        DailyEnergyDto dto = new DailyEnergyDto();
        dto.setMentalEnergy(0);
        dto.setEmotionalBalance(0);
        dto.setFocusLevel(0);
        dto.setActionReadiness(0);
        dto.setSocialHarmony(0);
        dto.setTotalEnergy(0);
        dto.setAffirmation("Hãy cập nhật ngày sinh để nhận thông điệp vũ trụ.");
        dto.setUserElement("Chưa rõ");
        dto.setDailyElement("Chưa rõ");
        dto.setInteractionScore(0);
        dto.setFromCache(false);
        dto.setRadarData(List.of(0, 0, 0, 0, 0));
        return dto;
    }
}