package com.gmail.muslimovatanya.testserver;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView resultConnection;
    private EditText urlConnect;
    private ScrollView content;

    private String mGlobalResult="";
    private Long nowStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final MainActivity activity = this;

        urlConnect = (EditText) findViewById(R.id.urlConnection);
        urlConnect.setSelection(urlConnect.getText().length());
        urlConnect.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        final Button http = (Button) findViewById(R.id.http);
        http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextToEditText("http://");
            }
        });

        Button https = (Button) findViewById(R.id.https);
        https.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextToEditText("https://");
            }
        });

        content = (ScrollView) findViewById(R.id.content);

        resultConnection = (TextView) findViewById(R.id.result);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testServer();
            }
        });
    }

    private void setTextToEditText(String text) {
        if (urlConnect.getText().toString().isEmpty()) {
            urlConnect.setText(text);
            urlConnect.setSelection(urlConnect.getText().length());
        }
    }

    private void testServer() {
        nowStart = (new Date().getTime())/1000 + 60L;
        for (int i = 0; i < 100; i++) {
            mGlobalResult = "";
            new TestServer().execute(urlConnect.getText().toString());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (urlConnect != null) {
            Rect rect = new Rect();
            urlConnect.getGlobalVisibleRect(rect);

            if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                urlConnect.clearFocus();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private class TestServer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(params[0]);

                //mGlobalResult = setGlobalResult(mGlobalResult, "Url", url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                //mGlobalResult = setGlobalResult(mGlobalResult, "UrlConnection", urlConnection.toString());

                int responseCode = urlConnection.getResponseCode();
                mGlobalResult = setGlobalResult(mGlobalResult, "ResponseCode", responseCode+"");

            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);

            Long nowFinish = (new Date().getTime())/1000 + 60L;
            String delta = "start: " + nowStart + " finish: " + nowFinish + " delta: " + (nowFinish - nowStart);
            //mGlobalResult = setGlobalResult(mGlobalResult, "TimeDelta", delta);

            resultConnection.setText(mGlobalResult);
            content.post(new Runnable() {
                @Override
                public void run() {
                    content.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    public String setGlobalResult(String stringList, String tag, String data) {
        stringList = stringList + " " + tag + ": " + data + "\n";
        return stringList;
    }

}
