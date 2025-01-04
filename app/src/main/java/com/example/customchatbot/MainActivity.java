package com.example.customchatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ChatAdapter.OnYesNoClickListener {
    private RecyclerView chatRecyclerView;
    private EditText inputEditText;
    private ImageButton sendButton;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private enum ChatState {
        NAME, AGE, PHONE, EMAIL, CUSTOM_QUESTIONS
    }

    private ChatState currentState = ChatState.NAME;
    private int currentQuestionIndex = 0;

    private String[] customQuestions = {
            "Do you like Android development?",
            "Have you used chatbots before?",
            "Are you interested in AI?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);

        // Set up RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Set up click listener
        sendButton.setOnClickListener(v -> onSendButtonClick());

        // Start the conversation
        addBotMessage("Hi! What's your name?", false);
    }

    private void onSendButtonClick() {
        String userInput = inputEditText.getText().toString().trim();
        if (!userInput.isEmpty()) {
            addUserMessage(userInput);
            processUserInput(userInput);
            inputEditText.setText("");
        }
    }

    @Override
    public void onYesClicked(int position) {
        processUserInput("Yes");
        chatAdapter.notifyQuestionAnswered(position, "Yes");
    }

    @Override
    public void onNoClicked(int position) {
        processUserInput("No");
        chatAdapter.notifyQuestionAnswered(position, "No");
    }

    private void addBotMessage(String message, boolean showYesNoButtons) {
        chatMessages.add(new ChatMessage(message, false, showYesNoButtons));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        });

    }

    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        });
    }

    private void processUserInput(String input) {
        switch (currentState) {
            case NAME:
                currentState = ChatState.AGE;
                addBotMessage("Nice to meet you, " + input + "! How old are you?", false);
                break;
            case AGE:
                if (validateAge(input)) {
                    currentState = ChatState.PHONE;
                    addBotMessage("What's your phone number?", false);
                } else {
                    addBotMessage("Enter valid input. Age should be between 3 and 90.", false);
                }
                break;
            case PHONE:
                if (validatePhone(input)) {
                    currentState = ChatState.EMAIL;
                    addBotMessage("What's your email address?", false);
                } else {
                    addBotMessage("Enter valid input. Phone number should be exactly 10 digits.", false);
                }
                break;
            case EMAIL:
                if (validateEmail(input)) {
                    currentState = ChatState.CUSTOM_QUESTIONS;
                    askCustomQuestion();
                } else {
                    addBotMessage("Enter valid input. Email should not contain the @ symbol.", false);
                }
                break;
            case CUSTOM_QUESTIONS:
                if (currentQuestionIndex < customQuestions.length - 1) {
                    currentQuestionIndex++;
                    askCustomQuestion();
                } else {
                    addBotMessage("Thank you for answering all the questions!", false);
                    // End of conversation
                }
                break;
        }
    }

    private boolean validateAge(String age) {
        try {
            int ageValue = Integer.parseInt(age);
            return ageValue >= 3 && ageValue <= 90;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validatePhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean validateEmail(String email) {
        return email.contains("@");
    }

    private void askCustomQuestion() {
        if (currentQuestionIndex < customQuestions.length) {
            addBotMessage(customQuestions[currentQuestionIndex], true);
            inputEditText.setEnabled(false);
            sendButton.setEnabled(false);
            inputEditText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
            chatRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }
            });
        }
    }
}