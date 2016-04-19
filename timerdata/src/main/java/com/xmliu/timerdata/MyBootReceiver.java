/*
 *  *
 *  *************************************************************************
 *  *
 *  *  Copyright (C) 2015 XMLIU diyangxia@163.com.
 *  *
 *  *                       All rights reserved.
 *  *
 *  **************************************************************************
 */

package com.xmliu.timerdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Date: 2016/4/8 9:31
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description:
 * adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -c android.intent.categor.DEFAULT -n com.xmliu.timerdata/.MyBootReceiver
 * 大部分机型会默认禁止app开机启动，即使添加了开机启动权限。这时需要手动到手机的开机自启项里把该应用设为自启
 */
public class MyBootReceiver extends BroadcastReceiver {
    Calendar c;
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receive Boot Broadcast", Toast.LENGTH_LONG).show();
        c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);

//        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.phone");
//        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(appIntent);

        Intent ootStartIntent=new Intent(context,MainActivity.class);
        ootStartIntent.putExtra("boot","坑爹啊，自启动");
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ootStartIntent);
        if(hour>=0&&hour<=10)
        {
            Toast.makeText(context, "上午", Toast.LENGTH_LONG).show();
        }
        if(hour>10&&hour<13)
        {
            Toast.makeText(context, "中午", Toast.LENGTH_LONG).show();
        }
        if(hour>=13&&hour<=18)
        {
            Toast.makeText(context, "下午", Toast.LENGTH_LONG).show();
        }
        if(hour>18&&hour<24)
        {
            Toast.makeText(context, "晚上", Toast.LENGTH_LONG).show();
        }
        System.out.println("hour"+c.get(Calendar.HOUR_OF_DAY));
    }
}
