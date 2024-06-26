package com.example.cramify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayActivity extends AppCompatActivity {
    private TextView textQuestion, textAnswer;
    private Button buttonShowAnswer, buttonNext;

    private ImageButton buttonBack;
    private CardView questionCardView, answerCardView;
    private ArrayList<String> questions = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();
    private List<QuestionAnswerPair> questionAnswerPairs = new ArrayList<>();
    private int currentCardIndex = 0;
    private boolean isShowingAnswer = false;

    String DECK_ID, DECK_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        textQuestion = findViewById(R.id.textQuestion); // Changed to R.id.textQuestion
        textAnswer = findViewById(R.id.textAnswer); // Changed to R.id.textAnswer
        buttonShowAnswer = findViewById(R.id.buttonShowAnswer);
        buttonNext = findViewById(R.id.buttonNext);
        questionCardView = findViewById(R.id.questionCardView);
        answerCardView = findViewById(R.id.answerCardView);
        buttonBack = findViewById(R.id.backBtn);

        // Get data from intent
        questions = getIntent().getStringArrayListExtra("questions");
        answers = getIntent().getStringArrayListExtra("answers");

        getData();

        if (questions == null || answers == null || questions.isEmpty() || answers.isEmpty()) {
            Toast.makeText(this, "No questions or answers provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create question-answer pairs
        for (int i = 0; i < questions.size(); i++) {
            questionAnswerPairs.add(new QuestionAnswerPair(questions.get(i), answers.get(i)));
        }

        // Shuffle the question-answer pairs
        Collections.shuffle(questionAnswerPairs);

        // Set initial card
        setCard(currentCardIndex);

        buttonShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextCard();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayActivity.this, SecondActivity.class);
                intent.putExtra("DECK_ID", DECK_ID);
                intent.putExtra("DECK_NAME", DECK_NAME);
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
    private void setCard(int index) {
        QuestionAnswerPair pair = questionAnswerPairs.get(index);
        textQuestion.setText(pair.getQuestion());
        textAnswer.setText(pair.getAnswer());
        questionCardView.setVisibility(View.VISIBLE); // Show the question card
        answerCardView.setVisibility(View.GONE); // Hide the answer card
        buttonNext.setVisibility(View.GONE);
        isShowingAnswer = false;
    }

    private void showAnswer() {
        if (!isShowingAnswer) {
            questionCardView.setVisibility(View.GONE); // Hide the question card
            answerCardView.setVisibility(View.VISIBLE); // Show the answer card
        } else {
            questionCardView.setVisibility(View.VISIBLE); // Show the question card
            answerCardView.setVisibility(View.GONE); // Hide the answer card
        }
        isShowingAnswer = !isShowingAnswer;
        buttonNext.setVisibility(View.VISIBLE);
    }

    private void showNextCard() {
        currentCardIndex++;
        if (currentCardIndex >= questions.size()) {
            Toast.makeText(this, "End of cards", Toast.LENGTH_SHORT).show();
            currentCardIndex = 0;
        }
        setCard(currentCardIndex);
    }

}
