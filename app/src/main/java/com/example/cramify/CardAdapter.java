package com.example.cramify;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<String> cardId;

    private ArrayList<String> cardQuestions;
    private ArrayList<String> cardAnswers;

    private OnCardClickListener listener;
    private OnCardLongClickListener longClickListener;


    public CardAdapter(Context context, ArrayList<String> cardId, ArrayList<String> cardQuestions, ArrayList<String> cardAnswers, OnCardClickListener listener, OnCardLongClickListener longClickListener) {
        this.context = context;
        this.cardId = cardId;
        this.cardQuestions = cardQuestions;
        this.cardAnswers = cardAnswers;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_row, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtCardQuestion.setText(cardQuestions.get(position));
        holder.txtCardAnswer.setText(cardAnswers.get(position));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCardDeleteClick(position); // Invoke the listener method
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onCardLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardQuestions.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView txtCardQuestion;
        TextView txtCardAnswer;
        ImageButton delete;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCardQuestion = itemView.findViewById(R.id.card_question);
            txtCardAnswer = itemView.findViewById(R.id.card_answer);
            delete = itemView.findViewById(R.id.delete_card);
        }
    }

    public interface OnCardClickListener {
        void onCardDeleteClick(int position);
    }

    public interface OnCardLongClickListener {
        void onCardLongClick(int position);
    }
}

