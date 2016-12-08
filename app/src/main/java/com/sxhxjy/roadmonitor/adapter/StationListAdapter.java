package com.sxhxjy.roadmonitor.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseFragment;
import com.sxhxjy.roadmonitor.entity.GroupTree;
import com.sxhxjy.roadmonitor.entity.SimpleItem;
import com.sxhxjy.roadmonitor.ui.main.MainActivity;
import com.sxhxjy.roadmonitor.util.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 2016/9/18
 *
 * @author Michael Zhao
 */
public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.ViewHolder> implements View.OnClickListener {

    private List<GroupTree> data;
    private BaseFragment mFragment;
    private List<SimpleItem> mList = new ArrayList<>();
    private List<GroupTree> currentGroup;

    public StationListAdapter(BaseFragment fragment) {
        mFragment = fragment;
    }

    public void inject(List<GroupTree> list) {
        this.data = list;
        if (data != null)
            for (GroupTree groupTree : data) {
                SimpleItem item = new SimpleItem();
                item.setTitle(groupTree.name);
                item.setId(groupTree.id);
                mList.add(item);
            }
        currentGroup = data;

        if (mFragment.getActivity().getCallingActivity().getShortClassName().equals(".ui.main.LoginActivity") && currentGroup != null && currentGroup.size() == 1 && currentGroup.get(0).childrenGroup == null) {
            Bundle b = new Bundle();
            b.putString("stationId", currentGroup.get(0).id);
            b.putString("stationName", currentGroup.get(0).name);
            ActivityUtil.startActivityForResult(mFragment.getActivity(), MainActivity.class, b, 111);
            mFragment.getActivity().finish();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.title.setText(mList.get(position).getTitle());
        holder.arrow.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        int p = (int) v.getTag();

        if (currentGroup.get(p).childrenGroup != null) {
            mList.clear();
            for (GroupTree childrenGroup : currentGroup.get(p).childrenGroup) {
                SimpleItem item = new SimpleItem();
                item.setTitle(childrenGroup.name);
                item.setId(childrenGroup.id);
                mList.add(item);
            }
            currentGroup = currentGroup.get(p).childrenGroup;
            notifyDataSetChanged();
        } else {

            if (mFragment.getActivity().getCallingActivity() != null && mFragment.getActivity().getCallingActivity().getShortClassName().equals(".ui.main.LoginActivity")) {
                Bundle b = new Bundle();
                b.putString("stationId", currentGroup.get(p).id);
                b.putString("stationName", currentGroup.get(p).name);
                ActivityUtil.startActivityForResult(mFragment.getActivity(), MainActivity.class, b, 111);
                mFragment.getActivity().finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("stationId", currentGroup.get(p).id);
                intent.putExtra("stationName", currentGroup.get(p).name);
                mFragment.getActivity().setResult(Activity.RESULT_OK, intent);
                mFragment.getActivity().finish();
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, date, status;
        ImageView arrow;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            arrow = (ImageView) itemView.findViewById(R.id.right_arrow);
            arrow.setColorFilter(itemView.getResources().getColor(R.color.default_color));
        }
    }
}
