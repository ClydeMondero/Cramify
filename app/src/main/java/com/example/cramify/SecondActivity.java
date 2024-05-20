package com.example.cramify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener, CardAdapter.OnCardLongClickListener {

    Dialog addDialog, updateDialog;
    TextView deck_title;
    String DECK_ID, DECK_NAME;

    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    ArrayList<String> card_id, card_question, card_answer;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        deck_title = findViewById(R.id.deck_title);
        DB = new DBHelper(this);

        addDialog = new Dialog(this);
        updateDialog = new Dialog(this);

        recyclerView = findViewById(R.id.CardRecycler);
        card_id = new ArrayList<>();
        card_question = new ArrayList<>();
        card_answer = new ArrayList<>();

        getData();
        setData();

        displayCard();

        cardAdapter = new CardAdapter(SecondActivity.this, card_id, card_question, card_answer, this, this);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SecondActivity.this));

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, PlayActivity.class);
                intent.putStringArrayListExtra("questions", card_question);
                intent.putStringArrayListExtra("answers", card_answer);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        if (getIntent().hasExtra("DECK_ID") && getIntent().hasExtra("DECK_NAME")) {
            DECK_ID = getIntent().getStringExtra("DECK_ID");
            DECK_NAME = getIntent().getStringExtra("DECK_NAME");
        } else {
            Toast.makeText(this, "No Data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        deck_title.setText(DECK_NAME);
    }

    void displayCard() {
        Cursor cursor = DB.readCards(DECK_ID);
        if (cursor.getCount() == 0) {
            Toast.makeText(SecondActivity.this, "No Card Found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                card_id.add(cursor.getString(0));
                card_question.add(cursor.getString(1));
                card_answer.add(cursor.getString(2));
            }
        }
        cursor.close();
    }

    private void refreshData() {
        card_id.clear();
        card_question.clear();
        card_answer.clear();
        displayCard();
        cardAdapter.notifyDataSetChanged();
    }

    public void ShowPopup(View v) {
        EditText question, answer;
        TextView close;
        Button addCard;
        addDialog.setContentView(R.layout.activity_adding_card);

        close = addDialog.findViewById(R.id.close);
        addCard = addDialog.findViewById(R.id.addCard);
        question = addDialog.findViewById(R.id.question);
        answer = addDialog.findViewById(R.id.answer);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.dismiss();
            }
        });
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ques = question.getText().toString();
                String ans = answer.getText().toString();

                if (ques.equals("") || ans.equals("")) {
                    Toast.makeText(SecondActivity.this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
                } else {
                    boolean inserted = DB.addCard(ques, ans, DECK_ID);
                    if (inserted) {
                        Toast.makeText(SecondActivity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                        refreshData();
                    } else {
                        Toast.makeText(SecondActivity.this, "Failed to add card", Toast.LENGTH_SHORT).show();
                    }
                    addDialog.dismiss();
                }
            }
        });
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addDialog.show();
    }

    private void showUpdatePopup(int position) {
        String cardId = card_id.get(position);
        String cardQuestion = card_question.get(position);
        String cardAnswer = card_answer.get(position);

        TextView close;
        updateDialog.setContentView(R.layout.activity_updating_card);
        EditText updatedCardQuestion = updateDialog.findViewById(R.id.updateQuestion);
        EditText updatedCardAnswer = updateDialog.findViewById(R.id.updateAnswer);
        close = updateDialog.findViewById(R.id.close);

        updatedCardQuestion.setText(cardQuestion);
        updatedCardAnswer.setText(cardAnswer);
        Button updateButton = updateDialog.findViewById(R.id.updateCard);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedQuestion = updatedCardQuestion.getText().toString();
                String updatedAnswer = updatedCardAnswer.getText().toString();

                boolean isUpdated = DB.updateCardDetails(cardId, updatedQuestion, updatedAnswer);

                if (isUpdated) {
                    card_question.set(position, updatedQuestion);
                    card_answer.set(position, updatedAnswer);
                    cardAdapter.notifyItemChanged(position);
                    updateDialog.dismiss();
                    Toast.makeText(SecondActivity.this, "Card details updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SecondActivity.this, "Failed to update card details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateDialog.show();
    }

    @Override
    public void onCardDeleteClick(int position) {
        String cardId = card_id.get(position);
        boolean isDeleted = DB.deleteCard(cardId);
        if (isDeleted) {
            card_id.remove(position);
            card_question.remove(position);
            card_answer.remove(position);
            cardAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete card", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCardLongClick(int position) {
        showUpdatePopup(position);
    }
}
