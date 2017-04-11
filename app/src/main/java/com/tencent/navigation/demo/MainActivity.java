package com.tencent.navigation.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 导航sdk使用demo的主页面
 *
 * @author selenali
 */
public class MainActivity extends ListActivity {

    /**
     * 导航sdk使用demo的包路径
     */
    private static final String demoPackage = "com.tencent.navigation.demo.";

    /**
     * 导航sdk使用demo的功能list
     */
    private String[] demoClassNames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化页面，list列表上显示demo功能list
        demoClassNames = this.getResources().getStringArray(
                R.array.demo_activitys);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, demoClassNames));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // 点击功能list，进入功能demo页面
        String strGotName = demoClassNames[position];
        String strClassName = demoPackage + strGotName;
        try {
            Class clazz = Class.forName(strClassName);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
