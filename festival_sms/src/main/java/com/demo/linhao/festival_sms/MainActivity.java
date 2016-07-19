package com.demo.linhao.festival_sms;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.demo.linhao.festival_sms.fragment.FestivalCategoryFragment;
import com.demo.linhao.festival_sms.fragment.SmsHistoryFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MsgService msgService ;


    private SwipeRefreshLayout swipeRefreshLayout;


    private String[] mTitles=new String[]{"节日短信","发送记录"};

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          msgService = ((MsgService.MsgBinder) service).getMsg();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("stop","onCreate");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.statusbar_bg);
//
//        }

        relative();


        initViews();

    }


    private void relative() {
        Log.d("tag", "绑定成功了");

            Intent intent = new Intent("com.demo.linhao.festival_sms.MainActivity");
            bindService(intent, connection, BIND_AUTO_CREATE);

    }

    private void initViews() {
        mTabLayout= (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager= (ViewPager) findViewById(R.id.id_viewpager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
//        下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_purple,
                android.R.color.holo_red_dark);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                //在ViewPager与Tab的联系后，会多次(具体次数等于getCount)调用getItem
                if (position==0) {
                    return new FestivalCategoryFragment();
                }
                else return new SmsHistoryFragment();
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);//建立ViewPager与Tab的联系
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            exitByTwo();//双击退出
        }
        return false;
    }



//    @Override
//    public void () {
//        super.onBackPressed();
//        Intent i = new Intent(MainActivity.this,MainActivity.class);
//        startActivity(i);
//    }

    private static boolean isExit = false;
    private void exitByTwo() {
        Timer timer = null;
        if (!isExit){
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        }else {
            finish();
            System.exit(-1);
        }
    }
    //刷新
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        this.finish();
        Log.d("stop","stop"+"");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        this.finish();
        Log.d("stop", "onRestart" + "");
    }
}
