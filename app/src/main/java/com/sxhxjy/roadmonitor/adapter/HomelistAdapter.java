package com.sxhxjy.roadmonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhxjy.roadmonitor.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zm on 2016/10/31.
 */

public class HomelistAdapter extends BaseAdapter {
    private ArrayList<Map<String,Object>> list;//数据源
    private int resource;//自定义样式
    private LayoutInflater inflater;//布局填充器
    private Context context;
    public HomelistAdapter(Context context, ArrayList<Map<String,Object>> list, int resource){
        this.context=context;
        this.list=list;
        this.resource=resource;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    //获取集合大小
    public int getCount() {
        return list.size();
    }

    @Override
    //获取集合中某一个值，即列表项，listview中一行数据
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    //获取列表项的索引值
    public long getItemId(int position) {
        return position;
    }

    @Override
    //
    //1索引 2容器 3
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView=inflater.inflate(resource,null);
            vh.name=(TextView)convertView.findViewById(R.id.tv_home_item);
            vh.home_list_item= (LinearLayout) convertView.findViewById(R.id.home_list_item);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            vh.home_list_item.setMinimumWidth(width/3);
            convertView.setTag(vh);
        }else{
            vh=(ViewHolder)convertView.getTag();
        }
        Map<String,Object> map=list.get(position);//得到列表中某一项的对象
        vh.name.setText(map.get("name").toString()+"\n\n优");
        return convertView;
    }
    //內部类优化--定义属性
    class ViewHolder{
        TextView name;
        LinearLayout home_list_item;
    }
}
