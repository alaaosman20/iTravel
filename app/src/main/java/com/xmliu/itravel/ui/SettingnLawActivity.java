package com.xmliu.itravel.ui;

import android.os.Bundle;

import com.xmliu.itravel.R;

/**
 * Date: 2016/4/22 10:48
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class SettingnLawActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_law);

        toolbar.setTitle("法律声明");

    }
}
