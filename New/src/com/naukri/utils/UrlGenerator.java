package com.naukri.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class UrlGenerator {
    private String baseUrl = "https://www.amazon.in/s";
    private Map<String, String> params = new LinkedHashMap<>();

    public UrlGenerator() {
        // Default constructor uses the baseUrl above
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public String generateUrl() {
        if (params.isEmpty()) return baseUrl;
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        return baseUrl + "?" + sj.toString();
    }

    // Accept all params as method arguments for the new URL
    public static String generateUrlWithParams(String k, String i, String rh, String s, String dc, String ds) {
        String baseUrl = "https://www.amazon.in/s";
        StringJoiner sj = new StringJoiner("&");
        sj.add("k=" + k);
        sj.add("i=" + i);
        sj.add("rh=" + rh);
        sj.add("s=" + s);
        sj.add("dc=" + dc);
        sj.add("ds=" + ds);
        return baseUrl + "?" + sj.toString();
    }
}
