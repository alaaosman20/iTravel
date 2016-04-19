package com.xmliu.itravel.ui;

import android.os.Bundle;

import com.xmliu.itravel.R;

/**
 * Created by xmliu on 2016/1/30.
 */
public class AboutActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar.setTitle("关于");
    }
}
