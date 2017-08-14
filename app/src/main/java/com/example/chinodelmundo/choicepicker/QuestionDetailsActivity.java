package com.example.chinodelmundo.choicepicker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailsActivity extends BaseActivity {
    public static final String QUESTION_ID = "com.example.chinodelmundo.choicepicker.QUESTION_ID";
    long questionId;
    ListView listView;
    EditText questionEditText;
    ArrayList<ChoiceDataModel> dataModels;
    private static ChoiceCustomAdapter adapter;
    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(QuestionDetailsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        questionId = intent.getLongExtra(MainActivity.QUESTION_ID, 0);

        if(questionId != 0){
            getQuestion();
            retrieveChoices();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_question_details;
    }

    @Override
    protected void searchAction(String searchString) {
        retrieveChoices(searchString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateQuestionText();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void getQuestion(){
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

        questionEditText = (EditText) findViewById(R.id.etQuestion);
        questionEditText.setText(questionText);
    }

    public void retrieveChoices(String searchString){
        String selection;
        String[] selectionArgs;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT
        };

        if(searchString.isEmpty()){
            selection = FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID + " = ?";
            selectionArgs = new String[]{ Long.toString(questionId) };
        }else{
            selection = FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID + " = ? AND " + FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT + " LIKE ?";
            selectionArgs = new String[]{ Long.toString(questionId) , "%" + searchString + "%"};
        }

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.CHOICES_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        dataModels = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemText = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT));
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            dataModels.add(new ChoiceDataModel(itemId, questionId, itemText));
        }
        cursor.close();
        updateListView();
    }

    public void retrieveChoices(){
        retrieveChoices("");
    }

    public void updateListView() {
        adapter = new ChoiceCustomAdapter(dataModels, QuestionDetailsActivity.this);
        listView = (ListView)findViewById(R.id.lvChoices);
        listView.setAdapter(adapter);
    }

    public void addNewChoice(View view) {
        EditText newChoice = (EditText) findViewById(R.id.etNewChoice);
        String newChoiceString = newChoice.getText().toString();
        newChoice.setText("");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_TEXT, newChoiceString);
        values.put(FeedReaderContract.FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID, questionId);
        db.insert(FeedReaderContract.FeedEntry.CHOICES_TABLE_NAME, null, values);

        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        retrieveChoices();
    }

    public void deleteChoice(long id, String text){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        db.delete(FeedReaderContract.FeedEntry.CHOICES_TABLE_NAME, selection, selectionArgs);

        Snackbar.make(findViewById(R.id.mainCoordinatorLayout), "Deleted: " + text, Snackbar.LENGTH_SHORT).show();
        retrieveChoices();
    }

    public void chooseItem(View view){
        EditText questionEditText = (EditText) findViewById(R.id.etQuestion);
        String questionText = questionEditText.getText().toString().trim();

        TextInputLayout questionLayout = (TextInputLayout) findViewById(R.id.layoutQuestion);
        TextInputLayout choiceLayout = (TextInputLayout) findViewById(R.id.layoutChoice);

        if (questionText.isEmpty()) {
            questionLayout.setError(getString(R.string.question_empty));
        } else {
            questionLayout.setErrorEnabled(false);
        }
        if(dataModels.size() == 0){
            choiceLayout.setError(getString(R.string.choices_empty));
        } else {
            choiceLayout.setErrorEnabled(false);
        }

        if(!questionLayout.isErrorEnabled() && !choiceLayout.isErrorEnabled()){
            updateQuestionText();
            Intent intent = new Intent(this, ChooseActivity.class);
            intent.putExtra(QUESTION_ID, questionId);
            startActivity(intent);
        }
    }

    public void updateQuestionText(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        EditText questionEditText = (EditText) findViewById(R.id.etQuestion);
        String newQuestion = questionEditText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.QUESTIONS_COLUMN_NAME_TEXT, newQuestion);

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(questionId) };

        db.update(
                FeedReaderContract.FeedEntry.QUESTIONS_TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }
}
