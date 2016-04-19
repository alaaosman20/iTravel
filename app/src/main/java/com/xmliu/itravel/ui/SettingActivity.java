package com.xmliu.itravel.ui;


import android.os.Bundle;

import com.xmliu.itravel.R;

/**
 * Created by xmliu on 2016/1/31.
 */
public class SettingActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar.setTitle("系统设置");

    }
}
