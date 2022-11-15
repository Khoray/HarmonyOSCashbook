package com.khoray.cashbook.model;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.provider.PageProvider;
import com.khoray.cashbook.utils.DebugUtil;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.app.Context;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class DialogHelper {

    public static interface PickTimeCallBack {
        void pickTimeCallBack(int year, int month, int day, int hour, int minute);
    }
    public static interface PickTypeCallBack {
        void pickTypeCallBack(int majorType, int minorType);
    }

    public static interface RecordCallBack {
        void recordCallBack(RecordBean record);
        void recordDelCallBack(RecordBean record);
    }



    public static void addandeditRecord(Context context, RecordCallBack recordCallBack, String title, boolean canDelete, RecordBean record) {
        RecordBean newRecord = new RecordBean(record);

        CommonDialog cd = new CommonDialog(context);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_addandedit_dialog, null, false);
        TextField valueTf = (TextField) dl.findComponentById(ResourceTable.Id_money_textfield);
        TextField noteTf = (TextField) dl.findComponentById(ResourceTable.Id_note_textfield);
        ((Text) dl.findComponentById(ResourceTable.Id_addandedit_title)).setText(title);
        if(canDelete) {
            DirectionalLayout mainDl = (DirectionalLayout) dl.findComponentById(ResourceTable.Id_addandedit_main_layout);
            Button delBtn = new Button(context);
            delBtn.setClickedListener((Component component) -> {
                recordCallBack.recordDelCallBack(record);
                cd.destroy();
            });
            delBtn.setText("删除");
            delBtn.setTextColor(Color.RED);
            delBtn.setTextSize(AttrHelper.vp2px(17, context));
            delBtn.setWidth(200);
            delBtn.setHeight(MATCH_CONTENT);
            mainDl.addComponent(delBtn);
        }

        Button cancelBtn = (Button) dl.findComponentById(ResourceTable.Id_andandedit_cancel_btn);
        Button saveBtn = (Button) dl.findComponentById(ResourceTable.Id_addandedit_save_btn);
        Button timeBtn = (Button) dl.findComponentById(ResourceTable.Id_addandedit_time_btn);
        Button typeBtn = (Button) dl.findComponentById(ResourceTable.Id_addandedit_type_btn);
        // 初始化record
        if(record.getTime() != -1) {
            valueTf.setText(Double.toString(record.getValue()));
            timeBtn.setText(TimeUtil.formatYMDHM(record.getTime()));
            if(record.getMajorType() == 0) {
                typeBtn.setText("支出->" + Const.payType[record.getMinorType()]);
            } else {
                typeBtn.setText("收入->" + Const.incomeType[record.getMinorType()]);
            }
            noteTf.setText(record.getNote());

        }

        timeBtn.setClickedListener((Component component) -> {
            pickTime(context, new PickTimeCallBack() {
                @Override
                public void pickTimeCallBack(int year, int month, int day, int hour, int minute) {
                    timeBtn.setText(TimeUtil.formatYMDHM(year, month, day, hour, minute));
                    newRecord.setTime(TimeUtil.YMDHMtoTime(year, month, day, hour, minute));
                }
            });
        });

        typeBtn.setClickedListener((Component component) -> {
            pickType(context, new PickTypeCallBack() {
                @Override
                public void pickTypeCallBack(int majorType, int minorType) {
                    if(record.getMajorType() == 0) {
                        typeBtn.setText("支出->" + Const.payType[record.getMinorType()]);
                    } else {
                        typeBtn.setText("收入->" + Const.incomeType[record.getMinorType()]);
                    }

                    newRecord.setMajorType(majorType);
                    newRecord.setMinorType(minorType);
                }
            });
        });

        cancelBtn.setClickedListener((Component component) -> {
            cd.destroy();
        });


        saveBtn.setClickedListener((Component component) -> {
            // 设置新的record并调用callback
            try {
                newRecord.setValue(Double.parseDouble(valueTf.getText()));
            } catch (Exception e) {
                e.printStackTrace();
                DebugUtil.showToast(context, "金额输入有误");
                return;
            }

            newRecord.setNote(noteTf.getText());
            recordCallBack.recordCallBack(newRecord);
            DebugUtil.showToast(context, newRecord.toString());
            cd.destroy();

        });

        cd.setSize(600, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
    }

    public static void pickFilter(Context context, FilterDialog.FilterCallBack fcb, FilterBean fb) {
        CommonDialog cd = new CommonDialog(context);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_filter_dialog, null, false);
        Image moreImg = (Image) dl.findComponentById(ResourceTable.Id_more_filter_btn);
        Image img365 = (Image) dl.findComponentById(ResourceTable.Id_yearrecord_btn);
        Image img30 = (Image) dl.findComponentById(ResourceTable.Id_monthrecord_btn);
        Image img1 = (Image) dl.findComponentById(ResourceTable.Id_dayrecord_btn);
        moreImg.setClickedListener((Component c) -> {
            new FilterDialog(context, fcb, fb);
            cd.destroy();
        });
        img365.setClickedListener((Component c) -> {
            fcb.filterCallBack(FilterBean.getYearFilter());
            cd.destroy();
        });
        img30.setClickedListener((Component c) -> {
            DebugUtil.showToast(context, FilterBean.getMonthFilter().toString());
            fcb.filterCallBack(FilterBean.getMonthFilter());
            cd.destroy();
        });
        img1.setClickedListener((Component c) -> {

            fcb.filterCallBack(FilterBean.getDayFilter());
            cd.destroy();
        });

        cd.setSize(MATCH_PARENT, MATCH_CONTENT);
        cd.setAlignment(LayoutAlignment.BOTTOM);
        cd.setContentCustomComponent(dl);
        cd.show();
    }

    public static void pickDate(Context context, PickTimeCallBack confirmPickTime) {
        CommonDialog cd = new CommonDialog(context);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_date_pick_dialog, null, false);
        Button confirmBtn = (Button) dl.findComponentById(ResourceTable.Id_date_pick_confirm);
        DatePicker tp = (DatePicker) dl.findComponentById(ResourceTable.Id_date_pick);
        // 确认按钮
        confirmBtn.setClickedListener((Component component) -> {
            int year = tp.getYear();
            int month = tp.getMonth();
            int day = tp.getDayOfMonth();
            confirmPickTime.pickTimeCallBack(year, month, day, -1, -1);
            cd.destroy();
        });
        // 取消按钮
        Button cancelBtn = (Button) dl.findComponentById(ResourceTable.Id_date_pick_cancel);
        cancelBtn.setClickedListener((Component component) -> {
            cd.destroy();
        });
        cd.setSize(600, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
    }

    public static void pickTime(Context context, PickTimeCallBack confirmPickTime) {
        CommonDialog cd = new CommonDialog(context);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_time_pick_dialog, null, false);
        Button confirmBtn = (Button) dl.findComponentById(ResourceTable.Id_time_pick_confirm);
        DatePicker tp = (DatePicker) dl.findComponentById(ResourceTable.Id_time_date_pick);
        TimePicker pt = (TimePicker) dl.findComponentById(ResourceTable.Id_time_time_pick);

        // 确认按钮
        confirmBtn.setClickedListener((Component component) -> {
            int year = tp.getYear();
            int month = tp.getMonth();
            int day = tp.getDayOfMonth();
            confirmPickTime.pickTimeCallBack(year, month, day, pt.getHour(), pt.getMinute());
            cd.destroy();
        });
        // 取消按钮
        Button cancelBtn = (Button) dl.findComponentById(ResourceTable.Id_time_pick_cancel);
        cancelBtn.setClickedListener((Component component) -> {
            cd.destroy();
        });
        cd.setSize(600, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
    }

    public static void pickType(Context context, PickTypeCallBack pickTypeCallBack) {
        CommonDialog cd = new CommonDialog(context);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_type_select_dialog, null, false);
        PageSlider ps = (PageSlider) dl.findComponentById(ResourceTable.Id_page_slider);
        Text payText = (Text) dl.findComponentById(ResourceTable.Id_paytext_text);
        payText.setTextColor(Color.BLUE);
        Text incomeText = (Text) dl.findComponentById(ResourceTable.Id_incometext_text);

        ps.addPageChangedListener(new PageSlider.PageChangedListener() {
            @Override
            public void onPageSliding(int i, float v, int i1) {

            }

            @Override
            public void onPageSlideStateChanged(int i) {

            }

            @Override
            public void onPageChosen(int i) {
                if(i == 0) {
                    payText.setTextColor(Color.BLUE);
                    incomeText.setTextColor(Color.BLACK);
                } else {
                    payText.setTextColor(Color.BLACK);
                    incomeText.setTextColor(Color.BLUE);
                }
            }
        });
        ps.setProvider(new PageProvider(new PageProvider.ClickedListener() {
            @Override
            public void click(int majorType, int minorType) {
                pickTypeCallBack.pickTypeCallBack(majorType, minorType);
                cd.destroy();
            }
        }, context));
        cd.setSize(600, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
    }
}
