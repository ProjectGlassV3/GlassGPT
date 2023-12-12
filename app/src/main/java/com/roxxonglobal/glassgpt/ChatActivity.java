package com.roxxonglobal.glassgpt;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.roxxonglobal.glassgpt.GlassGestureDetector;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.time.Duration;

import android.os.StrictMode;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity implements
        GlassGestureDetector.OnGestureListener {

    private static final int REQUEST_CODE = 999;
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String DELIMITER = "\n";

    private TextView resultTextView;
    private GlassGestureDetector glassGestureDetector;
    private List<String> mVoiceResults = new ArrayList<>(4);

    OpenAiService service = new OpenAiService(Secrets.API_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        resultTextView = findViewById(R.id.results);
        glassGestureDetector = new GlassGestureDetector(this, this);
        OpenAiService service = new OpenAiService(Secrets.API_KEY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String speechResult = null;
        if (resultCode == RESULT_OK) {
            final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.d(TAG, "results: " + results.toString());
            if (results != null && results.size() > 0 && !results.get(0).isEmpty()) {
                speechResult = results.get(0);
                updateUI(speechResult);
            }
        } else {
            Log.d(TAG, "Result not OK");
        }
        String gptResponse = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(speechResult);
        chatMessage.setRole("user");
        chatMessageList.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-4-1106-preview")
                .messages(chatMessageList).build();
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        updateUI(responseMessage.getContent());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                requestVoiceRecognition();
                return true;
            case SWIPE_DOWN:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void requestVoiceRecognition() {
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void updateUI(String result) {
        if (mVoiceResults.size() >= 4) {
            mVoiceResults.remove(mVoiceResults.size() - 1);
        }
        mVoiceResults.add(0, result);
        final String recognizedText = String.join(DELIMITER, mVoiceResults);
        resultTextView.setText(recognizedText);
    }
}