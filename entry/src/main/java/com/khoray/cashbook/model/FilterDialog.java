package com.khoray.cashbook.model;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.utils.DebugUtil;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.app.Context;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class FilterDialog {
    Context context;
    CommonDialog cd;
    DirectionalLayout dl;
    Button confirmBtn, cancelBtn, startTimeModify, endTimeModify, typeModify, valueModify;
    Text startTimeText, endTimeText, typeText, filterTypeText;
    TextField noteTextField, lowTf, highTf;
    FilterCallBack filterCallBack;
    FilterBean currentFilter;

    public FilterDialog(Context context, FilterCallBack filterCallBack, FilterBean currentFilter, Text filterTypeText) {
        this.filterTypeText = filterTypeText;
        this.context = context;
        this.filterCallBack = filterCallBack;
        this.currentFilter = new FilterBean(currentFilter);

        cd = new CommonDialog(context);
        dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_more_filter_dialog, null, false);

        getComponents();
        fillFilter();
        setListeners();
        cd.setSize(MATCH_PARENT, MATCH_CONTENT);
        cd.setAlignment(LayoutAlignment.BOTTOM);
        cd.setContentCustomComponent(dl);
        cd.show();
    }

    private void fillFilter() {
        if(currentFilter.startTime != -1) {
            startTimeText.setText(TimeUtil.formatYMD(currentFilter.startTime));
        }
        if(currentFilter.endTime != -1) {
            endTimeText.setText(TimeUtil.formatYMD(currentFilter.endTime));
        }
        String typeStr = "";
        if(currentFilter.majorType == 0) {
            typeStr += "支出";
            if(currentFilter.minorType != -1) {
                typeStr += "->" + Const.payType[currentFilter.minorType];
            }
        } else if(currentFilter.majorType == 1) {
            typeStr += "收入";
            if(currentFilter.minorType != -1) {
                typeStr += "->" + Const.incomeType[currentFilter.minorType];
            }
        } else {
            typeStr = "全部";
        }
        typeText.setText(typeStr);
        if(currentFilter.lowValue != -1) {
            lowTf.setText(Integer.toString(currentFilter.lowValue));
        }
        if(currentFilter.highValue != -1) {
            highTf.setText(Integer.toString(currentFilter.highValue));
        }
        noteTextField.setText(currentFilter.note);
    }

    public void getComponents() {
        confirmBtn = (Button) dl.findComponentById(ResourceTable.Id_more_filter_confirm_btn);
        cancelBtn = (Button) dl.findComponentById(ResourceTable.Id_more_filter_cancel_btn);
        noteTextField = (TextField) dl.findComponentById(ResourceTable.Id_search_field);
        startTimeText = (Text) dl.findComponentById(ResourceTable.Id_start_time_text);
        endTimeText = (Text) dl.findComponentById(ResourceTable.Id_end_time_text);
        startTimeModify = (Button) dl.findComponentById(ResourceTable.Id_start_time_modify_btn);
        endTimeModify = (Button) dl.findComponentById(ResourceTable.Id_end_time_modify_btn);
        typeModify = (Button) dl.findComponentById(ResourceTable.Id_type_clear_btn);
        typeText = (Text) dl.findComponentById(ResourceTable.Id_type_text);
        lowTf = (TextField) dl.findComponentById(ResourceTable.Id_low_value_tf);
        highTf = (TextField) dl.findComponentById(ResourceTable.Id_high_value_tf);
    }

    public void setListeners() {
        confirmBtn.setClickedListener((Component c) -> {
            try {
                if(!lowTf.getText().equals("")) currentFilter.lowValue = Integer.parseInt(lowTf.getText());
                if(!highTf.getText().equals("")) currentFilter.highValue = Integer.parseInt(highTf.getText());
            } catch (Exception e) {
                e.printStackTrace();
                DebugUtil.showToast(context, "金额范围需要是整数");
                return;
            }
            currentFilter.note = noteTextField.getText();
            filterTypeText.setText("自定义筛选");
            filterCallBack.filterCallBack(currentFilter);
            cd.destroy();
        });
        cancelBtn.setClickedListener((Component c) -> {
            cd.destroy();
        });
        startTimeText.setClickedListener(this::pickStartTime);
        endTimeText.setClickedListener(this::pickEndTime);
        typeText.setClickedListener(this::pickType);
        startTimeModify.setClickedListener((Component c) -> {
            currentFilter.startTime = -1;
            startTimeText.setText("-");
        });
        endTimeModify.setClickedListener((Component c) -> {
            currentFilter.endTime = -1;
            endTimeText.setText("-");
        });
        typeModify.setClickedListener((Component component) -> {
            typeText.setText("全部");
            currentFilter.majorType = -1;
            currentFilter.minorType = -1;
        });
    }
    void pickType(Component component) {
        DialogHelper.pickType(context, ((majorType, minorType) -> {
            currentFilter.majorType = majorType;
            currentFilter.minorType = minorType;
            if(currentFilter.majorType == 0) {
                typeText.setText("支出->" + Const.payType[minorType]);
            } else {
                typeText.setText("收入->" + Const.incomeType[minorType]);
            }
        }));
    }

    void pickStartTime(Component component) {
        DialogHelper.pickDate(context, (year, month, day, hour, minute) -> {
            currentFilter.startTime = TimeUtil.YMDtoTime(year, month, day);
            startTimeText.setText(TimeUtil.formatYMD(year, month, day));
        });
    }

    void pickEndTime(Component component) {
        DialogHelper.pickDate(context, (year, month, day, hour, minute) -> {
            currentFilter.endTime = TimeUtil.YMDtoTime(year, month, day);
            endTimeText.setText(TimeUtil.formatYMD(year, month, day));
        });
    }

    public static interface FilterCallBack {
        void filterCallBack(FilterBean fb);
    }
}
