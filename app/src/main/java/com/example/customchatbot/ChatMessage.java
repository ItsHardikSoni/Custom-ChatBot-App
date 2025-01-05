package com.example.customchatbot;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private boolean showYesNoButtons;
    private boolean answered;
    private String userAnswer;

    public ChatMessage(String message, boolean isUser, boolean showYesNoButtons) {
        this.message = message;
        this.isUser = isUser;
        this.showYesNoButtons = showYesNoButtons;
        this.answered = false;
        this.userAnswer = null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean showYesNoButtons() {
        return showYesNoButtons;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}

