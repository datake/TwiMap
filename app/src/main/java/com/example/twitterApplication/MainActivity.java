package com.example.twitterApplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

//import java.util.logging.Handler;


public class MainActivity extends Activity {

    private final String apiKey = "Qko6HCmLeKYLsvuvU32rCQ0HF";
    private final String apiSecret = "MC0DWK47LllmBG6CEzowdtUyxqebXhM0lN0ori5qgaTtb2mQYA";
    //String consumerKey = "";
    //String consumerSecret = "";
    private AsyncTwitter twitter;
    private RequestToken requestToken;

    ListView listView;
    EditText editText;
    ArrayAdapter<String> adapter;
    Handler h = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("oncreate", "oncreate");

        listView = (ListView)findViewById(R.id.listView);
        // listViewを関連付け
        //editText = (EditText)findViewById(R.id.editText1);
        // editTextを関連付け
        // adapterを作成、listViewと関連付け
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);




        AsyncTwitterFactory factory = new AsyncTwitterFactory();
        twitter = factory.getInstance();

        twitter.setOAuthConsumer(apiKey, apiSecret);

        //非同期通信処理
        // Twitterと通信した際の動作を記述
        twitter.addListener(new TwitterAdapter() {
            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                //1 RequestTokenを取得したときの処理
                //OAuth認証をつかうためブラウザにとばす
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(token.getAuthorizationURL());
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                // TODO2 AccessTokenを取得したときの処理を書く
                // SharedPreferencesを使ってデータを保存すること毎回認証する必要を省く
                SharedPreferences pref = getSharedPreferences("ACCESS_TOKEN", MODE_PRIVATE);
                Editor editor = pref.edit();
                // AccessTokenの必要な情報を書き込み
                editor.putString("token", token.getToken());
                editor.putString("tokenSecret", token.getTokenSecret());
                editor.commit(); // 内容を保存


            }


            @Override
            public void gotHomeTimeline(ResponseList<Status> statuses) {
                // TODO3 タイムラインを取得したときの処理を書く

                final ResponseList<Status> slist = statuses;

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        adapter.clear();
                        for (int i = 0; i < slist.size(); i++) {
                            Status s = slist.get(i);// ポストをひとつずつ取り出し
                            adapter.add(s.getUser().getName() + ", " + s.getText()); // adapterに加える
                        }
                    }
                });
            }
        });//end addlistener


        // 保存した情報を読み込み
        SharedPreferences pref = getSharedPreferences("ACCESS_TOKEN", MODE_PRIVATE);
        String token = pref.getString("token", null);
        String tokenSecret = pref.getString("tokenSecret", null);
        // 読み込んだ情報があれば
        if (token != null && tokenSecret != null) {
            // 新しいアクセストークンの作成
            AccessToken accessToken = new AccessToken(token, tokenSecret);
            // Twitterにアクセストークンをセット
            twitter.setOAuthAccessToken(accessToken);
            // タイムライン取得
            twitter.getHomeTimeline();
        }


        // 初期化
        // twitter = new TwitterFactory().getInstance();
        //twitter.setOAuthConsumer(apiKey,apiSecret);
        //Query query = new Query();





        /*try {
            // 検索ワードをセット
            query.setQuery("apple");
            // 1度のリクエストで取得するTweetの数（100が最大）
            query.setCount(100);
            query.resultType(Query.RECENT);

            //ここでおちる!?
            QueryResult result;
            //result = twitter.search(query);
            result = twitter.search(new Query("Apple Watch"));

            Log.d("ヒット数 ", "hit"+result.getTweets().size());

            for (Status tweet : result.getTweets()) {
                String str = tweet.getText();
                // java.util.Date date = tweet.getCreatedAt();
                Log.d( "text","text:"+str);
            }
        } catch (TwitterException e) {

            Log.e("TwitterException","error");
            e.printStackTrace();
        }*/

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

        Uri data = intent.getData(); // インテントから情報を取得
        // インテントに情報がある かつ キャンセルされていない場合
        if(data != null && data.getQueryParameter("oauth_denied") == null){
            String verifier = data.getQueryParameter("oauth_verifier");
            // 認証された証を取得
            twitter.getOAuthAccessTokenAsync(verifier);
            // Twitterにアクセスするための処理
        }


    }

     // 認証ボタンを押した時の処理
     public void oauth(View v){
     // OAuth認証のためのRequestTokenを取得する
        twitter.getOAuthRequestTokenAsync("callback://MainActivity");
      }
    // ツイートボタンを押した時の処理
      public void tweet(View v){
        // ツイートする
        // twitter.updateStatus(editText.getText().toString());
      }
      // 更新ボタンの処理
       public void refresh(View v){
      // タイムラインを更新する
           twitter.getHomeTimeline();
      }


}


