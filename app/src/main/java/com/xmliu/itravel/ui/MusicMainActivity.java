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

package com.xmliu.itravel.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;

/**
 * Date: 2016/4/5 15:41
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class MusicMainActivity extends BaseActivity{

    private BottomBar bottomBar;

    private TextView contentTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_tab);
        contentTV = (TextView) findViewById(R.id.bottom_bar_tab_content);
        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.menu_bottom_bar, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                if(menuItemId == R.id.music1){
                    contentTV.setText("穷游");
                }else if(menuItemId == R.id.music2){
                    contentTV.setText("蚂蜂窝");
                }else if(menuItemId == R.id.music3){
                    contentTV.setText("携程");
                }else if(menuItemId == R.id.music4){
                    contentTV.setText("去哪儿");
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {

            }

        });
        bottomBar.mapColorForTab(0, "#007853"); // 穷游
        bottomBar.mapColorForTab(1, "#FFCB10"); // 蚂蜂窝
        bottomBar.mapColorForTab(2, "#1F7AEC"); // 携程
        bottomBar.mapColorForTab(3, "#1BA9BA"); // 去哪儿

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }
}
