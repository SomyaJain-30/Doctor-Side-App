package com.example.doctorside;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> langs;
    Context context;

    public LanguageAdapter(Context context, List<String> langs)
    {
        this.langs = langs;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_card,null);
        return new LanguageAdapter.LanguageCardHolder(layoutInflater);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((LanguageCardHolder) holder).lan.setText(langs.get(holder.getAdapterPosition()));
        ((LanguageCardHolder) holder).del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                langs.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return langs.size();
    }

    public class LanguageCardHolder extends RecyclerView.ViewHolder{
        TextView lan;
        ImageView del;
        @SuppressLint("ResourceAsColor")
        public LanguageCardHolder(@NonNull View itemView) {
            super(itemView);
            lan = itemView.findViewById(R.id.time);
            del = itemView.findViewById(R.id.close);
            lan.setTextSize(10);

        }
    }
}
