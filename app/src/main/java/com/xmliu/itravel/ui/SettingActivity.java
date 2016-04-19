package com.xmliu.itravel.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        TextView feedBackTV = (TextView) findViewById(R.id.drawer_id_feedback);
        feedBackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
            }
        });
    }
}
