package com.example.chinodelmundo.choicepicker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final String QUESTION_ID = "com.example.chinodelmundo.choicepicker.QUESTION_ID";
    ListView listView;
    ArrayList<QuestionDataModel> dataModels;
    private static QuestionCustomAdapter adapter;
    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = (ListView)findViewById(R.id.lvQuestions);
        retrieveQuestions();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void searchAction(String searchString) {
        retrieveQuestions(searchString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveQuestions();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void retrieveQuestions(String searchString){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT
        };

        String selection = null;
        String[] selectionArgs = null;

        if(!searchString.isEmpty()){
            selection = FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT + " LIKE ?";
            selectionArgs = new String[]{ "%" + searchString + "%" };
        }

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        dataModels = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemText = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT));
            Integer itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            dataModels.add(new QuestionDataModel(itemId, itemText));
        }
        cursor.close();
        updateListView();
    }

    public void retrieveQuestions() {
        retrieveQuestions("");
    }

    public void updateListView() {
        adapter = new QuestionCustomAdapter(dataModels, MainActivity.this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                long questionID = dataModels.get(position).getId();

                Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
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
        retrieveQuestions();
    }

    public void editItem(long id){
        Intent intent = new Intent(this, QuestionDetailsActivity.class);
        intent.putExtra(QUESTION_ID, id);
        startActivity(intent);
    }
}