package com.example.twitterApplication;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by take on 15/03/14.
 */
//global変数のため
public class Globals extends Application {
    //グローバルに使用する変数
    List<String> titleList = new ArrayList<String>();
    List<String> snippetList = new ArrayList<String>();
    List<Double> latitudeList = new ArrayList<Double>();
    List<Double> longititudeList = new ArrayList<Double>();


    //全て初期化するメソッド
    public void GlobalsAllInit() {
        titleList.add("station");
        snippetList.add("kyoto station");
        latitudeList.add(34.985460);
        longititudeList.add(135.758450);
    }
}