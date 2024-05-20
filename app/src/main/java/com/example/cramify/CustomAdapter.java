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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList deck_id, deck_name;

    private OnDeckClickListener listener;
    private OnDeckLongClickListener longClickListener;

    CustomAdapter(Context context, ArrayList<String> deck_id, ArrayList<String> deck_name, OnDeckClickListener clickListener, OnDeckLongClickListener longClickListener) {
        this.context = context;
        this.deck_id = deck_id;
        this.deck_name = deck_name;
        this.listener = clickListener;
        this.longClickListener = longClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.deck_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtdeck_name.setText(String.valueOf(deck_name.get(position)));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeckDeleteClick(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeckClick(position);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onDeckLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return deck_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtdeck_name;
        ImageButton delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtdeck_name = itemView.findViewById(R.id.card_question);
            delete = itemView.findViewById(R.id.delete_card);
        }
    }

    public interface OnDeckClickListener {
        void onDeckClick(int position);

        void onDeckDeleteClick(int position);
    }

    public interface OnDeckLongClickListener {
        void onDeckLongClick(int position);
    }
}
