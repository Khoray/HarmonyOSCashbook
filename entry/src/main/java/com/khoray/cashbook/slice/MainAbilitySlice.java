package com.khoray.cashbook.slice;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.model.*;
import com.khoray.cashbook.provider.RecordItemProvider;
import com.khoray.cashbook.utils.DebugUtil;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    FilterBean currentFilter = new FilterBean();
    Text payText, incomeText;
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
        initListContainer();


        setListener();

        updateRecordList();
    }

    private void updateRecordList() {
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
            while(ed < tmpRecords.size() && tmpRecords.get(ed).getTime() / Const.dayMilllis == tmpRecords.get(beg).getTime() / Const.dayMilllis) {
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


        payText.setText(Double.toString(totalPay));
        incomeText.setText(Double.toString(totalIncome));
        recordItemProvider.update(records);

    }

    private void initListContainer() {
        recordItemProvider = new RecordItemProvider(records, getContext());
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
    }

    public void setListener() {
//        startTimeModifyBtn.setClickedListener(this::pickStartTime);
//        endTimeModifyBtn.setClickedListener(this::pickEndTime);
//        typeModifyBtn.setClickedListener(this::pickType);
//        typeClearBtn.setClickedListener((Component component) -> {
//            typeText.setText("全部");
//            recordMinorType = -1;
//            recordMajorType = -1;
//            updateRecordList();
//        });
        filterButton.setClickedListener((Component c) -> {
            DialogHelper.pickFilter(getContext(), new FilterDialog.FilterCallBack() {
                @Override
                public void filterCallBack(FilterBean fb) {
                    currentFilter = fb;
                    updateRecordList();
                }
            }, currentFilter);
        });
        addandeditBtn.setClickedListener(this::addandedit);
//        searchBtn.setClickedListener((Component component) -> {
//            updateRecordList();
//        });
//        searchBtn.setTouchFocusable(true);
    }

    void addandedit(Component component) {
        DialogHelper.addandeditRecord(getContext(), new DialogHelper.RecordCallBack() {
            @Override
            public void recordCallBack(RecordBean record) {
                boolean ok = ormContext.insert(record);
                boolean okflush = ormContext.flush();
                DebugUtil.showToast(getContext(), "ok:" + Boolean.toString(ok) + " okflush:" + Boolean.toString(okflush));
                updateRecordList();
            }

            @Override
            public void recordDelCallBack(RecordBean record) {

            }
        }, "记一笔账", false, new RecordBean(-1));
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
