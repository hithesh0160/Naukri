package com.amazon.utils;

import java.net.http.*;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class HtmlBodyExtractor {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the URL: ");
        String url = scanner.nextLine();
        scanner.close();

        String htmlBody = getHtmlBodyFromUrl(url);
        System.out.println("\n--- Response Content ---\n" + htmlBody);
    }

    public static String getHtmlBodyFromUrl(String url) throws Exception {
        // Step 1: Setup cookie manager
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        // Step 2: Attach cookie manager to HttpClient
        HttpClient client = HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

        // Step 3: Build the request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .GET()
            .build();

        // Step 4: Send request
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        String contentType = response.headers().firstValue("Content-Type").orElse("unknown");
        String contentEncoding = response.headers().firstValue("Content-Encoding").orElse("none");

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Headers: " + response.headers());

        byte[] bodyBytes = response.body();

        // Debug: Print first 200 bytes in hex
        for (int i = 0; i < Math.min(200, bodyBytes.length); i++) {
            // System.out.printf("%02x ", bodyBytes[i]);
        }
        System.out.println();

        // Step 5: Detect charset
        String charset = "UTF-8";
        if (contentType.contains("charset=")) {
            charset = contentType.substring(contentType.indexOf("charset=") + 8).split("[;\"]")[0].trim();
        }

        // Step 6: Decode response body
        if (contentEncoding.equalsIgnoreCase("gzip")) {
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bodyBytes));
                 InputStreamReader isr = new InputStreamReader(gis, charset);
                 BufferedReader br = new BufferedReader(isr)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } else {
            try {
                return new String(bodyBytes, charset);
            } catch (Exception e) {
                System.out.println("Failed to decode with charset: " + charset + ". Showing as UTF-8.");
                return new String(bodyBytes, StandardCharsets.UTF_8);
            }
        }
    }
}