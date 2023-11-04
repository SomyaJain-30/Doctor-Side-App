package com.example.doctorside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<String> list;
    List<Integer> response;

    public SurveyAdapter(Context context , List<String> list, List<Integer> response){
        this.context = context;
        this.list = list;
        this.response = response;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_ques, parent,false);
        return new SurveyAdapter.Question(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question q = (Question) holder;
        q.ques.setText(list.get(position));
        holder.setIsRecyclable(false);

        // Set the checked state based on the checkedPositions list
        q.ans.setTag(holder.getAdapterPosition());
        q.ans.setId(holder.getAdapterPosition());

        System.out.println(holder.getAdapterPosition() + " triggered");
        int pos= holder.getAdapterPosition();
        int responseValue = response.get(pos);

        q.yes.setChecked(responseValue == 1);
        q.no.setChecked(responseValue == 0);

        System.out.println(response);

        q.yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    response.set(pos,1);
                else
                    response.set(pos,0);
            }
        });

        q.no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    response.set(pos,0);
                else
                    response.set(pos,1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Question extends RecyclerView.ViewHolder{
        TextView ques;
        RadioGroup ans;
        RadioButton yes;
        RadioButton no;
        public Question(@NonNull View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.ques);
            ans = itemView.findViewById(R.id.ans);
            yes = itemView.findViewById(R.id.yes);
            no = itemView.findViewById(R.id.no);

        }
    }
}
