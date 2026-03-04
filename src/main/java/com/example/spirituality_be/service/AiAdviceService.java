package com.example.spirituality_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAdviceService {

    @Value("${groq.api.key:}")
    private String groqApiKey;

    private final RestTemplate restTemplate;

    public AiAdviceService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateDailyGuidance(String userElement, int mental, int emotional, int focus, int action, int social, int personalDay) {


        if (groqApiKey == null || groqApiKey.trim().isEmpty() || groqApiKey.contains("YOUR_GROQ_API_KEY")) {
            System.out.println("No Groq API key found. Using Mock AI Service.");
            return generateMockGuidance(userElement, mental, emotional, focus, action, social, personalDay);
        }

        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            String prompt = String.format(
                "Bạn là một cố vấn tâm linh kết hợp Đông-Tây. Dựa trên bản mệnh của user: %s. " +
                "Năng lượng hôm nay: Mental Energy: %d, Emotional Balance: %d, Focus Level: %d, " +
                "Action Readiness: %d, Social Harmony: %d. Personal Day Number: %d. " +
                "Sinh một lời khuyên cá nhân hóa, thực tế, tích cực cho ngày hôm nay. " +
                "Lời khuyên phải: Ngắn gọn (tối đa 2-3 câu ngắn), cụ thể (nên làm gì, tránh gì), khuyến khích cân bằng tâm lý, không mê tín mờ ảo. " +
                "LUÔN LUÔN VÀ BẮT BUỘC TRẢ LỜI NGAY VÀO LỜI KHUYÊN, KHÔNG MỞ BÀI CHÀO HỎI HAY KẾT LUẬN THỪA.",
                userElement, mental, emotional, focus, action, social, personalDay
            );

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama3-8b-8192");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");
                    if (messageObj != null && messageObj.get("content") != null) {
                        return messageObj.get("content").toString().trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Groq API Call failed. Falling back to mock.");
        }


        return generateMockGuidance(userElement, mental, emotional, focus, action, social, personalDay);
    }

    private String generateMockGuidance(String userElement, int mental, int emotional, int focus, int action, int social, int personalDay) {
        StringBuilder advice = new StringBuilder();
        advice.append("Bạn mang mệnh ").append(userElement).append(". Hôm nay ");

        if (mental > 75 && focus > 75) {
            advice.append("tinh thần và sự tập trung của bạn rất sắc bén. ");
        } else if (mental < 40 || focus < 40) {
            advice.append("tâm trí có phần xao nhãng và dễ mệt mỏi. ");
        } else {
            advice.append("trạng thái tinh thần ở mức ổn định. ");
        }

        if (emotional < 50) {
            advice.append("Emotional Balance đang thấp, bạn dễ bị cảm xúc chi phối. Hãy dành 15 phút tĩnh tâm hoặc đi dạo để cân bằng lại. ");
        } else if (emotional > 80) {
            advice.append("Cảm xúc của bạn vô cùng tích cực và lan tỏa. ");
        }

        if (action > 70) {
            advice.append("Action Readiness cao, đây là ngày tuyệt vời để đưa ra những quyết định quan trọng.");
        } else if (action < 40) {
            advice.append("Hãy ưu tiên hoàn thành những việc đang dang dở thay vì bắt đầu cái mới.");
        }

        return advice.toString();
    }
}