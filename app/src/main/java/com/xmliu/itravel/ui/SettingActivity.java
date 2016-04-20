package com.xmliu.itravel.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
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

        TextView shakeTV = (TextView) findViewById(R.id.setting_feedback_shake);

        shakeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }else {
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                }
            }
        });


    }
}
