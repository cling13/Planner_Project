package com.example.plannerproject010;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GptCallback {

    public static final String MY_SECRET_KEY = "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";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public CompletableFuture<String> callAPI(String question) {
        CompletableFuture<String> future = new CompletableFuture<>();
        JSONObject object = new JSONObject();
        try {
            object.put("model", "text-davinci-003");
            object.put("prompt", question);
            object.put("max_tokens", 4000);
            object.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 실패시 CompletableFuture 에 예외를 완료
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 성공 시 CompletableFuture 에 결과를 완료
                    String responseBody = response.body().string();
                    future.complete(responseBody);
                } else {
                    // 실패 시 CompletableFuture 에 예외를 완료
                    future.completeExceptionally(new IOException("Unexpected response code: " + response.code()));
                }
            }
        });

        return future;
    }
}
