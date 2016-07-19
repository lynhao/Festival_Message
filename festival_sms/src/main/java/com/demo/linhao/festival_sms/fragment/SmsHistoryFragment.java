package com.demo.linhao.festival_sms.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.linhao.festival_sms.R;
import com.demo.linhao.festival_sms.bean.SendedMsg;
import com.demo.linhao.festival_sms.db.SmsProvider;
import com.demo.linhao.festival_sms.view.FlowLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

//需要注意某些导入的包，都是v4下的

//直接继承ListFragment，免去编写布局文件
public class SmsHistoryFragment extends ListFragment {
    private static final int LOADER_ID =1;

    private LayoutInflater mInflater;
    private CursorAdapter mCursorAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean mSubtitleVisible;
    ListAdapter adapter;

    FlowLayout flContacts;
    TextView tvContent;
    TextView tvFes;
    TextView tvDate;

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle("asd");
            }
        }

        ListView listView = (ListView)v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.list_item_context, menu);
                    return true;
                }

                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                             adapter = getListAdapter();
//                            ArrayList list = (ArrayList) adapter;
                            for(int i = adapter.getCount()-1;i>=0;i--){
//                               addTag((String) adapter.getItem(i),flContacts);
//                                deleteTag(flContacts);
//                              System.out.println(adapter.getCount());
//                               deleteTag(flContacts.getId());
//                                adapter.getItem(i);
//                                list.remove(adapter.getItem(i));
                                mCursorAdapter.getCursor().move(i+1);

                            }
//                            CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
//                            CrimeLab crimeLab = CrimeLab.get(getActivity());
//                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
//                                if (getListView().isItemChecked(i)) {
//                                    crimeLab.deleteCrime(adapter.getItem(i));
//                                }
//                            }
//                            mode.finish();
//                            adapter.notifyDataSetChanged();

                            return true;
                        default:
                            return false;
                    }
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {

                }
            });

        }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInflater=LayoutInflater.from(getActivity());


        initLoader();

        setupListAdapter();

    }



    private void setupListAdapter() {
        Log.d("测试", "setupListAdapter-1");
        mCursorAdapter=new CursorAdapter(getActivity(),null,false) {

            //并不是每次都被调用的，它只在实例化view的时候调用,数据增加的时候也会调用
            //但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view=mInflater.inflate(R.layout.item_sended_msg,parent,false);//注意是"包名.R"
                return view;
            }

            //在绘制Item之前一定会调用bindView方法，它在重绘的时候也同样被调用
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                tvContent= (TextView) view.findViewById(R.id.id_tv_sended_content);
                flContacts= (FlowLayout) view.findViewById(R.id.id_fl_sended_contacts);
                tvFes= (TextView) view.findViewById(R.id.id_tv_fes);
                tvDate= (TextView) view.findViewById(R.id.id_tv_date);

                tvContent.setText(cursor.getString(cursor.getColumnIndex(SendedMsg.COLUMN_CONTENT)));
                tvFes.setText(cursor.getString(cursor.getColumnIndex(SendedMsg.COLUMN_FESTIVAL_NAME)));

                //注意这里的date为long，int型会溢出
                long date=cursor.getLong(cursor.getColumnIndex(SendedMsg.COLUMN_DATE));
                tvDate.setText(parseDate(date));

                String names=cursor.getString(cursor.getColumnIndex(SendedMsg.COLUMN_NAMES));
                if(TextUtils.isEmpty(names)) {
                    return;
                }

                //因为ListView的item有复用的可能性，所以每次都要先除去item中的flContacts在上一次使用时添加的view
                flContacts.removeAllViews();

                for (String name:names.split(",")) {
                    addTag(name, flContacts);
                }
            }
        };

        setListAdapter(mCursorAdapter);
        Log.d("测试", "setupListAdapter-2");
    }

    private String parseDate(long date) {
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(date);
    }

    private void addTag(String name,FlowLayout fl) {
        TextView tv= (TextView) mInflater.inflate(R.layout.tag,fl,false);
        tv.setText(name);
        fl.addView(tv);
    }
    public void deleteTag(FlowLayout fl) {

        fl.addView(null);
    }

    private void initLoader() {
        getLoaderManager().initLoader(LOADER_ID,null,new LoaderManager.LoaderCallbacks<Cursor>() {

            //onCreateLoader是一个工厂方法，用来返回一个新的Loader
            //LoaderManager将会在它第一次创建Loader的时候调用该方法
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.d("测试","onCreateLoader");
                CursorLoader loader=new CursorLoader(getActivity(), SmsProvider.URI_SMS_ALL,null,null,null,null);
                return loader;
            }

            //onLoadFinished方法将在Loader创建完毕的时候自动调用
            //在数据更新的时候也会调用
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d("测试","onLoadFinished");
                if(loader.getId()==LOADER_ID) {
                    mCursorAdapter.swapCursor(data);//更新mCursorAdapter的Cursor
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mCursorAdapter.swapCursor(null);
            }
        });
    }
}
