package com.khoray.cashbook.provider;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.model.Const;
import com.khoray.cashbook.model.RecordBean;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordItemProvider extends BaseItemProvider {
    List<RecordBean> records;

    public RecordItemProvider(List<RecordBean> records, Context ctx, ClickedListener cl) {
        this.records = records;
        this.ctx = ctx;
        this.cl = cl;
    }
    ClickedListener cl;
    Context ctx;

    public void update(List<RecordBean> records) {
        this.records = records;
        notifyDataChanged();
    }

    public static interface ClickedListener {
        void click(RecordBean record);
    }

    @Override
    public int getCount() {
        if(records == null) return 0;
        return records.size();
    }

    @Override
    public Object getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        RecordBean nowRecord = records.get(i);
        if(nowRecord.isTitle) {
            DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(ctx).parse(ResourceTable.Layout_list_title, null, false);
            Text dayText = (Text) dl.findComponentById(ResourceTable.Id_title_day_text);
            Text ymText = (Text) dl.findComponentById(ResourceTable.Id_title_ym_text);
            Text payText = (Text) dl.findComponentById(ResourceTable.Id_title_pay_text);
            Text incomeText = (Text) dl.findComponentById(ResourceTable.Id_title_income_text);

            String year = (new SimpleDateFormat("yyyy")).format(new Date(nowRecord.getTime()));
            String month = (new SimpleDateFormat("MM")).format(new Date(nowRecord.getTime()));
            String day = (new SimpleDateFormat("dd")).format(new Date(nowRecord.getTime()));
            ymText.setText(year + "." + month);
            dayText.setText(day);
            payText.setText(Double.toString(nowRecord.pay));
            incomeText.setText(Double.toString(nowRecord.income));
            return dl;
        } else {
            DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(ctx).parse(ResourceTable.Layout_list_item, null, false);
            Image majorTypeImg = (Image) dl.findComponentById(ResourceTable.Id_item_majortype_img);
            Text minorTypeText = (Text) dl.findComponentById(ResourceTable.Id_item_type_text);
            Text noteText = (Text) dl.findComponentById(ResourceTable.Id_item_note_text);
            Text moneyText = (Text) dl.findComponentById(ResourceTable.Id_item_money_text);
            Text timeText = (Text) dl.findComponentById(ResourceTable.Id_item_time_text);


            if(nowRecord.getMajorType() == 0) {
                majorTypeImg.setPixelMap(Const.payImg[nowRecord.getMinorType()]);
                minorTypeText.setText(Const.payType[nowRecord.getMinorType()]);
                moneyText.setText("-" + Double.toString(nowRecord.getValue()));
                moneyText.setTextColor(new Color(Color.rgb(20, 186, 138)));
            } else {
                majorTypeImg.setPixelMap(Const.incomeImg[nowRecord.getMinorType()]);
                minorTypeText.setText(Const.incomeType[nowRecord.getMinorType()]);
                moneyText.setText("+" + Double.toString(nowRecord.getValue()));
                moneyText.setTextColor(new Color(Color.rgb(241, 83, 58)));
            }

            noteText.setText(nowRecord.getNote());

            timeText.setText(TimeUtil.formatYMDHM(nowRecord.getTime()));
            dl.setClickedListener((Component c) -> {
                cl.click(nowRecord);
            });
//        componentContainer.addComponent(dl);
            return dl;
        }


    }
}
