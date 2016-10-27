package com.sxhxjy.roadmonitor.ui.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseActivity;
import com.sxhxjy.roadmonitor.base.MonitorPosition;
import com.sxhxjy.roadmonitor.base.MyApplication;
import com.sxhxjy.roadmonitor.base.MySubscriber;
import com.sxhxjy.roadmonitor.entity.MonitorTypeTree;
import com.sxhxjy.roadmonitor.entity.SimpleItem;
import com.sxhxjy.roadmonitor.view.MyLinearLayout;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 2016/9/29
 *
 * @author Michael Zhao
 */

public class AddDataCorrelationActivity extends BaseActivity {
    private String[] aLocation;
    private List<SimpleItem> mLocationList = new ArrayList<>();
    private List<SimpleItem> mTypeList = new ArrayList<>();
    private String[] aType;
    private ArrayList<SimpleItem> positionItems = new ArrayList<>();
    private ArrayList<SimpleItem> positionItemsCorrelation = new ArrayList<>();
    //    private ArrayList<>
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private String title, titleCorrelation;
    private String startTime, endTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data_correlation_activity);
        initToolBar("数据关联", true);

        mToolbar.inflateMenu(R.menu.confirm_right);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle b = new Bundle();
                b.putSerializable("positionItems", positionItems);
                b.putSerializable("positionItemsCorrelation", positionItemsCorrelation);
                b.putString("title", title);
                b.putString("titleCorrelation", titleCorrelation);
                b.putLong("start", simpleDateFormat.parse(startTime, new ParsePosition(0)).getTime());
                b.putLong("end", simpleDateFormat.parse(endTime, new ParsePosition(0)).getTime());

                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
        });


    }

    public void monitorType(final View view) {
        if (mTypeList.isEmpty()) {
            getMessage(getHttpService().getMonitorTypeTree(), new MySubscriber<List<MonitorTypeTree>>() {
                @Override
                protected void onMyNext(List<MonitorTypeTree> monitorTypeTrees) {
                    for (MonitorTypeTree monitorTypeTree : monitorTypeTrees) {
                        if (monitorTypeTree.getChildrenPoint() != null) {
                            for (MonitorTypeTree.ChildrenPointBean childrenPointBean : monitorTypeTree.getChildrenPoint()) {
                                mTypeList.add(new SimpleItem(childrenPointBean.getId(), childrenPointBean.getName(), false));
                            }
                        }

                    }

                    aType = new String[mTypeList.size()];
                    for (int i = 0; i < mTypeList.size(); i++) {
                        aType[i] = mTypeList.get(i).getTitle();
                    }

                    showDialogType(view);
                }
            });
        } else {
            showDialogType(view);
        }
    }

    public void monitorLocation(final View view) {
        if (!mLocationList.isEmpty()) {
            showDialogPosition(view);
        } else {
            for (SimpleItem item :mTypeList) {
                if (item.isChecked()) {
                    getMessage(getHttpService().getPositions(item.getId(), MyApplication.getMyApplication().getSharedPreference().getString("gid", "")), new MySubscriber<List<MonitorPosition>>() {
                        @Override
                        protected void onMyNext(List<MonitorPosition> monitorPositions) {
                            aLocation = new String[monitorPositions.size()];
                            int i = 0;
                            for (MonitorPosition position : monitorPositions) {
                                mLocationList.add(new SimpleItem(position.getId(), position.getName(), false));
                                aLocation[i++] = position.getName();
                            }
                            showDialogPosition(view);
                        }
                    });
                }
            }
        }
    }

    private void showDialogType(final View view) {
        new AlertDialog.Builder(AddDataCorrelationActivity.this).setTitle("选择监测因素").setSingleChoiceItems(aType, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyLinearLayout myLinearLayout = (MyLinearLayout) view;
                myLinearLayout.setContent(aType[which]);
                mTypeList.get(which).setChecked(true);
                if (view.getId() == R.id.correlation_type) {
                    titleCorrelation = mTypeList.get(which).getTitle();
                } else {
                    title = mTypeList.get(which).getTitle();
                }
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showDialogPosition(final View view) {
        final boolean[] aTypeChecked = new boolean[]{true, false, false};

        new AlertDialog.Builder(AddDataCorrelationActivity.this).setTitle("选择监测点位置").setMultiChoiceItems(aLocation, aTypeChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                mLocationList.get(which).setChecked(isChecked);
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < aTypeChecked.length; i++) {
                    if (aTypeChecked[i]) {
                        sb.append(aLocation[i]).append("  ");
                        if (view.getId() == R.id.correlation_position)
                            positionItemsCorrelation.add(mLocationList.get(i));
                        else
                            positionItems.add(mLocationList.get(i));
                    }
                }
                MyLinearLayout myLinearLayout = (MyLinearLayout) view;
                myLinearLayout.setContent(sb.toString());
            }
        }).create().show();
    }

    public void timeStart(View view) {
        chooseTime((MyLinearLayout) view);
    }

    public void timeEnd(View view) {
        chooseTime((MyLinearLayout) view);
    }

    public void chooseTime(final MyLinearLayout myLinearLayout) {
        final StringBuilder sb = new StringBuilder();

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                sb.append(year);
                sb.append("-");
                sb.append(monthOfYear + 1);
                sb.append("-");
                sb.append(dayOfMonth);
                sb.append("  ");
                new TimePickerDialog(AddDataCorrelationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sb.append(hourOfDay < 10 ? "0" + hourOfDay : hourOfDay);
                        sb.append(":");
                        sb.append(minute < 10 ? "0" + minute : minute);
                        myLinearLayout.setContent(sb.toString());
                    }
                }, 0, 0, true).show();
            }
        }, 2016, 0, 1).show();
        if (myLinearLayout.getId() == R.id.start_time)
            startTime = sb.toString();
        else
            endTime = sb.toString();
    }
}
