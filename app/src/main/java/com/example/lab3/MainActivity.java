package com.example.lab3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Object [] questions;
    ActivityResultLauncher<Intent> editActivityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        SharedPreferences orderedList = getSharedPreferences("orderedQuestions", Context.MODE_PRIVATE);


        TextView question = findViewById(R.id.question);
        TextView answer = findViewById(R.id.answer);
        TextView wrong1 = findViewById(R.id.wrong1);
        TextView wrong2 = findViewById(R.id.wrong2);

        questions =  preferences.getAll().values().toArray();

        List<String> temp = new ArrayList<>();
        for (Object q:questions) {
            if (!q.toString().isEmpty()) temp.add(q.toString());
        }
        questions = temp.toArray().clone();

        if (questions.length<1)
        {
            question.setText("");
            answer.setVisibility(View.INVISIBLE);
            wrong1.setVisibility(View.INVISIBLE);
            wrong2.setVisibility(View.INVISIBLE);
        }

        else {

            String [] questionsSet = orderedList.getString("orderedQuestions","").split(",");
            String firstQuestion = questionsSet [0];
            String answers = preferences.getString(firstQuestion,"");
            List<String> answersArray = Arrays.asList( answers.split(","));

            question.setText( answersArray.get( 0));
            List<String> newAnswersArray = answersArray.subList(1,4);
            Collections.shuffle(newAnswersArray);

            answer.setText(newAnswersArray.get( 0));
            wrong1.setText(newAnswersArray .get( 1));
            wrong2.setText(newAnswersArray.get(2));

        }

        ImageView edit = findViewById(R.id.editBtn);
        ImageView delete = findViewById(R.id.delBtn);
        ImageView add = findViewById(R.id.addBtn);
        ImageView next = findViewById(R.id.nextBtn);

        editActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data =  result.getData();
                    if (data!=null ) {
                        String quest = data.getStringExtra("question");
                        question.setGravity(Gravity.CENTER);
                        question.setText(quest);


                        String ans = data.getStringExtra("answer");
                        answer.setVisibility(View.VISIBLE);
                        answer.setText(ans);


                        String wr1 = data.getStringExtra("wrong1");
                        wrong1.setVisibility(View.VISIBLE);
                        wrong1.setText(wr1);

                        String wr2 =data.getStringExtra("wrong2");
                        wrong2.setVisibility(View.VISIBLE);
                        wrong2.setText(wr2);

                    }

                });

        add.setOnClickListener(view -> {
            int numQuestion = preferences.getAll().size();
            System.out.println("@@Number of questions  "+numQuestion);
            Intent intent = new Intent(MainActivity.this, Question.class);
            intent.putExtra("numQuestion",numQuestion);
            intent.putExtra("option","add");
            editActivityResultLauncher.launch(intent);
            questions =  preferences.getAll().values().toArray();

        });
        edit.setOnClickListener(view -> {
            int numQuestion = preferences.getAll().size();
            System.out.println("@@Number of questions  "+numQuestion);
            Intent intent = new Intent(MainActivity.this, Question.class);
            intent.putExtra("numQuestion",numQuestion);
            intent.putExtra("option","edit");
            intent.putExtra("question",question.getText().toString());
            intent.putExtra("answer",answer.getText().toString());
            intent.putExtra("wrong1",wrong1.getText().toString());
            intent.putExtra("wrong2",wrong2.getText().toString());
            editActivityResultLauncher.launch(intent);
        });
        next.setOnClickListener(view -> {
            questions =  preferences.getAll().values().toArray();
            List<String> temp1 = new ArrayList<>();
            for (Object q:questions) {
                if (!q.toString().isEmpty()) temp1.add(q.toString());
            }
            questions = temp1.toArray().clone();
            String[] strings= orderedList.getString("orderedQuestions","").split(",");
            if (Important.current < questions.length-1) {

               String[] choices = preferences.getString(strings[Important.current+1], "").split(",");

               List<String> newChoices = Arrays.asList(choices). subList(1,4);
               Collections.shuffle(newChoices);

               question.setText(choices[0]);
               answer.setText(newChoices.get(0));
               answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
               wrong1.setText(newChoices.get(1));
               wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
               wrong2.setText(newChoices.get(2));
               wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));

           }
           else {
               Toast.makeText(MainActivity.this, "There's no questions anymore", Toast.LENGTH_SHORT).show();
           }
            Important.current+=1;

                  });


        delete.setOnClickListener(view -> {
            List<String> questionsSet = Arrays.asList( orderedList.getString("orderedQuestions","").split(","));

            if (Important.current == 0 && questionsSet.size()>1)
            {
                String newOrderedSet = "";
                List<String> a = questionsSet.subList(Important.current+1,questionsSet.size());
                             for (int h =0 ;h<a.size();h++)
                {
                    newOrderedSet+= a.get(h);
                    if (h!= a.size()-1) newOrderedSet+=",";
                }

                orderedList.edit().putString("orderedQuestions",newOrderedSet).apply();
                preferences.edit().putString("Question "+ (Important.current + 1),"").apply();


                String[] strings= orderedList.getString("orderedQuestions","").split(",");
                String[] choices = preferences.getString(strings[Important.current], "").split(",");
                List<String> newChoices = Arrays.asList(choices). subList(1,4);
                Collections.shuffle(newChoices);

                question.setText(choices[0]);
                answer.setText(newChoices.get(0));
                answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong1.setText(newChoices.get(1));
                wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong2.setText(newChoices.get(2));
                wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));

            }

            else if (Important.current == questionsSet.size()-1 && questionsSet.size()>1)
            {
                String newOrderedSet = "";
                List<String> a = questionsSet.subList(0,questionsSet.size()-1);
                for (int h =0 ;h<a.size();h++)
                {
                    newOrderedSet+= a.get(h);
                    if (h!= a.size()-1) newOrderedSet+=",";
                }

                orderedList.edit().putString("orderedQuestions",newOrderedSet).apply();
                preferences.edit().putString("Question "+ (Important.current + 1),"").apply();
                Important.current-=1;

                String[] strings= orderedList.getString("orderedQuestions","").split(",");
                String[] choices = preferences.getString(strings[Important.current], "").split(",");
                List<String> newChoices = Arrays.asList(choices). subList(1,4);
                Collections.shuffle(newChoices);

                question.setText(choices[0]);
                answer.setText(newChoices.get(0));
                answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong1.setText(newChoices.get(1));
                wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong2.setText(newChoices.get(2));
                wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));


            }

            else if (Important.current == questionsSet.size()-1 && questionsSet.size()>1)
            {
                String newOrderedSet = "";
                List a = questionsSet.subList(0,questionsSet.size()-1);
                for (int h =0 ;h<a.size();h++)
                {
                    newOrderedSet+= a.get(h);
                    if (h!= a.size()-1) newOrderedSet+=",";
                }

                orderedList.edit().putString("orderedQuestions",newOrderedSet).apply();
                preferences.edit().putString("Question "+ (Important.current + 1),"").apply();
                Important.current-=1;

                String[] strings= orderedList.getString("orderedQuestions","").split(",");
                String[] choices = preferences.getString(strings[Important.current], "").split(",");
                List<String> newChoices = Arrays.asList(choices). subList(1,4);
                Collections.shuffle(newChoices);

                question.setText(choices[0]);
                answer.setText(newChoices.get(0));
                answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong1.setText(newChoices.get(1));
                wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong2.setText(newChoices.get(2));
                wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));


            }
            else if ( questionsSet.size()==1)
            {
                String newOrderedSet = "";


                  orderedList.edit().putString("orderedQuestions",newOrderedSet).apply();
                  preferences.edit().putString("Question "+ (Important.current + 1),"").apply();

                question.setText("");
                answer.setText("");
                answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong1.setText("");
                wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong2.setText("");
                wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));


            }

            else
            {
                String newOrderedSet = "";
                List<String> a = questionsSet.subList(0,questionsSet.size());
                System.out.println(a.size());
                for (int h =0 ;h<a.size();h++)
                {

                    if (h!= Important.current) {
                        newOrderedSet+= a.get(h);
                        newOrderedSet += ",";
                    }
                }
                System.out.println("Set "+newOrderedSet);//good List of questions
                orderedList.edit().putString("orderedQuestions",newOrderedSet).apply();
                preferences.edit().putString("Question "+ (Important.current + 1),"").apply();
                Important.current-=1;

                String[] strings= orderedList.getString("orderedQuestions","").split(",");
                String[] choices = preferences.getString(strings[Important.current], "").split(",");
                List<String> newChoices = Arrays.asList(choices). subList(1,4);
                Collections.shuffle(newChoices);

                question.setText(choices[0]);
                answer.setText(newChoices.get(0));
                answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong1.setText(newChoices.get(1));
                wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                wrong2.setText(newChoices.get(2));
                wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));

            }
        });


    }
    public void restart(View v)
    {   //this function return the user to the first question and i restore the backgroundColor of the choices
        Important.current = 0;
        System.out.println("clicked"+Important.current);
        SharedPreferences preferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        SharedPreferences orderedList = getSharedPreferences("orderedQuestions", Context.MODE_PRIVATE);

        List<String> questionsSet = Arrays.asList(orderedList.getString("orderedQuestions","").split(","));
       if (questionsSet.get(0).length()>0) {

           String answers = preferences.getString(questionsSet.get(Important.current), "");
           Object[] answersArray = answers.split(",");

           TextView question = findViewById(R.id.question);
           TextView answer = findViewById(R.id.answer);
           TextView wrong1 = findViewById(R.id.wrong1);
           TextView wrong2 = findViewById(R.id.wrong2);

           question.setText(answersArray[0].toString());
           answer.setText(answersArray[1].toString());
           answer.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
           wrong1.setText(answersArray[2].toString());
           wrong1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
           wrong2.setText(answersArray[3].toString());
           wrong2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));


       }
    }

    public void show_answer(View v) {
        TextView choice = findViewById(v.getId());
        TextView answer = findViewById(R.id.answer);
        TextView newview = findViewById(R.id.wrong1);
        TextView newview2 = findViewById(R.id.wrong2);

        SharedPreferences preferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        SharedPreferences orderedList = getSharedPreferences("orderedQuestions", Context.MODE_PRIVATE);
        String[] strings= orderedList.getString("orderedQuestions","").split(",");
        String correctQUestion = preferences.getString( strings[Important.current],"").split(",")[1];
        System.out.println(preferences.getString( strings[Important.current],""));

        //if the user get it right i set it's choice's backgroundColor green otherwise red and i set the other ones white
        boolean isCorrect = choice.getText().toString().compareTo(correctQUestion)==0;
        choice.setBackgroundColor(ContextCompat.getColor(this, isCorrect? R.color.green:R.color.red));
        if (newview.getId()!=choice.getId())
        {
        newview.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        }
        if (answer.getId()!=choice.getId())
        {
            answer.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        }

        if (newview2.getId()!=choice.getId())
        {
            newview2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        }

    }

}