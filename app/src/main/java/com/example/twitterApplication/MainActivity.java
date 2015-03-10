package com.example.twitterApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Status;


public class MainActivity extends Activity {

    String apiKey = "777777";
    String apiSecret = "777777";
    Twitter twitter;
    ListView listView;
    EditText editText;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 初期化
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        try {
            // 検索ワードをセット
            query.setQuery("apple");
            // 1度のリクエストで取得するTweetの数（100が最大）
            query.setCount(100);
            query.resultType(Query.RECENT);

            //ここでおちる!?
            QueryResult result = twitter.search(query);

            Log.d("ヒット数 ", "hit"+result.getTweets().size());

            for (Status tweet : result.getTweets()) {
                String str = tweet.getText();
                // java.util.Date date = tweet.getCreatedAt();
                Log.d( "text","text:"+str);
            }
        } catch (TwitterException e) {

            Log.e("TwitterException","error");
            e.printStackTrace();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}


