package com.example.project;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

public class Network extends AsyncTask<Void, Void, String> {
    TextView tv_response;
    String prompt;
    public Network(TextView tv_response, String result){
        this.tv_response = tv_response;
        this.prompt = "Generate 1 short conversation starter as a visitor while at " + result + ". Less than 20 words. Do not include quotation marks.";
        tv_response.setText("Loading...");
    }

//    TextView tv_response;
    @Override
    protected String doInBackground(Void... voids) {
        try {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        messages.add(systemMessage);
            OpenAiService service = new OpenAiService("nope");
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-3.5-turbo")
                    .n(1)

                    //.echo(false)
                    .build();
            String response = service.createChatCompletion(completionRequest).getChoices().get(0).getMessage().toString();
            // 36 chars in response prefix and 31 in suffix
            return response.substring(36, response.length() - 31);
        }
        catch(Exception e){
            return "Error " + e;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result obtained from the background thread
        tv_response.setText(result);
    }
}