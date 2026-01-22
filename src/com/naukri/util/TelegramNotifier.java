package com.naukri.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelegramNotifier {
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotifier.class);
    
    private static final String BOT_TOKEN;
    private static final String CHAT_ID;
    private static final int MESSAGE_LIMIT = 3800; // Telegram limit is 4096, keep buffer
    
    static {
        // Try to read from config.properties first, fallback to environment variables
        String token = null;
        String chatId = null;
        
        try {
            token = ConfigUtil.getConfig("telegram.token", "telegram.token");
            chatId = ConfigUtil.getConfig("telegram.chatid", "telegram.chatid");
            
            // If config values are empty or placeholder, try environment variables
            if (token == null || token.isEmpty() || token.equals("telegram.token")) {
                token = System.getenv("TELEGRAM_TOKEN");
            }
            if (chatId == null || chatId.isEmpty() || chatId.equals("telegram.chatid")) {
                chatId = System.getenv("TELEGRAM_CHAT_ID");
            }
        } catch (Exception e) {
            logger.debug("Could not read from config, trying environment variables");
            token = System.getenv("TELEGRAM_TOKEN");
            chatId = System.getenv("TELEGRAM_CHAT_ID");
        }
        
        BOT_TOKEN = token;
        CHAT_ID = chatId;
    }
    
    public static void sendSuccessNotification(String resumeName) {
        if (!isConfigured()) {
            logger.info("Telegram not configured, skipping notification");
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        
        String message = String.format(
            "✅ *Naukri Resume Upload Successful*\n\n" +
            "📄 *Resume:* `%s`\n" +
            "🕒 *Time:* %s\n" +
            "💻 *Status:* Completed successfully",
            resumeName,
            timestamp
        );
        
        sendMessage(message);
    }
    
    public static void sendFailureNotification(String error) {
        if (!isConfigured()) {
            logger.info("Telegram not configured, skipping notification");
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        
        // Escape special characters in error message
        String safeError = error.replace("_", "\\_").replace("*", "\\*").replace("`", "\\`");
        
        String message = String.format(
            "❌ *Naukri Resume Upload Failed*\n\n" +
            "⚠️ *Error:* %s\n" +
            "🕒 *Time:* %s\n" +
            "💻 Check logs for details",
            safeError,
            timestamp
        );
        
        sendMessage(message);
    }
    
    /**
     * Split message into Telegram-safe chunks
     */
    private static List<String> splitMessage(String message) {
        List<String> chunks = new ArrayList<>();
        int length = message.length();
        
        for (int i = 0; i < length; i += MESSAGE_LIMIT) {
            int end = Math.min(i + MESSAGE_LIMIT, length);
            chunks.add(message.substring(i, end));
        }
        
        return chunks;
    }
    
    /**
     * Send message to Telegram with Markdown formatting
     */
    private static void sendMessage(String message) {
        try {
            List<String> chunks = splitMessage(message);
            logger.info("Sending Telegram message in {} chunk(s)", chunks.size());
            
            for (int i = 0; i < chunks.size(); i++) {
                logger.info("Sending chunk {}/{}", i + 1, chunks.size());
                sendChunk(chunks.get(i));
            }
            
            logger.info("Telegram notification sent successfully!");
            
        } catch (Exception e) {
            logger.error("Error sending Telegram notification: {}", e.getMessage());
        }
    }
    
    /**
     * Send a single message chunk to Telegram
     */
    private static void sendChunk(String text) throws Exception {
        String urlString = String.format(
            "https://api.telegram.org/bot%s/sendMessage",
            BOT_TOKEN
        );
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        String postData = String.format(
            "chat_id=%s&text=%s&parse_mode=Markdown",
            URLEncoder.encode(CHAT_ID, StandardCharsets.UTF_8.toString()),
            URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
        );
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            // Read response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                br.readLine(); // Just consume the response
            }
        } else {
            logger.warn("Failed to send Telegram notification. Response code: {}", responseCode);
            // Read error response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String errorResponse = br.readLine();
                logger.warn("Error response: {}", errorResponse);
            }
        }
    }
    
    /**
     * Check if Telegram is configured
     */
    private static boolean isConfigured() {
        boolean configured = BOT_TOKEN != null && !BOT_TOKEN.isEmpty() 
            && CHAT_ID != null && !CHAT_ID.isEmpty();
        
        if (!configured) {
            logger.debug("Telegram credentials not set. Set TELEGRAM_TOKEN and TELEGRAM_CHAT_ID environment variables.");
        }
        
        return configured;
    }
}
