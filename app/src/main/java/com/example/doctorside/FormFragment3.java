package com.example.doctorside;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class FormFragment3 extends Fragment {
    RecyclerView surveyRv;
    SurveyAdapter surveyAdapter;
    List<String> list;
    List<Integer> response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_form3, container, false);
        surveyRv = v.findViewById(R.id.survey_rec);
        list = new ArrayList<>();
        response = new ArrayList<>();

        list.add("Have you personally experienced issues with addiction, such as struggles with substances like alcohol or drugs?");
        list.add("Have you had difficulty adjusting to significant life changes, such as a major relocation, job loss, or the end of a significant relationship?");
        list.add("Have you encountered behavioral challenges or issues, like problems managing impulsive behaviors or disruptive actions in your personal life?");
        list.add("Have you struggled with conduct-related problems, such as engaging in illegal activities or consistently violating societal norms and rules?");
        list.add("Have you faced personal crisis situations, like a sudden loss of a loved one or experiencing a natural disaster, that had a profound impact on your mental health?");
        list.add("Did you experience challenges related to your own developmental stages and milestones, like identity formation or transitioning through life stages such as adolescence or middle age?");
        list.add("Have you personally dealt with emotional issues or difficulties, such as feelings of intense sadness, anxiety, or mood swings?");
        list.add("Have you had challenges in your interpersonal relationships, such as conflicts with family, friends, or colleagues, like disagreements or strained connections?");
        list.add("Have you encountered internal or intra psychic issues within your own thoughts and feelings, like persistent self-doubt, self-criticism, or intrusive thoughts?");
        list.add("Have you experienced physical health issues that affected your mental well-being, such as chronic pain, disability, or serious medical conditions?");
        list.add("Have you faced psychosocial challenges in your life, such as discrimination or socioeconomic disparities, that impacted your overall mental well-being?");

        for (int i = 0; i < list.size(); i++) {
            response.add(-1);
        }

        surveyRv.setLayoutManager(new LinearLayoutManager(getContext()));
        //System.out.println("From frag" + response);
        surveyAdapter = new SurveyAdapter(getContext(), list, response);
        surveyRv.setAdapter(surveyAdapter);


        return v;
    }
    public boolean checkValues(){

        for (Integer value : response) {
            if (value == -1) {
                Toast.makeText(getContext(), "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}