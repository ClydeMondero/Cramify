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

public class HomeActivity extends AppCompatActivity implements CustomAdapter.OnDeckClickListener, CustomAdapter.OnDeckLongClickListener {

    Dialog myDialog, updateDialog;
    DBHelper DB;
    RecyclerView recyclerView;
    ArrayList<String> deck_id, deck_name;
    CustomAdapter customAdapter;
    int userId; // User ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user ID from intent extras
        userId = getIntent().getIntExtra("USER_ID", -1);


        myDialog = new Dialog(this);
        updateDialog = new Dialog(this);
        DB = new DBHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        deck_id = new ArrayList<>();
        deck_name = new ArrayList<>();

        displayDeck();

        customAdapter = new CustomAdapter(HomeActivity.this, deck_id, deck_name, this, this); // Pass this as the long click listener
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
    }

    void displayDeck(){
        Cursor cursor = DB.readDeck(userId); // Pass user ID to readDeck method
        if(cursor.getCount() == 0){
            Toast.makeText(HomeActivity.this, "No Deck Found", Toast.LENGTH_SHORT). show();
        }
        else{
            while(cursor.moveToNext()){
                deck_id.add(cursor.getString(0));
                deck_name.add(cursor.getString(1));
            }
        }
    }

    //for adding deck
    public void ShowPopup(View v){
        EditText deckName;
        TextView close;
        Button addDeck;
        myDialog.setContentView(R.layout.activity_adding_deck);

        close = (TextView) myDialog.findViewById(R.id.close);
        addDeck = (Button) myDialog.findViewById(R.id.addCard);
        deckName = (EditText) myDialog.findViewById(R.id.updatedDeckName);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        addDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deck = deckName.getText().toString();

                if(deck.equals("") ){
                    Toast.makeText(HomeActivity.this, "Please enter deck name", Toast.LENGTH_SHORT). show();
                }
                else{
                    Boolean checkDeck = DB.addDeck(deck, userId); // Pass user ID to addDeck method
                    if (checkDeck == true) {
                        Toast.makeText(HomeActivity.this, "Add Successful", Toast.LENGTH_SHORT).show();
                        recreate(); // Refresh the activity
                    } else {
                        Toast.makeText(HomeActivity.this, "Adding Failed", Toast.LENGTH_SHORT).show();
                        myDialog.dismiss();
                    }
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }



    //for updating deck

    private void showUpdatePopup(int position) {
        String deckId = deck_id.get(position).toString();
        String deckName = deck_name.get(position).toString();
        TextView close;

        updateDialog.setContentView(R.layout.activity_updating_deck);
        EditText updatedDeckName = updateDialog.findViewById(R.id.updatedDeckName);
        close = (TextView) updateDialog.findViewById(R.id.close);

        updatedDeckName.setText(deckName);
        Button updateButton = updateDialog.findViewById(R.id.addCard);
        // Handle update button click

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated deck name
                String updatedName = updatedDeckName.getText().toString();
                // Update the deck name in the database
                boolean isUpdated = DB.updateDeckName(deckId, updatedName);
                if (isUpdated) {
                    // Update the deck name in the ArrayList
                    deck_name.set(position, updatedName);
                    // Notify the adapter about the change
                    customAdapter.notifyDataSetChanged();
                    // Dismiss the dialog
                    updateDialog.dismiss();
                    // Show a toast message
                    Toast.makeText(HomeActivity.this, "Deck name updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a toast message if update fails
                    Toast.makeText(HomeActivity.this, "Failed to update deck name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateDialog.show();
    }


    // one click
    @Override
    public void onDeckClick(int position) {
        // Get the clicked deck ID or any other necessary data
        String deckId = deck_id.get(position).toString();
        String deckName = deck_name.get(position).toString();


        // Start the new activity with the clicked deck ID
        Intent intent = new Intent(HomeActivity.this, SecondActivity.class);
        intent.putExtra("DECK_ID", deckId);
        intent.putExtra("DECK_NAME", deckName);
        startActivity(intent);
    }

    // long press
    @Override
    public void onDeckLongClick(int position) {
        // Show popup to update deck name
        showUpdatePopup(position);
    }

    public void onDeckDeleteClick(int position) {
        String deckId = deck_id.get(position);
        // Delete the deck from the database
        boolean isDeleted = DB.deleteDeck(deckId);
        if (isDeleted) {
            // Remove the deck from the ArrayList
            deck_id.remove(position);
            deck_name.remove(position);
            // Notify the adapter about the change
            customAdapter.notifyItemRemoved(position);
            // Show a toast message
            Toast.makeText(this, "Deck deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast message if deletion fails
            Toast.makeText(this, "Failed to delete deck", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the activity
    }
}