package com.example.twitterApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;



public class MainActivity extends Activity implements OnMapReadyCallback {

    private final String apiKey = "Qko6HCmLeKYLsvuvU32rCQ0HF";
    private final String apiSecret = "MC0DWK47LllmBG6CEzowdtUyxqebXhM0lN0ori5qgaTtb2mQYA";
    //String consumerKey = "";
    //String consumerSecret = "";
    private AsyncTwitter twitter;
    private RequestToken requestToken;

    ListView listView;
    EditText editText;
    ArrayList<User> users = new ArrayList<User>();


    //Map<String, String> m = new HashMap<String, String>();
    List<String> titleList = new ArrayList<String>(Arrays.asList("data1", "temple", "Station"));
    List<String> snippetList = new ArrayList<String>(Arrays.asList("data1", "Kiyomizu", "kyoto station"));
    List<Double> latitudeList = new ArrayList<Double>(Arrays.asList(34.985442, 34.994856, 34.985460));
    List<Double> longititudeList = new ArrayList<Double>(Arrays.asList(135.758456,135.785046,135.758450));


    // getApplication()でアプリケーションクラスのインスタンスを拾う
    //final Globals globals = (Globals)this.getApplication();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Log.d("oncreate", "oncreate");
        // editTextを関連付け
        editText = (EditText)findViewById(R.id.editText);


        final ListView myListView = (ListView) findViewById(R.id.listView);
        //adapterをListviewにセット
        //setUserAdapters();
        final UserAdapter userAdapter = new UserAdapter(this,0,users);
        myListView.setAdapter(userAdapter);

        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナー
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i, long l){
                TextView name = (TextView) view.findViewById(R.id.name);
                Toast.makeText(MainActivity.this, name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //Twitter処理
        AsyncTwitterFactory factory = new AsyncTwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(apiKey, apiSecret);

        //非同期通信処理
        // Twitterと通信した際の動作を記述
        twitter.addListener(new TwitterAdapter() {
            //ArrayList<User> usersListener = new ArrayList<User>();
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




            //http://workpiles.com/2014/03/android-twitter4j-asynctwitter/
            @Override
            public void searched(QueryResult queryResult) {
                 users.clear();
                for (Status status : queryResult.getTweets()) {
                    //Log.d("searched", "gettext,geo:" + status.getText()+status.getGeoLocation());

                   /* User user = new twitter4j.User();
                    //status.getUser().getProfileImageURL()はString型
                    user.setImage(status.getUser().getProfileImageURL());
                    user.setName(status.getUser().getName());
                    user.setLocation(status.getText());

                    //検索結果に基づいてUserを作成しUsersに追加(:ArrayList<User>)
                    users.add(user);*/

                    //mapに表示するためのデータ
                    titleList.add((status.getUser().getName()));
                    snippetList.add(status.getText());
                    latitudeList.add(status.getGeoLocation().getLatitude());
                    longititudeList.add(status.getGeoLocation().getLongitude());
                    Log.d("searchedfor map", "name,text,lat,long:" + status.getUser().getScreenName()+status.getText()+status.getGeoLocation());


                }
                userAdapter.notifyDataSetChanged();

                //TODO ここでgooglemapをasyncする処理。
                //mapFragment.getMapAsync(this);
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

            Query query = new Query();
            //query.setQuery("");
            query.setCount(10);
            //QueryResult result;
            twitter.search(query);
        }



        //Google Map Fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        //onmapreadyのよみだし
        mapFragment.getMapAsync(this);

        //一回最初のデータの読み込み
        initSearch();
    }//end oncreate


    //Googlemapのよびだし
    @Override
    public void onMapReady(GoogleMap map) {
        for (int tmp=0; tmp<titleList.size(); tmp++) {
            LatLng location = new LatLng(latitudeList.get(tmp), longititudeList.get(tmp));
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

            map.addMarker(new MarkerOptions()
                    .title(titleList.get(tmp))
                    .snippet(snippetList.get(tmp))
                    .position(location));
            Log.d("onMapReady", "maptitile:" + titleList.get(tmp));
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
     /*public void tweet(View v){
        // ツイートする
        // twitter.updateStatus(editText.getText().toString());
     }
      // 更新ボタンの処理
     public void refresh(View v){
     // タイムラインを更新する
           //twitter.getHomeTimeline();
     }*/

    //検索ボタンの処理
     public void search(View v){

         String editTextStr = editText.getText().toString().trim();
         Toast.makeText(MainActivity.this,editTextStr, Toast.LENGTH_SHORT).show();
         Log.d("Clicled","serch clicked"+editTextStr);
         Query query = new Query();
         //query.setQuery(editTextStr);
         query.setCount(10);
         //清水寺から１キロ以内のtweet
         GeoLocation geo= new GeoLocation(34.994856, 135.785046);
         query.setGeoCode(geo,1,Query.KILOMETERS);
         QueryResult result;
         twitter.search(query);
         //result = twitter.search(query);
     }
    //検索ボタンの処理
    public void initSearch(){

        Log.d("initSearch()","data set");
        Query query = new Query();
        //100件取得
        query.setCount(20);
        //清水寺から１キロ以内のtweet
        GeoLocation geo= new GeoLocation(34.994856, 135.785046);
        query.setGeoCode(geo,1,Query.KILOMETERS);
        QueryResult result;
        twitter.search(query);
        //result = twitter.search(query);
    }


    public void clearText(View v) {
        editText.setText("");
    }


    //viewholderで高速化
    private static class ViewHolder {

        ImageView image;
        TextView name;
        TextView location;

        public ViewHolder(View view){

            this.image = (ImageView) view.findViewById(R.id.image);
            this.name = (TextView) view.findViewById(R.id.name);
            this.location = (TextView) view.findViewById(R.id.location);
        }
    }

    public class UserAdapter extends ArrayAdapter<User>{

        private LayoutInflater layoutInflater;

        //コンストラクタ
        public UserAdapter(Context context,int viewResourceId, ArrayList<User> users){
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public android.view.View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            //再利用できるviewがなかったらviewをつくる=LayoutInflaterを使ってlist_item.xmlをviewにする
            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.list_item_twitter, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //そのviewをデータにセット
            User user = (User) getItem(position);

            //アイコンの処理(画像の読み込み)
            viewHolder.image.setImageURI(Uri.parse(user.getImage()));
            Picasso.with(getContext()).load(user.getImage()).into(viewHolder.image);


            viewHolder.name.setText(user.getName());
            viewHolder.location.setText(user.getLocation());

            //viewを返す
            return convertView;
        }
    }




    //listviewで使うUserクラス
    private class User {
        private String image;
        private String name;
        private String location;

        public String getImage() {
            return this.image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return this.location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }
}


