package com.xmliu.itravel.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bugtags.library.Bugtags;
import com.gc.materialdesign.views.Switch;
import com.xmliu.itravel.R;

/**
 * Created by xmliu on 2016/1/31.
 */
public class SettingActivity extends ToolbarActivity {
    private Switch shakeSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar.setTitle("系统设置");

        RelativeLayout shakeLayout = (RelativeLayout) findViewById(R.id.setting_feedback_layout);
        shakeSwitch = (Switch) findViewById(R.id.setting_feedback_shake);

        if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
            shakeSwitch.setChecked(true);
        }else {
            shakeSwitch.setChecked(false);
        }

        shakeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
                    shakeSwitch.setChecked(false);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }else {
                    shakeSwitch.setChecked(true);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                }
            }
        });

        shakeSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                if(check){
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                }else{
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }
            }
        });

    }
}
