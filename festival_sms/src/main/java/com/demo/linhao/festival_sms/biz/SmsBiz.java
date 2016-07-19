package com.demo.linhao.festival_sms.biz;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.SmsManager;

import com.demo.linhao.festival_sms.bean.SendedMsg;
import com.demo.linhao.festival_sms.db.SmsProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * 与短信有关的业务类
 */
public class SmsBiz {

    private Context context;

    public SmsBiz(Context context) {
        this.context=context;
    }

    /**
     *
     * @param number
     * @param msgContent
     * @param sentPi
     * @param deliverPi
     * @return 返回短信的条数（有可能短信内容比较长，要做分割）
     **/
    public int sendMsg(String number, String msgContent, PendingIntent sentPi, PendingIntent deliverPi) {
        SmsManager smsManager=SmsManager.getDefault();
        ArrayList<String> contents=smsManager.divideMessage(msgContent);

        //分割后单条短信的发送
        for(String content:contents) {
            smsManager.sendTextMessage(number,null,content,sentPi,deliverPi);
        }

        return contents.size();
    }

    //有可能需要发送的联系人不止一个
    public int sendMsg(Set<String> numbers, SendedMsg msg, PendingIntent sentPi, PendingIntent deliverPi) {
        save(msg);
        int result=0;
        for(String number:numbers) {
            int count=sendMsg(number,msg.getContent(),sentPi,deliverPi);
            result+=count;
        }

        return result;
    }

    //因为这里只涉及数据库的增和查，所以这里直接写在业务层下
    //但是如果涉及到的数据库的操作比较多（增删改查，以及按条件查询等），最好在项目的包下建立一个"dao"层
    //或者在"db"下增加一个相关操作的类，然后业务层就可以根据其实例去访问数据库的一些内容
    private void save(SendedMsg sendedMsg) {
        sendedMsg.setDate(new Date());
        ContentValues values=new ContentValues();
        values.put(SendedMsg.COLUMN_DATE,sendedMsg.getDate().getTime());
        values.put(SendedMsg.COLUMN_FESTIVAL_NAME,sendedMsg.getFestivalName());
        values.put(SendedMsg.COLUMN_CONTENT,sendedMsg.getContent());
        values.put(SendedMsg.COLUMN_NAMES,sendedMsg.getNames());
        values.put(SendedMsg.COLUMN_NUMBERS,sendedMsg.getNumbers());

        context.getContentResolver().insert(SmsProvider.URI_SMS_ALL,values);
    }
}