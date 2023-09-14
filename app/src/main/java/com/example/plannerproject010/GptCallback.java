package com.example.plannerproject010;

public interface GptCallback {
    void onResponse(String gptResponse);
    void onFailure();
}
