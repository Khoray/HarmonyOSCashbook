package com.khoray.cashbook.slice;

import com.khoray.cashbook.ResourceTable;
import com.khoray.cashbook.model.Const;
import com.khoray.cashbook.model.DialogHelper;
import com.khoray.cashbook.model.RecordBean;
import com.khoray.cashbook.model.RecordDatabase;
import com.khoray.cashbook.provider.RecordItemProvider;
import com.khoray.cashbook.utils.DebugUtil;
import com.khoray.cashbook.utils.TimeUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MainAbilitySlice extends AbilitySlice {
    long startTime, endTime;
    String noteContain;
    int recordMajorType, recordMinorType;
    Text startTimeText, endTimeText, typeText;
    TextField searchField;
    Button startTimeModifyBtn, endTimeModifyBtn, typeModifyBtn, typeClearBtn, addandeditBtn;
    ListContainer listContainer;
    OrmContext ormContext;
    DatabaseHelper databaseHelper;
    List<RecordBean> records;
    RecordItemProvider recordItemProvider;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);


        initDatabase();

        getComponents();
        initListContainer();
        updateRecordList();

        setButtonListener();
    }

    private void updateRecordList() {
        // TODO change to normal
        OrmPredicates ormPredicates = ormContext.where(RecordBean.class);
        ormPredicates.lessThan("time", endTime + 24 * 60);
        ormPredicates.and().greaterThanOrEqualTo("time", startTime);
        if(recordMajorType != -1) {
            ormPredicates.and().equalTo("majorType", recordMajorType);
            ormPredicates.and().equalTo("minorType", recordMinorType);
        }
        String searchStr = ((TextField) findComponentById(ResourceTable.Id_search_field)).getText();
        if(!searchStr.equals("")) {
            ormPredicates.and().contains("note", searchStr);
        }
        records = ormContext.query(ormPredicates);
        DebugUtil.showToast(getContext(), Integer.toString(records.size()) + "  " + Long.toString(startTime));
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
        searchField = (TextField) findComponentById(ResourceTable.Id_search_field);
        listContainer = (ListContainer) findComponentById(ResourceTable.Id_main_list_container);
        startTimeText = (Text) findComponentById(ResourceTable.Id_start_time_text);
        endTimeText = (Text) findComponentById(ResourceTable.Id_end_time_text);
        startTimeModifyBtn = (Button) findComponentById(ResourceTable.Id_start_time_modify_btn);
        endTimeModifyBtn = (Button) findComponentById(ResourceTable.Id_end_time_modify_btn);
        typeModifyBtn = (Button) findComponentById(ResourceTable.Id_type_select_btn);
        typeClearBtn = (Button) findComponentById(ResourceTable.Id_type_clear_btn);
        typeText = (Text) findComponentById(ResourceTable.Id_type_text);
        addandeditBtn = (Button) findComponentById(ResourceTable.Id_addandedit_btn);

    }

    public void setButtonListener() {
        startTimeModifyBtn.setClickedListener(this::pickStartTime);
        endTimeModifyBtn.setClickedListener(this::pickEndTime);
        typeModifyBtn.setClickedListener(this::pickType);
        typeClearBtn.setClickedListener((Component component) -> {
            typeText.setText("全部");
            recordMinorType = -1;
            recordMajorType = -1;
            updateRecordList();
        });
        addandeditBtn.setClickedListener(this::addandedit);
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

    void pickType(Component component) {
        DialogHelper.pickType(getContext(), ((majorType, minorType) -> {
            recordMajorType = majorType;
            recordMinorType = minorType;
            if(recordMajorType == 0) {
                typeText.setText("支出->" + Const.payType[minorType]);
            } else {
                typeText.setText("收入->" + Const.incomeType[minorType]);
            }
            updateRecordList();
        }));
    }

    void pickStartTime(Component component) {
        DialogHelper.pickDate(getContext(), (year, month, day, hour, minute) -> {
            startTime = TimeUtil.YMDtoTime(year, month, day);
            startTimeText.setText(TimeUtil.formatYMD(year, month, day));
            updateRecordList();
        });
    }

    void pickEndTime(Component component) {
        DialogHelper.pickDate(getContext(), (year, month, day, hour, minute) -> {
            endTime = TimeUtil.YMDtoTime(year, month, day);
            endTimeText.setText(TimeUtil.formatYMD(year, month, day));
            updateRecordList();
        });
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
