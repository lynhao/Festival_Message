package com.demo.linhao.festival_sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.linhao.festival_sms.bean.Festival;
import com.demo.linhao.festival_sms.bean.FestivalLab;
import com.demo.linhao.festival_sms.bean.Msg;
import com.demo.linhao.festival_sms.bean.SendedMsg;
import com.demo.linhao.festival_sms.biz.SmsBiz;
import com.demo.linhao.festival_sms.view.FlowLayout;

import java.util.HashSet;
import java.util.Set;

public class SendMsgActivity extends AppCompatActivity {
    public static final String KEY_ID_FESTIVAL="FestivalId";
    public static final String KEY_ID_MSG="MsgId";

    private static final int CODE_REQUEST=1;

    private int mFestivalId;
    private int mMsgId;

    private Festival mFestival;
    private Msg mMsg;

    private EditText mEdMsg;
    private Button mBtnAdd;
    private FlowLayout mFlContacts;
    private FloatingActionButton mFabSend;//用于发送短信的FloatingActionButton
    private View mLayoutLoading;

    private Set<String> mContactNames=new HashSet<>();
    private Set<String> mContactNums=new HashSet<>();

    private LayoutInflater mInflater;

    private static final String ACTION_SEND_MSG="ACTION_SEND_MSG";
    private static final String ACTION_DELIVER_MSG="ACTION_DELIVER_MSG";

    private PendingIntent mSendPi;
    private PendingIntent mDeliverPi;
    private BroadcastReceiver mSendBroadcastReceiver;
    private BroadcastReceiver mDeliverBroadcastReceiver;

    private SmsBiz mSmsBiz;

    private int mMsgSendCount;//用来记录发送次数
    private int mTotalCount;

    private MsgService send_msgService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        mInflater=LayoutInflater.from(this);

        mSmsBiz=new SmsBiz(this);

        initDatas();

        initViews();

        initEvents();
        
        initRecivers();


    }

    private void initRecivers() {
        Intent sendIntent=new Intent(ACTION_SEND_MSG);
        mSendPi=PendingIntent.getBroadcast(this,0,sendIntent,0);
        Intent deliverIntent=new Intent(ACTION_DELIVER_MSG);
        mDeliverPi=PendingIntent.getBroadcast(this,0,deliverIntent,0);

        registerReceiver(mSendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //因为是每个分割的短信发送后都会发送一个广播，所以这里每次在接受广播后都要累加（不管发送成功与否），直至等于分割的短信的总数
                mMsgSendCount++;

                if (getResultCode() == RESULT_OK) {
                    Log.d("测试", "短信发送成功" + (mMsgSendCount + "/" + mTotalCount));
                } else {
                    Log.d("测试", "短信发送失败");
                }
                Toast.makeText(SendMsgActivity.this, (mMsgSendCount + "/" + mTotalCount) + "短信已经发送", Toast.LENGTH_SHORT).show();
                if (mMsgSendCount == mTotalCount) {
                    finish();
                }
            }
        }, new IntentFilter(ACTION_SEND_MSG));

        registerReceiver(mDeliverBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("测试", "联系人已经成功接收短信");
            }
        }, new IntentFilter(ACTION_DELIVER_MSG));
    }

    private void initEvents() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_REQUEST);
            }
        });

        mFabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContactNums.size() == 0) {
                    Toast.makeText(SendMsgActivity.this, "请先添加联系人！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String msgContent = mEdMsg.getText().toString();
                if (TextUtils.isEmpty(msgContent)) {
                    Toast.makeText(SendMsgActivity.this, "短信内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLayoutLoading.setVisibility(View.VISIBLE);
                mMsgSendCount = 0;
                mTotalCount = mSmsBiz.sendMsg(mContactNums, buildSendedMsg(msgContent), mSendPi, mDeliverPi);
            }
        });
    }

    private SendedMsg buildSendedMsg(String msgContent) {
        SendedMsg sendedMsg=new SendedMsg();
        sendedMsg.setContent(msgContent);
        sendedMsg.setFestivalName(mFestival.getName());
        StringBuilder names=new StringBuilder();
        for (String name:mContactNames) {
            names.append(name+",");
        }
        sendedMsg.setNames(names.substring(0,names.length()-1));
        StringBuilder numbers=new StringBuilder();
        for (String number:mContactNums) {
            numbers.append(number+",");
        }
        sendedMsg.setNumbers(numbers.substring(0, numbers.length() - 1));
        return sendedMsg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CODE_REQUEST) {
            if(resultCode==RESULT_OK) {
                Uri contactUri=data.getData();//这里得到的Uri是选定的联系人的特定Uri
                Cursor cursor=getContentResolver().query(contactUri,null,null,null,null);
                if (cursor!=null) {
                    cursor.moveToFirst();
                    String contactName=cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    String number=getContactNumber(cursor);

                    cursor.close();

                    //如果number不存在，就不需要添加contactName与number
                    if(!TextUtils.isEmpty(number)) {
                        mContactNums.add(number);
                        mContactNames.add(contactName);

                        addTag(contactName);
                    }
                }
            }
        }
    }

    /**
     * 将选定的联系人添加进FlowLayout中
     * @param contactName
     */
    private void addTag(String contactName) {
        TextView textView= (TextView) mInflater.inflate(R.layout.tag,mFlContacts,false);
        textView.setText(contactName);
        mFlContacts.addView(textView);
    }

    private String getContactNumber(Cursor cursor) {
        int numberCount=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number=null;
        if(numberCount>0) {
            int contactId=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            Cursor phoneCursor=getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+contactId,null,null);
            phoneCursor.moveToFirst();

            //可能phoneCursor中会存在多个号码(因为可能同一个联系人下存有多种号码，如手机、单位等),这里只简单的获取第一个
            number=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneCursor.close();
        }
        return number;
    }

    private void initViews() {
        mEdMsg= (EditText) findViewById(R.id.id_et_content);
        mBtnAdd= (Button) findViewById(R.id.id_btn_add);
        mFlContacts= (FlowLayout) findViewById(R.id.id_fl_contacts);
        mFabSend= (FloatingActionButton) findViewById(R.id.id_fab_send);
        mLayoutLoading=findViewById(R.id.id_layout_loading);

        mLayoutLoading.setVisibility(View.GONE);

        if(mMsgId!=-1) {
            mMsg= FestivalLab.getInstance().getMsgByFestivalIdAndMsgId(mFestivalId, mMsgId);
            mEdMsg.setText(mMsg.getContent());
        }
    }

    private void initDatas() {
        mFestivalId=getIntent().getIntExtra(KEY_ID_FESTIVAL,-1);
        mMsgId=getIntent().getIntExtra(KEY_ID_MSG,-1);

        mFestival=FestivalLab.getInstance().getFestivalById(mFestivalId);
        setTitle(mFestival.getName());
    }

    public static void toActivity(Context context, int festivalId, int msgId) {
        Intent intent=new Intent(context,SendMsgActivity.class);
        intent.putExtra(KEY_ID_FESTIVAL,festivalId);
        intent.putExtra(KEY_ID_MSG,msgId);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mSendBroadcastReceiver);
        unregisterReceiver(mDeliverBroadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("act","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("act","onStop");

    }
}
