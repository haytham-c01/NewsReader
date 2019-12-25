package com.example.student.newsreader;


import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;

     DownloadTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            String pageContent = getPageContent(strings[0]);
            JSONArray jsonArray= new JSONArray(pageContent);

            mainActivity.sqLiteDatabase.execSQL("DELETE FROM articles");
            
            int numberOfPages= 20;
            if(jsonArray.length() < numberOfPages){
                numberOfPages= jsonArray.length();
            }
            Log.i("length", String.valueOf(jsonArray.length()));
            
            for(int i = 0; i< numberOfPages; i++){
                Log.i("length", String.valueOf(jsonArray.getInt(i)));
                StringBuilder sb= new StringBuilder();
                sb.append("https://hacker-news.firebaseio.com/v0/item/")
                        .append(jsonArray.getLong(i))
                        .append(".json?print=pretty");

                pageContent= getPageContent(sb.toString());

                JSONObject jsonObject= new JSONObject(pageContent);

                // return true if the element value is null, or if no value exists
                if(!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                    SQLiteStatement statement= mainActivity.sqLiteDatabase.compileStatement(
                            "INSERT INTO articles(articleId, title, content) VALUES(?, ?, ?)");

                    long articleId=  jsonArray.getLong(i);
                    String title= jsonObject.getString("title");
                    String content= getPageContent(jsonObject.getString("url"));

                    statement.bindLong(1, articleId);
                    statement.bindString(2, title);
                    statement.bindString(3, content);
                    statement.execute();
                }
            } // end for
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    private String getPageContent(String string) throws IOException {
        URL url= new URL(string);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        InputStream inputStream= connection.getInputStream();

        BufferedReader br= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder sb= new StringBuilder();
        String line;
        while((line= br.readLine())!= null){
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mainActivity.updateAppData();
    }
}
