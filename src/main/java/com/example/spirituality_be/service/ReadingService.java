package com.example.spirituality_be.service;

import com.example.spirituality_be.dto.ReadingResultDto;
import com.example.spirituality_be.model.*;
import com.example.spirituality_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReadingService {

    private final LibraryRepository libraryRepository;
    private final AiCacheRepository aiCacheRepository;
    private final JournalRepository journalRepository;
    private final ProfilesRepository profilesRepository;
    private final DailyEnergyRepository dailyEnergyRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${groq.api.key}")
    private String apiKey;

    public ReadingResultDto performReading(Accounts account, String question) {
        Profiles profile = profilesRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));




        DailyEnergy energy = dailyEnergyRepository.findFirstByAccountIdAndDateOfRecordOrderByCreatedAtDesc(
                account.getId(), java.time.LocalDate.now()).orElse(null);


        Library tarot = drawWeighted(energy, "tarot_card");
        Library hex = drawWeighted(energy, "iching_hexagram");


        String cacheKey = generateHash(question, tarot.getId(), hex.getId(), energy);
        Optional<AiCache> cached = aiCacheRepository.findByQuestionHashAndTarotCardIdAndIchingHexId(
                cacheKey, tarot.getId(), hex.getId());

        String aiResponse;
        boolean fromCache = false;

        if (cached.isPresent()) {
            aiResponse = cached.get().getAiResponse();
            fromCache = true;
        } else {
            aiResponse = callGroqAi(question, tarot, hex, profile, energy);
            aiCacheRepository.save(new AiCache(null, cacheKey, tarot.getId(), hex.getId(), aiResponse, null));
        }







        return ReadingResultDto.builder()
                .tarotId(tarot.getId())
                .tarotName(tarot.getName())
                .tarotImage(tarot.getImageUrl())
                .tarotMeaning(tarot.getMeaningUpright())
                .hexId(hex.getId())
                .hexNumber(extractHexNumber(hex.getItemId()))
                .hexName(hex.getName())
                .hexBinary(extractHexBinary(hex.getCorrelation()))
                .hexImage(hex.getImageUrl())
                .hexMeaning(hex.getDescription())
                .aiInterpretation(aiResponse)
                .remainingTurns(profile.getFreeTurnsToday())
                .fromCache(fromCache)
                .build();
    }

    private String extractHexBinary(String correlation) {
        if (correlation == null) return "111111";

        String[] parts = correlation.split("\\|");
        String lastPart = parts[parts.length - 1];
        if (lastPart.matches("[01]{6}")) {
            return lastPart;
        }
        return "111111";
    }

    private Integer extractHexNumber(String itemId) {
        try {
            return Integer.parseInt(itemId.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private Library drawWeighted(DailyEnergy energy, String type) {
        List<Library> items = libraryRepository.findByType(type);
        if (items.isEmpty()) return null;

        int balance = (energy != null) ? energy.getEmotionalBalance() : 50;

        List<Library> pool;
        Random rand = new Random();


        if (rand.nextInt(100) < 20) {
            return items.get(rand.nextInt(items.size()));
        }

        if (balance < 40) {
            pool = items.stream().filter(i -> i.getCorrelation().contains("Warning") || i.getCorrelation().contains("Neutral")).collect(Collectors.toList());
        } else if (balance > 70) {
            pool = items.stream().filter(i -> i.getCorrelation().contains("Positive")).collect(Collectors.toList());
        } else {
            pool = items;
        }

        if (pool.isEmpty()) pool = items;
        return pool.get(new Random().nextInt(pool.size()));
    }

    private String generateHash(String q, Integer tId, Integer hId, DailyEnergy e) {
        try {
            String raw = q + tId + hId + (e != null ? e.getPersonalDayNumber() : "0") + java.time.LocalDate.now();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(raw.getBytes()));
        } catch (Exception ex) {
            return String.valueOf(Objects.hash(q, tId, hId));
        }
    }

    private String callGroqAi(String q, Library t, Library h, Profiles p, DailyEnergy e) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "[Fallback] Chưa có Groq API Key.";
        }

        String url = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, Object> req = new HashMap<>();
        req.put("model", "moonshotai/kimi-k2-instruct-0905");
        req.put("temperature", 0.6);
        req.put("max_completion_tokens", 4096);
        req.put("top_p", 1);
        req.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of("role", "system", "content",
            "Bạn là một Bậc Thầy Chiêm Tinh và Người Dẫn Dắt Tâm Linh (Soul Guide). " +
            "Phong cách: Trầm tĩnh, thấu suốt, thực tế, đầy sự thấu cảm nhưng không ủy mị. " +
            "Nhiệm vụ: Giải mã thông điệp kết nối giữa Tarot, Kinh Dịch và năng lượng cá nhân để trả lời câu hỏi của người dùng. " +
            "Lưu ý: Nếu có nhắc đến 'Chỉ số ngày cá nhân' (Numerology), hãy lồng ghép nó vào lời khuyên một cách tự nhiên (ví dụ: 'Với chỉ số ngày là 5, hôm nay là thời điểm của sự thay đổi...'), đừng chỉ liệt kê số. " +
            "Ngôn ngữ: Tiếng Việt, trình bày rõ ràng, sâu sắc."));

        String energySnapshot = (e != null) ?
            String.format("Tinh thần: %d/100, Cân bằng cảm xúc: %d/100, Tập trung: %d/100, Sẵn sàng hành động: %d/100, Ngày cá nhân (Thần số học): %d",
                e.getMentalEnergy(), e.getEmotionalBalance(), e.getFocusLevel(), e.getActionReadiness(), e.getPersonalDayNumber())
            : "Trạng thái ổn định";

        String prompt = String.format(
                "### Thông tin thỉnh cầu ###\n" +
                "- Câu hỏi: \"%s\"\n\n" +
                "### Dữ liệu Vũ trụ hiện tại ###\n" +
                "- Người thỉnh cầu: %s (Mệnh: %s, Chỉ số cốt lõi: %d)\n" +
                "- Bản đồ năng lượng nội tại: %s\n" +
                "- Lá bài Tarot: %s (%s)\n" +
                "- Quẻ Kinh Dịch: %s (%s)\n\n" +
                "### Yêu cầu bình giải ###\n" +
                "Hãy phân tích sự liên kết giữa các yếu tố trên để đưa ra lời giải đáp thực tế. " +
                "Trình bày theo 3 phần: \n" +
                "1. [Thông điệp Tarot]\n" +
                "2. [Trí tuệ Kinh Dịch]\n" +
                "3. [Lời khuyên Hành động] (Kết nối tất cả dữ liệu bao gồm cả năng lượng & ngày cá nhân).\n\n" +
                "--- Bắt đầu bản bình giải ---",
                q, p.getFullName(), p.getElements(), p.getGodNumber(), energySnapshot,
                t.getName(), t.getMeaningUpright(), h.getName(), h.getDescription());

        messages.add(Map.of("role", "user", "content", prompt));
        req.put("messages", messages);


        System.out.println("==================== [AI PROMPT SENT TO GROQ] ====================");
        System.out.println(prompt);
        System.out.println("================================================================");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        try {
            System.out.println("--- CALLING GROQ API (KIMI MODEL) ---");
            Map<String, Object> resp = restTemplate.postForObject(url, new HttpEntity<>(req, headers), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            System.err.println("Groq API Error (" + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            return generateFallbackResponse(t, h);
        } catch (Exception ex) {
            System.err.println("Groq API Unexpected Error: " + ex.getMessage());
            ex.printStackTrace();
            return generateFallbackResponse(t, h);
        }
    }

    private String generateFallbackResponse(Library t, Library h) {
        return String.format(
            "⚠️ [Kết nối Vũ trụ bị gián đoạn - Bản tin dự phòng]\n\n" +
            "Dù kết nối AI đang tạm thời tắc nghẽn, nhưng những biểu tượng này vẫn mang thông điệp rõ ràng cho bạn:\n\n" +
            "1. Ý NGHĨA TAROT (%s):\n%s\n\n" +
            "2. Ý NGHĨA KINH DỊCH (%s):\n%s\n\n" +
            "3. LỜI KHUYÊN:\nHãy dựa vào sự kết hợp giữa '%s' và '%s'. Tín hiệu này khuyên bạn nên giữ vững tâm thế ổn định và tin vào trực giác của mình lúc này.",
            t.getName(), t.getMeaningUpright(),
            h.getName(), h.getDescription(),
            t.getName(), h.getName()
        );
    }

    public void saveToJournalManual(Accounts account, String question, Integer tarotId, Integer hexId, String aiInsight) {
        Library tarot = (tarotId != null) ? libraryRepository.findById(tarotId).orElse(null) : null;
        Library hex = (hexId != null) ? libraryRepository.findById(hexId).orElse(null) : null;

        DailyEnergy energy = dailyEnergyRepository.findFirstByAccountIdAndDateOfRecordOrderByCreatedAtDesc(
                account.getId(), java.time.LocalDate.now()).orElse(null);

        saveJournal(account, question, tarot, hex, aiInsight, energy);
    }

    private void saveJournal(Accounts a, String q, Library t, Library h, String ai, DailyEnergy e) {
        Journal j = new Journal();
        j.setAccount(a);
        j.setQuestion(q);
        j.setReadingType("concurrent_spread");
        if (t != null) {
            j.setTarotCardId(t.getId());
            j.setTarotSnapshot(t.getName() + ": " + t.getMeaningUpright());
        }
        if (h != null) {
            j.setIchingHexId(h.getId());
            j.setIchingSnapshot(h.getName() + ": " + h.getDescription());
        }
        j.setAiInsight(ai);

        if (e != null) {
            j.setMentalEnergy(e.getMentalEnergy());
            j.setEmotionalBalance(e.getEmotionalBalance());
            j.setFocusLevel(e.getFocusLevel());
            j.setActionReadiness(e.getActionReadiness());
            j.setSocialHarmony(e.getSocialHarmony());
            j.setPersonalDayNumber(e.getPersonalDayNumber());
        }
        journalRepository.save(j);
    }
}