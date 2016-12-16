package com.sxhxjy.defensemonitor.ui.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.BaseActivity;
import com.sxhxjy.defensemonitor.base.MonitorPosition;
import com.sxhxjy.defensemonitor.base.MyApplication;
import com.sxhxjy.defensemonitor.base.MySubscriber;
import com.sxhxjy.defensemonitor.entity.MonitorTypeTree;
import com.sxhxjy.defensemonitor.entity.SimpleItem;
import com.sxhxjy.defensemonitor.view.DeleteView;
import com.sxhxjy.defensemonitor.view.MyLinearLayout;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 2016/9/29
 *
 * @author Michael Zhao
 */

public class AddDataContrastActivity extends BaseActivity {

    private View addTimeMultiple;
    private View addTimeSingle;
    private LinearLayout timeContent;
    private String[] aLocation;
    private List<SimpleItem> mLocationList = new ArrayList<>();
    private List<SimpleItem> mTypeList = new ArrayList<>();
    private String[] aType;
    private ArrayList<SimpleItem> positionItems = new ArrayList<>();
//    private ArrayList<>
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private String title;
    private String startTime;
    private String endTime;
    private Random random = new Random();
    public int startDay; //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data_contrast_activity);
        initToolBar("数据对比", true);

        mToolbar.inflateMenu(R.menu.confirm_right);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (positionItems.isEmpty()) {
                    showToastMsg("请完善分析条件");
                    return false;
                } else if (positionItems.size() > 1) {
                    if (startTime == null || endTime == null)      {
                        showToastMsg("请完善分析条件");
                        return false;
                    }

                } else {
                    if (timeContent.getChildCount() == 0) {
                        showToastMsg("请完善分析条件");
                        return false;
                    }
                }

                Bundle b = new Bundle();

                b.putString("title", title);
                b.putSerializable("positionItems", positionItems);
                if (positionItems.size() > 1) {
                    b.putLong("start", simpleDateFormat.parse(startTime, new ParsePosition(0)).getTime());
                    b.putLong("end", simpleDateFormat.parse(endTime, new ParsePosition(0)).getTime());
                } else {
                    ArrayList<String> times = new ArrayList<String>();
                    for (int i = 0; i < timeContent.getChildCount(); i++) {
                        DeleteView myLinearLayout = (DeleteView) timeContent.getChildAt(i);
                        times.add(myLinearLayout.getContent());
                    }
                    b.putStringArrayList("times", times);

                }

                Intent data = new Intent();
                data.putExtras(b);
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
        });

        addTimeMultiple = findViewById(R.id.add_time_multiple);
        timeContent = (LinearLayout) findViewById(R.id.container);
        addTimeSingle = findViewById(R.id.add_time_single);
    }

    public void monitorType(final View view) {
        if (mTypeList.isEmpty()) {
            getMessage(getHttpService().getMonitorTypeTree(MyApplication.getMyApplication().getSharedPreference().getString("stationId","")), new MySubscriber<List<MonitorTypeTree>>() {
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
            for (SimpleItem item : mTypeList) {
                item.setChecked(false);
            }
            showDialogType(view);
        }
    }

    public void monitorLocation(final View view) {
        if (!mLocationList.isEmpty()) {
            showDialogPosition(view);
        } else {
            for (SimpleItem item :mTypeList) {
                if (item.isChecked()) {
                    getMessage(getHttpService().getPositions(item.getId(), MyApplication.getMyApplication().getSharedPreference().getString("stationId", "")), new MySubscriber<List<MonitorPosition>>() {
                        @Override
                        protected void onMyNext(List<MonitorPosition> monitorPositions) {
                            aLocation = new String[monitorPositions.size()];
                            int i = 0;
                            for (MonitorPosition position : monitorPositions) {
                                SimpleItem simpleItem = new SimpleItem(position.getId(), position.getName(), false);
                                simpleItem.setCode(position.code);
                                simpleItem.setColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

                                mLocationList.add(simpleItem);
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
        new AlertDialog.Builder(AddDataContrastActivity.this).setTitle("选择监测因素").setSingleChoiceItems(aType, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyLinearLayout myLinearLayout = (MyLinearLayout) view;
                myLinearLayout.setContent(aType[which]);
                mTypeList.get(which).setChecked(true);
                title = mTypeList.get(which).getTitle();
                mLocationList.clear();
                dialog.dismiss();
            }
        }).create().show();
    }


    private void showDialogPosition(final View view) {
        final boolean[] aTypeChecked = new boolean[mLocationList.size()];

        new AlertDialog.Builder(AddDataContrastActivity.this).setTitle("选择监测点位置").setMultiChoiceItems(aLocation, aTypeChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                mLocationList.get(which).setChecked(isChecked);
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb = new StringBuilder();
                positionItems.clear();
                int checked = 0;
                for (int i = 0; i < aTypeChecked.length; i++) {
                    if (aTypeChecked[i]) {
                        checked++;
                        sb.append(aLocation[i]).append("  ");
                        positionItems.add(mLocationList.get(i));
                    }
                }
                MyLinearLayout myLinearLayout = (MyLinearLayout) view;
                myLinearLayout.setContent(sb.toString());
                if (checked > 1) {
                    addTimeSingle.setVisibility(View.VISIBLE);
                    addTimeMultiple.setVisibility(View.GONE);
                } else {
                    addTimeSingle.setVisibility(View.GONE);
                    addTimeMultiple.setVisibility(View.VISIBLE);
                }
            }
        }).create().show();
    }

    public void timeStart(View view) {
        chooseTime((MyLinearLayout) view);
    }

    public void timeEnd(View view) {
        chooseTime((MyLinearLayout) view);
    }

    public StringBuilder chooseTime(final MyLinearLayout myLinearLayout) {
        final StringBuilder sb = new StringBuilder();
        Date date = new Date(System.currentTimeMillis());

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                sb.append(year);
                sb.append("-");
                sb.append(monthOfYear + 1);
                sb.append("-");
                sb.append(dayOfMonth);
                sb.append("  ");
                new TimePickerDialog(AddDataContrastActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sb.append(hourOfDay < 10 ? "0" + hourOfDay : hourOfDay);
                        sb.append(":");
                        sb.append(minute < 10 ? "0" + minute : minute);
                        myLinearLayout.setContent(sb.toString());
                        if (myLinearLayout.getId() == R.id.start_time)
                            startTime = sb.toString();
                        else
                            endTime = sb.toString();
                    }
                }, 0, 0, true).show();
            }
        }, 2016, date.getMonth(), date.getDate()).show();

        return sb;
    }

    public void addTime(View view) {
        final StringBuilder sb = new StringBuilder();
        final Date date = new Date(System.currentTimeMillis());

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                sb.append(year);
                sb.append("-");
                sb.append(monthOfYear + 1);
                sb.append("-");
                sb.append(dayOfMonth);
                startDay = dayOfMonth;
                sb.append("  ");
                new TimePickerDialog(AddDataContrastActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sb.append(hourOfDay < 10 ? "0" + hourOfDay : hourOfDay);
                        sb.append(":");
                        sb.append(minute < 10 ? "0" + minute : minute);
                        sb.append("  ----  ");
                        new DatePickerDialog(AddDataContrastActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                sb.append(year);
                                sb.append("-");
                                sb.append(monthOfYear + 1);
                                if (dayOfMonth < startDay) {
                                    showToastMsg("不能小于开始时间，请重新选择！");
                                } else {
                                sb.append("-");
                                sb.append(dayOfMonth);
                                sb.append("  ");
                                new TimePickerDialog(AddDataContrastActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        sb.append(hourOfDay < 10 ? "0" + hourOfDay : hourOfDay);
                                        sb.append(":");
                                        sb.append(minute < 10 ? "0" + minute : minute);
                                        timeContent.addView(new DeleteView(AddDataContrastActivity.this, sb.toString(), timeContent));
                                    }
                                }, 0, 0, true).show();
                                }
                            }
                        }, 2016, date.getMonth(), date.getDate()).show();

                        showToastMsg("请选择结束时间");
                    }
                }, 0, 0, true).show();
            }
        }, 2016, date.getMonth(), date.getDate()).show();
        showToastMsg("请选择开始时间");
    }
}
