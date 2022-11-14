package com.khoray.cashbook.provider;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.model.Const;
import com.khoray.cashbook.model.RecordBean;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.agp.components.*;
import ohos.app.Context;

import java.util.List;

public class RecordItemProvider extends BaseItemProvider {
    List<RecordBean> records;

    public RecordItemProvider(List<RecordBean> records, Context ctx) {
        this.records = records;
        this.ctx = ctx;
    }

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
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(ctx).parse(ResourceTable.Layout_list_item, null, false);
        Text majorTypeText = (Text) dl.findComponentById(ResourceTable.Id_item_majortype_text);
        Text minorTypeText = (Text) dl.findComponentById(ResourceTable.Id_item_type_text);
        Text noteText = (Text) dl.findComponentById(ResourceTable.Id_item_note_text);
        Text moneyText = (Text) dl.findComponentById(ResourceTable.Id_item_money_text);
        Text timeText = (Text) dl.findComponentById(ResourceTable.Id_item_time_text);


        if(nowRecord.getMajorType() == 0) {
            majorTypeText.setText("支");
            minorTypeText.setText(Const.payType[nowRecord.getMinorType()]);
        } else {
            majorTypeText.setText("出");
            minorTypeText.setText(Const.incomeType[nowRecord.getMinorType()]);
        }

        noteText.setText(nowRecord.getNote());
        moneyText.setText(Double.toString(nowRecord.getValue()));
        timeText.setText(TimeUtil.formatYMDHM(nowRecord.getTime()));
//        componentContainer.addComponent(dl);
        return dl;
    }
}
