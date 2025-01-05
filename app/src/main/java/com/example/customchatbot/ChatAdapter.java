package com.example.customchatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;
    private OnYesNoClickListener onYesNoClickListener;

    public interface OnYesNoClickListener {
        void onYesClicked(int position);
        void onNoClicked(int position);
    }

    public ChatAdapter(List<ChatMessage> chatMessages, OnYesNoClickListener listener) {
        this.chatMessages = chatMessages;
        this.onYesNoClickListener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view, onYesNoClickListener, chatMessages);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.bind(message, position);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void notifyQuestionAnswered(int position, String answer) {
        ChatMessage message = chatMessages.get(position);
        message.setAnswered(true);
        message.setUserAnswer(answer);
        notifyItemChanged(position);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        LinearLayout yesNoButtonsLayout;
        Button yesButton;
        Button noButton;
        OnYesNoClickListener onYesNoClickListener;
        List<ChatMessage> chatMessages;

        ChatViewHolder(@NonNull View itemView, OnYesNoClickListener onYesNoClickListener, List<ChatMessage> chatMessages) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            yesNoButtonsLayout = itemView.findViewById(R.id.yesNoButtonsLayout);
            yesButton = itemView.findViewById(R.id.yesButton);
            noButton = itemView.findViewById(R.id.noButton);
            this.onYesNoClickListener = onYesNoClickListener;
            this.chatMessages = chatMessages;
        }

        void bind(ChatMessage message, int position) {
            messageTextView.setText(message.getMessage());
            if (message.isUser()) {
                messageTextView.setBackgroundResource(R.drawable.user_message_background);
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                yesNoButtonsLayout.setVisibility(View.GONE);
            } else {
                messageTextView.setBackgroundResource(R.drawable.bot_message_background);
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                if (message.showYesNoButtons()) {
                    if (message.isAnswered()) {
                        yesNoButtonsLayout.setVisibility(View.GONE);
                    } else {
                        yesNoButtonsLayout.setVisibility(View.VISIBLE);
                        yesButton.setOnClickListener(v -> onYesNoClickListener.onYesClicked(getAdapterPosition()));
                        noButton.setOnClickListener(v -> onYesNoClickListener.onNoClicked(getAdapterPosition()));
                    }
                } else {
                    yesNoButtonsLayout.setVisibility(View.GONE);
                }
            }
        }
    }
}

