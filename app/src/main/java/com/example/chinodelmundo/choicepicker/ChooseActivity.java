package com.example.chinodelmundo.choicepicker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChooseActivity extends AppCompatActivity {
    public static final String QUESTION_ID = "com.example.chinodelmundo.choicepicker.QUESTION_ID";
    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(ChooseActivity.this);

    String question;
    List<String> choicesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Intent intent = getIntent();
        long questionId = intent.getLongExtra(MainActivity.QUESTION_ID, 0);

        question = getQuestion(questionId);
        choicesArrayList = getChoices(questionId);

        chooseItem();
    }

    public String getQuestion(long questionId){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT
        };
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(questionId) };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String questionText = "";
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            questionText = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT));
        }
        cursor.close();

        return questionText;
    }

    public List<String> getChoices(long questionId){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT
        };
        String selection = FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = { Long.toString(questionId) };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.CHOICES_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<String> choicesArrayList = new ArrayList<String>();
        while(cursor.moveToNext()) {
            String itemText = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT));
            choicesArrayList.add(itemText);
        }
        cursor.close();

        return choicesArrayList;
    }

    public void chooseItem(){
        Random rand = new Random();
        String choice = "";
        if(choicesArrayList.size() > 0){
            int n = rand.nextInt(choicesArrayList.size());
            choice = choicesArrayList.get(n);
        }else{
            choice = "**No choices available**";
        }

        TextView textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(question.toUpperCase());

        TextView textViewChoice = (TextView) findViewById(R.id.textViewChoice);
        textViewChoice.setText(choice.toUpperCase());
    }

    public void onClickAgain(View view){
        chooseItem();
    }

    public void onClickNewQuestion(View view){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT, "");
        long questionID = db.insert(FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME, null, values);

        Intent intent = new Intent(this, QuestionDetailsActivity.class);
        intent.putExtra(QUESTION_ID, questionID);
        startActivity(intent);
    }

    public void onClickPreviousActivity(View view){
        finish();
    }
}
