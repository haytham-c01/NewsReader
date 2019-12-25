package com.example.student.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    SQLiteDatabase sqLiteDatabase;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> titles;
    private List<String> content;
    static final String EXTRA_WEB_CONTENT= "webContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles= new ArrayList<>();
        content= new ArrayList<>();

        sqLiteDatabase= openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles" +
                "(id INTEGER PRIMARY KEY, articleId INTERGER, title VARCHAR, content VARCHAR)");

        ListView listView= findViewById(R.id.listView);
        arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        updateAppData();
        DownloadTask task= new DownloadTask(this);
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
    }

     void updateAppData(){
        Cursor c= sqLiteDatabase.rawQuery("SELECT * FROM articles", null);
        int titleIndex= c.getColumnIndex("title");
        int contentIndex= c.getColumnIndex("content");

        while(c.moveToNext()){
            titles.add(c.getString(titleIndex));
            content.add(c.getString(contentIndex));
        }
        c.close();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent= new Intent(this, WebViewActivity.class);
        intent.putExtra(EXTRA_WEB_CONTENT, content.get(i));
        startActivity(intent);
    }
}
