package com.sxhxjy.roadmonitor.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseFragment;
import com.sxhxjy.roadmonitor.base.MySubscriber;
import com.sxhxjy.roadmonitor.base.UserManager;
import com.sxhxjy.roadmonitor.entity.AlertData;
import com.sxhxjy.roadmonitor.ui.main.AlertDetailActivity;
import com.sxhxjy.roadmonitor.ui.main.AlertFragment;
import com.sxhxjy.roadmonitor.util.ActivityUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 2016/9/26
 *
 * @author Michael Zhao
 */
public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.ViewHolder> implements View.OnClickListener {
    /**
     *
     */
    private List<AlertData> mList;
    private BaseFragment mFragment;
    private AlertDialog alertDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    public AlertListAdapter(BaseFragment fragment, ArrayList<AlertData> list) {
        mFragment = fragment;
        mList = list;
    }
    @Override
    public AlertListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlertListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(AlertListAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.num.setText(mList.get(position).getNum() + "次");

        if (mList.get(position).getConfirmInfo() == null) {
            holder.isConfirmed.setText("未确认");
            holder.isConfirmed.setBackgroundDrawable(mFragment.getResources().getDrawable(R.drawable.round_rectangle_red_stroke));
        } else {
            holder.isConfirmed.setText("已确认");
            holder.isConfirmed.setBackgroundDrawable(mFragment.getResources().getDrawable(R.drawable.round_rectangle_keycolor_stroke));
        }
        holder.level.setText(mList.get(position).getLevel());
        holder.desc.setText(mList.get(position).getType());
        holder.location.setText(mList.get(position).getAlarmContent());
        holder.reason.setText(mList.get(position).getGenerationReason());
        holder.date.setText(sdf.format(new Date(Long.parseLong(mList.get(position).getStime()))) + "--" + sdf.format(new Date(Long.parseLong(mList.get(position).getEtime()))));
        holder.code.setText(mList.get(position).getStationName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder=new ViewHolder(v);
        String isCon= holder.isConfirmed.getText().toString();
        final int p = (int) v.getTag();
        if (isCon.equals("已确认")){
            alertDialog = new AlertDialog.Builder(mFragment.getActivity()).setTitle("确定警告信息").
                    setView(mFragment.getActivity().getLayoutInflater().
                            inflate(R.layout.dialog_view_alerts, null)).
                    setNegativeButton("取消", null).setNeutralButton("查看详情", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle b = new Bundle();
                    b.putSerializable("data", mList.get(p));
                    ActivityUtil.startActivityForResult(mFragment.getActivity(), AlertDetailActivity.class, b, 100);
                }
            }).show();
            String info=mList.get(p).getConfirmInfo();
            String arrs[]=info.split(";");//拆分字符串为数组
            ((TextView) alertDialog.findViewById(R.id.tv_Info)).setText(arrs[0]);
            ((TextView) alertDialog.findViewById(R.id.tv_content)).setText(arrs[1]);
            ((TextView) alertDialog.findViewById(R.id.tv_time)).setText(arrs[2]);
        }else{
            alertDialog = new AlertDialog.Builder(mFragment.getActivity()).setTitle("确定警告信息").
                    setView(mFragment.getActivity().getLayoutInflater().
                            inflate(R.layout.dialog_view_alert, null)).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFragment.getMessage(mFragment.getHttpService().confirmAlertMsg(mList.get(p).getId(), UserManager.getUID(), ((EditText) alertDialog.findViewById(R.id.editText)).getText().toString(), mList.get(p).getStime()+"", mList.get(p).getEtime()+"", mList.get(p).getLevelId(), mList.get(p).getTypeId(), mList.get(p).getGenerationReason(), mList.get(p).getAlarmContent()), new MySubscriber<Object>() {
                        @Override
                        public void onNext(Object o) {
                            mFragment.showToastMsg("确定警告信息成功！");
                            ((AlertFragment) mFragment).onRefresh();
                        }

                        @Override
                        protected void onMyNext(Object o) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }

                    });

                }
            }).setNegativeButton("取消", null).setNeutralButton("查看详情", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle b = new Bundle();
                    b.putString("start", mList.get(p).getStime()+"");
                    b.putString("end", mList.get(p).getEtime()+"");
                    b.putString("id", mList.get(p).getId());
                    ActivityUtil.startActivityForResult(mFragment.getActivity(), AlertDetailActivity.class, b, 100);
                }
            }).create();
            alertDialog.show();
            ((EditText) alertDialog.findViewById(R.id.editText)).setText(mList.get(p).getConfirmInfo());
        }


    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reason, location, date, status, num, isConfirmed, level, desc, code;
        ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
//            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            location = (TextView) itemView.findViewById(R.id.location);
            date = (TextView) itemView.findViewById(R.id.date);
            reason = (TextView) itemView.findViewById(R.id.reason);
            num = (TextView) itemView.findViewById(R.id.num);
            isConfirmed = (TextView) itemView.findViewById(R.id.is_confirmed);
            level = (TextView) itemView.findViewById(R.id.level);
            desc = (TextView) itemView.findViewById(R.id.desc);
            code = (TextView) itemView.findViewById(R.id.code);
        }
    }
}
