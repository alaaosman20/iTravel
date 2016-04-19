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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xmliu.timerdata.bean.HtmlBean;
import com.xmliu.timerdata.bean.ImageBean;
import com.xmliu.timerdata.bean.NoteBean;
import com.xmliu.timerdata.bean.RecyclerBean;
import com.xmliu.timerdata.bean.UserBean;
import com.xmliu.timerdata.recyclerview.BaseRecyclerViewAdapter;
import com.xmliu.timerdata.recyclerview.DividerItemDecoration;
import com.xmliu.timerdata.recyclerview.RecyclerHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 发送广播命令：
 * adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -c android.intent.categor.DEFAULT -n com.xmliu.timerdata/.MyBootReceiver
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private TextView timedownTipTV;
    private TextView mStatusTV;
    private TextView mCountdownTV;
    private Button mSendBtn;
    private Button mCancelBtn;
    private Handler mHandler;
    private List<HtmlBean> htmlList = new ArrayList<>();
    private MyAdapter myMyAdapter;
    private List<RecyclerBean> mListData = new ArrayList<>();

    private static final int LOAD_DATA_SUCCESS = 1001;
    private static final int PARSE_DATA_SUCCESS = 1002;
    private static String parseUrl = "http://www.riji001.com/travel.html";
    //    private static String netTimeUrl = "http://www.bjtime.cn";
    private static String netTimeUrl = "http://www.baidu.com";
    private static String ACTION = "com.xmliu.timerdata.alarm";

    private Timer timer;
    private TimerTask timerTask;
    static int hour = Constants.ONE_UPDATE_HOUR;
    static int minute = Constants.ONE_UPDATE_MINUTE;
    static int second = Constants.ONE_UPDATE_SECOND;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            System.out.println("handle!");

            if (hour == 0) {
                if (minute == 0) {
                    if (second == 0) {

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (timerTask != null) {
                            timerTask = null;
                        }
                        // 倒计时结束，此时应该重新发送更新数据请求
                        sendTimeDown();
                    } else {
                        second--;
                        if (second >= 10) {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                        } else {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":0" + second);
                        }
                    }
                } else {
                    if (second == 0) {
                        second = 59;
                        minute--;
                        if (minute >= 10) {
                            mCountdownTV.setText("0" + hour + ":" + minute + ":" + second);
                        } else {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                        }
                    } else {
                        second--;
                        if (second >= 10) {
                            if (minute >= 10) {
                                mCountdownTV.setText("0" + hour + ":" + minute + ":" + second);
                            } else {
                                mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                            }
                        } else {
                            if (minute >= 10) {
                                mCountdownTV.setText("0" + hour + ":" + minute + ":0" + second);
                            } else {
                                mCountdownTV.setText("0" + hour + ":0" + minute + ":0" + second);
                            }
                        }
                    }
                }

            } else {
                // 如果hour不等于0
                if (minute == 0) {
                    if (second == 0) {
                        hour--;
                        minute = 59;
                        second = 59;
                        mCountdownTV.setText("0" + hour + ":" + minute + ":" + second);
                    } else {
                        second--;
                        if (second >= 10) {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                        } else {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":0" + second);
                        }
                    }
                } else {
                    if (second == 0) {
                        second = 59;
                        minute--;
                        if (minute >= 10) {
                            mCountdownTV.setText("0" + hour + ":" + minute + ":" + second);
                        } else {
                            mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                        }
                    } else {
                        second--;
                        if (second >= 10) {
                            if (minute >= 10) {
                                mCountdownTV.setText("0" + hour + ":" + minute + ":" + second);
                            } else {
                                mCountdownTV.setText("0" + hour + ":0" + minute + ":" + second);
                            }
                        } else {
                            if (minute >= 10) {
                                mCountdownTV.setText("0" + hour + ":" + minute + ":0" + second);
                            } else {
                                mCountdownTV.setText("0" + hour + ":0" + minute + ":0" + second);
                            }
                        }
                    }
                }
            }


        }
    };

    private void sendTimeDown() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(alarmReceiver, filter);

        // 倒计时启动
        timerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        };
        // 初始化计时数据
        hour = Constants.ONE_UPDATE_HOUR;
        minute = Constants.ONE_UPDATE_MINUTE;
        second = Constants.ONE_UPDATE_SECOND;
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
        sendUpdateBroadcast();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LOAD_DATA_SUCCESS:
                        // list反转
                        Collections.reverse(mListData);
                        RecyclerBean bean = new RecyclerBean();
                        bean.setContent("" + msg.obj);
                        mListData.add(bean);
                        // list再反转，得到倒序的list
                        Collections.reverse(mListData);
                        myMyAdapter.notifyDataSetChanged();

//                        mTipsTV.setText("" + msg.obj);

                        break;
                    case PARSE_DATA_SUCCESS:
                        // 插入到后台前去掉省略号
                        addNoteOnBmob(htmlList.get(0).content.replace("...", ""), htmlList.get(0).imageUrl);
                        break;
                }

            }
        };

        Bmob.initialize(this, "07b752a64abdf34127887ada169d9709");

        mRecyclerView = (RecyclerView) findViewById(R.id.myfeed_list_recycleView);
        mStatusTV = (TextView) findViewById(R.id.main_status_tv);
        timedownTipTV = (TextView) findViewById(R.id.time_down);
        mCountdownTV = (TextView) findViewById(R.id.time_countdown);
        mSendBtn = (Button) findViewById(R.id.main_send_btn);
        mCancelBtn = (Button) findViewById(R.id.main_cancel_btn);

        String bootStr = this.getIntent().getStringExtra("boot");
        if (!TextUtils.isEmpty(bootStr)) {
            mCountdownTV.setText(bootStr);
        }
        initToolBar();

        // RecycleView初始化配置
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                MainActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);

        myMyAdapter = new MyAdapter(MainActivity.this, mListData);
        mRecyclerView.setAdapter(myMyAdapter);

//        Snackbar.make(toolbar, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        final int colorRed = ContextCompat.getColor(this,R.color.red);
        String tipStr = "每 " + Constants.ONE_UPDATE_HOUR + "时" + Constants.ONE_UPDATE_MINUTE + "分" + Constants.ONE_UPDATE_SECOND + "秒" + " 添加一条数据到后台";
        SpannableString ss = new SpannableString(tipStr);
        ss.setSpan(new ForegroundColorSpan(colorRed), 1, tipStr.indexOf(" ", 3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        timedownTipTV.setText(ss);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendBtn.setEnabled(false);
                mCancelBtn.setEnabled(true);
                String tipstr = "当前状态：广播已发送";
                SpannableString ss = new SpannableString(tipstr);
                ss.setSpan(new ForegroundColorSpan(colorRed), tipstr.indexOf("：")+1, tipstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mStatusTV.setText(ss);

                sendTimeDown();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如何让定时器暂停 timer.cancel();
                timer.cancel();
                mSendBtn.setEnabled(true);
                mCancelBtn.setEnabled(false);
                String tipstr = "当前状态：广播已取消";
                SpannableString ss = new SpannableString(tipstr);
                ss.setSpan(new ForegroundColorSpan(colorRed), tipstr.indexOf("：")+1, tipstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mStatusTV.setText(ss);
                cancelUpdateBroadcast();
            }
        });

    }
    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("首页");
//            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        }
        /*自定义的一些操作*/
        toolbar.setContentInsetsRelative(50, 0);
    }
    private class MyAdapter extends BaseRecyclerViewAdapter {

        public MyAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.recyclerview_main_item;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            final RecyclerBean item = (RecyclerBean) list.get(position);
            holder.getTextView(R.id.recyclerview_item_content).setText(item.getContent());
        }
    }

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getTourData();
                }
            }).start();
            Toast.makeText(context, "更新数据", Toast.LENGTH_LONG).show();

            // 设置全局定时器(闹钟) 指定时间后再发广播通知本广播接收器触发执行.
            // 这种方式很像JavaScript中的 setTimeout(xxx,60000)
            sendUpdateBroadcast();
        }
    };

    void cancelUpdateBroadcast() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent();
        i.setAction(ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        am.cancel(pendingIntent);
    }

    void sendUpdateBroadcast() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 指定时间后将产生广播,触发alarmReceiver的执行,这个方法才是真正的更新数据的操作主要代码
        Intent i = new Intent();
        i.setAction(ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + Constants.ONE_UPDATE_HOUR * 60 * 60 * 1000 + Constants.ONE_UPDATE_MINUTE * 60 * 1000 + Constants.ONE_UPDATE_SECOND * 1000, pendingIntent);
    }


    /**
     * 重开机后重新计算并设置闹铃时间，暂未使用
     */
    public static class BootReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "recevie boot completed ... ");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                //重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
                Toast.makeText(context, "重启了手机", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addNoteOnBmob(final String content, final String image) {
        final NoteBean noteBean = new NoteBean();

        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo("username", "123456789");
        query.findObjects(this, new FindListener<UserBean>() {
            @Override
            public void onSuccess(final List<UserBean> userObject) {
                Log.i("TAG", "查询到用户总数：" + userObject.size());
                // 存在此用户
                BmobQuery<NoteBean> queryNote = new BmobQuery<>();
                queryNote.addWhereEqualTo("content", content);
                queryNote.findObjects(MainActivity.this, new FindListener<NoteBean>() {

                    @Override
                    public void onSuccess(List<NoteBean> noteObject) {
                        Log.i("TAG", "查询到记录总数：" + noteObject.size());
                        // 如果在表中查不到关于这个image的数据，那么再执行数据插入操作
                        if (noteObject.size() == 0) {
                            Log.i("TAG", "表中还无该数据：" + content);
                            noteBean.setAuthor(userObject.get(0));
                            noteBean.setContent(content);
                            noteBean.setIsopen(true);
                            noteBean.setAgreeNum(0);
                            noteBean.setCommentNum(0);
                            final List<ImageBean> imageList = new ArrayList<>();
                            ImageBean imageBean = new ImageBean();
                            imageBean.setImage(image);
                            imageList.add(imageBean);
                            noteBean.setImageList(imageList);
                            noteBean.save(MainActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addListWithTime("数据添加成功，可刷新主App查看");
                                        }
                                    }).start();

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(MainActivity.this, "数据插入失败" + s, Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Log.i("TAG", "表中已有该数据");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    addListWithTime("数据添加失败，与上条数据相同");
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(MainActivity.this, "查询失败:" + msg, Toast.LENGTH_SHORT).show();
                    }

                });

            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(MainActivity.this, "查询用户失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 获取网络时间，显示在RecyclerView列表
     */
    private void addListWithTime(String typeStr) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8")); // 时区设置
        try {
            URL url = new URL(netTimeUrl);//取得资源对象
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间（时间戳） System.currentTimeMillis()
            Date date = new Date(ld);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String currTime = formatter.format(date);
            Message msg = Message.obtain();
            msg.obj = typeStr + "  " + currTime;
            msg.what = LOAD_DATA_SUCCESS;
            mHandler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get方式抓取指定网站数据
     */
    private void getTourData() {
        htmlList.clear();
        String videoString = getHttpStringWithGet(parseUrl);
        Log.i("TAG", "html code==>" + videoString);

        Pattern p = Pattern
                .compile("<li class=\"card card-bpic\">([\\w\\W]*?)<img src=\"(.*?)\" />([\\w\\W]*?)<a href=\"(.*?)\">(.*?)</a>([\\w\\W]*?)<p>(.*?)</p>");
        Matcher m = p.matcher(videoString);
        Log.i("TAG", "boolean==>" + m.find());
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            HtmlBean data = new HtmlBean();
            data.imageUrl = mr.group(2);
            data.title = mr.group(5);
            data.content = mr.group(7);
            htmlList.add(data);
        }
        mHandler.sendEmptyMessageDelayed(PARSE_DATA_SUCCESS, 1000);
        Log.i("TAG", "result==>" + htmlList.size());
    }

    public String getHttpStringWithGet(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line;
        BufferedReader buffer = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(50000);
            urlConn.connect();

            buffer = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", e.getMessage());
            JSONObject obj = new JSONObject();
            try {
                obj.put("success", "false");
                obj.put("reason", "无法连接到服务器");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return obj.toString();
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("应用提示")
                .content("确定退出" + getResources().getString(R.string.app_name) + "吗?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarmReceiver);
    }
}
