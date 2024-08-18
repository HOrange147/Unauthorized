package org.example.check;

import org.example.utils.Utils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Check {

    public static String exploiteUrl(String text) throws Exception {
        List<String> base64StrList = Utils.getBase64Str();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // 使用固定线程池

        try {
            // 使用并行流和CompletableFuture进行并发处理
            List<CompletableFuture<String>> futures = base64StrList.parallelStream()
                    .map(base64Str -> CompletableFuture.supplyAsync(() -> {
                        try {
                            int responseCode = request(text, base64Str);
                            System.out.println(responseCode);
                            if (responseCode == 200) {
                                String encodedPayload = Base64.getEncoder().encodeToString(base64Str.getBytes());
                                return "[++] 存在弱口令 " + encodedPayload;
                            } else if (responseCode == 401) {
                                return null; // 继续检查下一个
                            } else {
                                throw new IOException("Unexpected response code: " + responseCode); // 终止检查
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            // 等待所有任务完成并获取结果
            for (CompletableFuture<String> future : futures) {
                String result = future.join();
                if (result != null && result.startsWith("[++]")) {
                    return result; // 找到弱口令
                }
            }

            return "[--] 不存在弱口令";
        } finally {
            executorService.shutdown();
        }
    }
    public static CompletableFuture<String> exploiteAll(String text) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> base64StrList = null;
            try {
                base64StrList = Utils.getBase64Str();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (String base64Str : base64StrList) {
                try {
                    int responseCode = request(text, base64Str);
                    System.out.println(responseCode);
                    if (responseCode == 200) {
                        String encodedPayload = Base64.getEncoder().encodeToString(base64Str.getBytes());
                        return "[++] 存在弱口令 " + encodedPayload;
                    } else if (responseCode == 401) {
                        continue;
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "[--] 不存在弱口令 " + text;
        });
    }

    public static Integer request(String url, String payload) throws IOException {
        HttpURLConnection urlConnection = null;
        URL httpURl = new URL(url);
        Integer responseCode = null;
        try {
            urlConnection = (HttpURLConnection)httpURl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(1000); //请求超时时间
            urlConnection.setReadTimeout(1000);
            urlConnection.setRequestProperty("Accept","*/*");
            urlConnection.setRequestProperty("Connection","close");
            urlConnection.setRequestProperty("Authorization","Basic "+ payload);
            urlConnection.setRequestProperty("Accept-Language","en");
            urlConnection.setRequestProperty("User-Agent", Utils.getRandomUserAgent());
            urlConnection.setUseCaches(false);
            responseCode = urlConnection.getResponseCode();
        } catch (IOException e) {
            return 000;
        }
        return responseCode;
    }
}