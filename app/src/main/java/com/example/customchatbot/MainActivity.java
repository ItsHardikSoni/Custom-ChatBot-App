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

    private enum NestedQuestionState {
        NONE, YES_NESTED, NO_NESTED
    }

    private ChatState currentState = ChatState.NAME;
    private NestedQuestionState nestedQuestionState = NestedQuestionState.NONE;
    private int currentQuestionIndex = 0;
    private int nestedQuestionIndex = 0;

    private String[] customQuestions = {
            "Do you like Android development?",
            "Have you used chatbots before?",
            "Are you interested in AI?"
    };

    private String[][] yesNestedQuestions = {
            {"Which programming language do you prefer for Android development?", "How long have you been developing Android apps?"},
            {"What kind of chatbots have you used?", "Have you ever developed a chatbot yourself?"},
            {"What area of AI interests you the most?", "Have you worked on any AI projects?"}
    };

    private String[][] noNestedQuestions = {
            {"Are you interested in learning Android development?", "What other mobile platforms are you interested in?"},
            {"Are you interested in learning about chatbots?", "What other technologies are you interested in?"},
            {"What areas of technology interest you the most?", "Have you worked on any technology projects?"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> onSendButtonClick());

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
        addUserMessage("Yes");
        processUserInput("Yes");
        chatAdapter.notifyQuestionAnswered(position, "Yes");
    }

    @Override
    public void onNoClicked(int position) {
        addUserMessage("No");
        processUserInput("No");
        chatAdapter.notifyQuestionAnswered(position, "No");
    }

    private void addBotMessage(String message, boolean showYesNoButtons) {
        chatMessages.add(new ChatMessage(message, false, showYesNoButtons));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();

        boolean showInput = !showYesNoButtons;
        inputEditText.setVisibility(showInput ? View.VISIBLE : View.GONE);
        sendButton.setVisibility(showInput ? View.VISIBLE : View.GONE);
    }

    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
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
                    addBotMessage("Enter valid input. Email should contain the @ symbol.", false);
                }
                break;
            case CUSTOM_QUESTIONS:
                processCustomQuestions(input);
                break;
        }
    }

    private void processCustomQuestions(String input) {
        if (nestedQuestionState == NestedQuestionState.NONE) {
            if (input.equalsIgnoreCase("Yes")) {
                nestedQuestionState = NestedQuestionState.YES_NESTED;
                askNestedQuestion();
            } else if (input.equalsIgnoreCase("No")) {
                nestedQuestionState = NestedQuestionState.NO_NESTED;
                askNestedQuestion();
            } else {
                if (currentQuestionIndex < customQuestions.length - 1) {
                    currentQuestionIndex++;
                    askCustomQuestion();
                } else {
                    addBotMessage("Thank you for answering all the questions!", false);
                    disableUserInput();
                }
            }
        } else {
            processNestedQuestion(input);
        }
    }

    private void askNestedQuestion() {
        String[] questions = (nestedQuestionState == NestedQuestionState.YES_NESTED)
                ? yesNestedQuestions[currentQuestionIndex]
                : noNestedQuestions[currentQuestionIndex];

        if (nestedQuestionIndex < questions.length) {
            boolean showYesNoButtons = (nestedQuestionState == NestedQuestionState.NO_NESTED);
            addBotMessage(questions[nestedQuestionIndex], showYesNoButtons);
        } else {
            nestedQuestionState = NestedQuestionState.NONE;
            currentQuestionIndex++;
            nestedQuestionIndex = 0;
            if (currentQuestionIndex < customQuestions.length) {
                askCustomQuestion();
            } else {
                addBotMessage("Thank you for answering all the questions!", false);
                disableUserInput();
                inputEditText.setEnabled(false);
                sendButton.setEnabled(false);
            }
        }
    }

    private void processNestedQuestion(String input) {
        nestedQuestionIndex++;
        askNestedQuestion();
    }

    private void askCustomQuestion() {
        if (currentQuestionIndex < customQuestions.length) {
            addBotMessage(customQuestions[currentQuestionIndex], true);
            nestedQuestionState = NestedQuestionState.NONE;
            nestedQuestionIndex = 0;
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

    private void scrollToBottom() {
        chatRecyclerView.post(() -> {
            int lastItemPosition = chatAdapter.getItemCount() - 1;
            if (lastItemPosition >= 0) {
                chatRecyclerView.smoothScrollToPosition(lastItemPosition);
            }
        });
    }

    private void disableUserInput() {
        inputEditText.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        inputEditText.setHint("Chat finished");
    }
}

