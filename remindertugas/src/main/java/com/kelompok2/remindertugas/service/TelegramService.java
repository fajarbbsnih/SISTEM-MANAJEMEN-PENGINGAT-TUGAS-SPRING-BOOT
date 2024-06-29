package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.entity.Contact;
import com.kelompok2.remindertugas.repository.ContactRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Optional;

@Service
public class TelegramService {

    @Value("${telegrambot.token}")
    private String botToken;

    private final RestTemplate restTemplate;
    private long lastUpdateId = 0;

    @Autowired
    private ContactRepository contactRepository;

    public TelegramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendMessage(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{ \"chat_id\": \"" + chatId + "\", \"text\": \"" + message + "\" }";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, String.class);
    }

    public void sendContactRequest(String chatId) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject keyboardButton = new JSONObject();
        keyboardButton.put("text", "Registarsi Nomormu");
        keyboardButton.put("request_contact", true);

        JSONArray keyboardRow = new JSONArray();
        keyboardRow.put(keyboardButton);

        JSONArray keyboard = new JSONArray();
        keyboard.put(keyboardRow);

        JSONObject replyMarkup = new JSONObject();
        replyMarkup.put("keyboard", keyboard);
        replyMarkup.put("one_time_keyboard", true);
        replyMarkup.put("resize_keyboard", true);

        JSONObject body = new JSONObject();
        body.put("chat_id", chatId);
        body.put("text", "Tekan tombol dibawah untuk melakukan registrasi");
        body.put("reply_markup", replyMarkup);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
    }

    public String getUpdates(long offset) {
        String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=" + offset;
        return restTemplate.getForObject(url, String.class);
    }

    @Scheduled(fixedRate = 6000)
    public void pollForUpdates() {
        String updates = getUpdates(lastUpdateId + 1);
        processUpdates(updates);
    }

    private void processUpdates(String updates) {
        try {
            JSONObject jsonObject = new JSONObject(updates);
            JSONArray results = jsonObject.getJSONArray("result");
            if (!results.isEmpty()) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    long updateId = result.getLong("update_id");

                    if (result.has("message")) {
                        JSONObject message = result.getJSONObject("message");
                        JSONObject chat = message.getJSONObject("chat");
                        String chatId = String.valueOf(chat.getBigInteger("id"));

                        if (message.has("contact")) {
                            JSONObject contact = message.getJSONObject("contact");
                            String phoneNumber = contact.getString("phone_number");
                            if (phoneNumber.startsWith("62")) {
                                phoneNumber = "0" + phoneNumber.substring(2);
                            }

                            Optional<Contact> contactOptional = contactRepository.findByPhoneNumber(phoneNumber);
                            if (contactOptional.isPresent()) {
                                Contact data = contactOptional.get();
                                data.setChatId(chatId);
                                contactRepository.save(data);
                                sendMessage(chatId, "Registrasi Berhasil");
                            }
                        } else {
                            sendContactRequest(chatId);
                        }
                        lastUpdateId = updateId;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
