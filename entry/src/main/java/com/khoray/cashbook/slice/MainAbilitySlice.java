package com.khoray.cashbook.slice;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.model.*;
import com.khoray.cashbook.provider.RecordItemProvider;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainAbilitySlice extends AbilitySlice {
    FilterBean currentFilter = FilterBean.getMonthFilter();
    Text payText, incomeText, totalText, filterTypeText;
//    TextField searchField;
    Button addandeditBtn, filterButton;
    ListContainer listContainer;
    OrmContext ormContext;
    DatabaseHelper databaseHelper;
    List<RecordBean> records = new ArrayList<>();
    RecordItemProvider recordItemProvider;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);


        initDatabase();
        getComponents();
        updateRecordListAndText();
        if(records.size() == 0) addDebugDatas();
        


        initListContainer();

        setListener();


    }

    /**
     * 添加随机生成的账目信息用于测试
     */
    private void addDebugDatas() {
        Random random = new Random();
        for(int i = 0; i < 200; i++) {
            RecordBean rb = new RecordBean();
            rb.setTime(random.nextLong() % (Const.dayMilllis * 20) - Const.dayMilllis * 20 + System.currentTimeMillis());
            rb.setValue(random.nextInt(1000) + (random.nextBoolean() ? 0.5 : 0));
            rb.setMajorType((random.nextBoolean() ? 0 : 1));
            rb.setMinorType((random.nextInt(9)));
            rb.setNote("用于测试");
            ormContext.insert(rb);
        }
        ormContext.flush();
        updateRecordListAndText();
    }


    /**
     * 更新record中的内容，并且更新界面中的payText和incomeText
     */
    private void updateRecordListAndText() {
        List<RecordBean> tmpRecords = ormContext.query(currentFilter.generatePredicates(ormContext));
        tmpRecords.sort(new Comparator<RecordBean>() {
            @Override
            public int compare(RecordBean recordBean, RecordBean t1) {
                if(recordBean.getTime() < t1.getTime()) {
                    return 1;
                } else if(recordBean.getTime() == t1.getTime()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        records.clear();

        double totalPay = 0, totalIncome = 0;

        int beg = 0;
        while(beg < tmpRecords.size()) {
            int ed = beg;
            RecordBean rb = new RecordBean();
            rb.isTitle = true;
            String d1 = TimeUtil.formatYMD(tmpRecords.get(beg).getTime());
            while(ed < tmpRecords.size()) {
                String d2 = TimeUtil.formatYMD(tmpRecords.get(ed).getTime());
                if(!d1.equals(d2)) break;
                double value = tmpRecords.get(ed).getValue();
                if(tmpRecords.get(ed).getMajorType() == 0) {
                    rb.pay += value;
                    totalPay += value;
                } else {
                    rb.income += value;
                    totalIncome += value;
                }
                ed++;
            }
            rb.setTime(tmpRecords.get(beg).getTime());
            records.add(rb);
            for(int i = beg; i < ed; i++) {
                records.add(tmpRecords.get(i));
            }
            beg = ed;
        }

//        DebugUtil.showToast(getContext(), "size:" + records.size());
        payText.setText(String.format("%.2f", totalPay));
        incomeText.setText(String.format("%.2f", totalIncome));
        totalText.setText(String.format("%.2f", totalIncome - totalPay));
    }

    /**
     * 更新ListContainer
     */
    private void updateRecordAndListContainer() {
        updateRecordListAndText();
        recordItemProvider.update(records);
    }

    private void initListContainer() {
        recordItemProvider = new RecordItemProvider(records, getContext(), new RecordItemProvider.ClickedListener() {
            @Override
            public void click(RecordBean record) {
                DialogHelper.addandeditRecord(getContext(), new DialogHelper.RecordCallBack() {
                    @Override
                    public void recordCallBack(RecordBean record) {

                        List<RecordBean> ret = ormContext.query((ormContext.where(RecordBean.class)).equalTo("recordId", record.getRecordId()));
                        ret.get(0).copy(record);
                        ormContext.update(ret.get(0));
                        ormContext.flush();
                        updateRecordAndListContainer();
                    }

                    @Override
                    public void recordDelCallBack(RecordBean record) {
                        ormContext.delete(record);
                        ormContext.flush();
                        updateRecordAndListContainer();
                    }
                }, "修改账目", true, record);
            }
        });
        listContainer.setItemProvider(recordItemProvider);
    }

    public void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
        ormContext = databaseHelper.getOrmContext("Record", "Record.db", RecordDatabase.class);
    }

    public void getComponents() {
        filterButton = (Button) findComponentById(ResourceTable.Id_filter_btn);
//        searchField = (TextField) findComponentById(ResourceTable.Id_search_field);
        listContainer = (ListContainer) findComponentById(ResourceTable.Id_main_list_container);
//        startTimeText = (Text) findComponentById(ResourceTable.Id_start_time_text);
//        endTimeText = (Text) findComponentById(ResourceTable.Id_end_time_text);
//        startTimeModifyBtn = (Button) findComponentById(ResourceTable.Id_start_time_modify_btn);
//        endTimeModifyBtn = (Button) findComponentById(ResourceTable.Id_end_time_modify_btn);
//        typeModifyBtn = (Button) findComponentById(ResourceTable.Id_type_select_btn);
//        typeClearBtn = (Button) findComponentById(ResourceTable.Id_type_clear_btn);
//        typeText = (Text) findComponentById(ResourceTable.Id_type_text);
        addandeditBtn = (Button) findComponentById(ResourceTable.Id_addandedit_btn);
//        searchBtn = (Button) findComponentById(ResourceTable.Id_search_btn);
        payText = (Text) findComponentById(ResourceTable.Id_pay_text);
        incomeText = (Text) findComponentById(ResourceTable.Id_income_text);
        totalText = (Text) findComponentById(ResourceTable.Id_total_rest);
        filterTypeText = (Text) findComponentById(ResourceTable.Id_filter_type_text);
    }

    public void setListener() {
        filterButton.setClickedListener((Component c) -> {
            DialogHelper.pickFilter(getContext(), new FilterDialog.FilterCallBack() {
                @Override
                public void filterCallBack(FilterBean fb) {
                    // 回调接收到筛选器后查询数据库更新record和ListContainer
                    currentFilter = fb;
                    updateRecordAndListContainer();
                }
            }, currentFilter, filterTypeText);
        });
        addandeditBtn.setClickedListener(this::addandedit);
    }

    void addandedit(Component component) {
        DialogHelper.addandeditRecord(getContext(), new DialogHelper.RecordCallBack() {
            @Override
            public void recordCallBack(RecordBean record) {
                // 记账并更新
//                DebugUtil.showToast(getContext(), "ok:" + Boolean.toString(ok) + " okflush:" + Boolean.toString(okflush));
                updateRecordAndListContainer();
            }

            @Override
            public void recordDelCallBack(RecordBean record) {

            }
        }, "记一笔账", false, new RecordBean());
    }



    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
