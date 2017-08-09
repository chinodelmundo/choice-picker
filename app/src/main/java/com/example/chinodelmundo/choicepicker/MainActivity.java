package com.example.chinodelmundo.choicepicker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String QUESTION_ID = "com.example.chinodelmundo.choicepicker.QUESTION_ID";
    ListView listView;
    ArrayList<QuestionDataModel> dataModels;
    private static QuestionCustomAdapter adapter;
    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        getQuestionsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuestionsList();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void getQuestionsList(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT
        };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        dataModels = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemText = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT));
            Integer itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            dataModels.add(new QuestionDataModel(itemId, itemText));
        }
        cursor.close();

        adapter = new QuestionCustomAdapter(dataModels, MainActivity.this);
        listView = (ListView)findViewById(R.id.lvQuestions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                long questionID = dataModels.get(position).getId();

                Intent intent = new Intent(MainActivity.this, QuestionDetailsActivity.class);
                intent.putExtra(QUESTION_ID, questionID);
                startActivity(intent);
            }
        });
    }

    public void addNewQuestion(View view) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT, "");
        long questionID = db.insert(FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME, null, values);

        Intent intent = new Intent(this, QuestionDetailsActivity.class);
        intent.putExtra(QUESTION_ID, questionID);
        startActivity(intent);
    }

    public void deleteQuestion(long id, String text) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selectionQuestion = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgsQuestion = { Long.toString(id) };
        db.delete(FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME, selectionQuestion, selectionArgsQuestion);

        String selectionChoices = FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgsChoices = { Long.toString(id) };
        db.delete(FeedReaderContract.FeedEntry.CHOICES_TABLE_NAME, selectionChoices, selectionArgsChoices);

        Snackbar.make(findViewById(R.id.mainCoordinatorLayout), "Deleted: " + text, Snackbar.LENGTH_SHORT).show();
        getQuestionsList();
    }

    public void chooseItem(long id){
        Intent intent = new Intent(this, ChooseActivity.class);
        intent.putExtra(QUESTION_ID, id);
        startActivity(intent);
    }
}
