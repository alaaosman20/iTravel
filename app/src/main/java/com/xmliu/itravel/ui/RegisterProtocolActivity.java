package com.xmliu.itravel.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;
import com.xmliu.itravel.widget.ProgressWebView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by diyangxia on 2015/10/16.
 * 注册的用户协议或服务条款界面，目前内容为杜撰
 */
public class RegisterProtocolActivity extends BaseActivity {

//    private TextView mTV;
    private ProgressWebView mWebView;
    private ImageButton mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_protocol);


        mBackBtn = (ImageButton) findViewById(R.id.common_back_btn);
//        mTV = (TextView) findViewById(R.id.register_protocol_content);
        mWebView = (ProgressWebView) findViewById(R.id.register_protocol_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/service.html");

//        mTV.setText(getFromAssets("service.txt"));
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
