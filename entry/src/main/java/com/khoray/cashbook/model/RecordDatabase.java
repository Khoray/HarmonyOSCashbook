package com.khoray.cashbook.model;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;

@Database(entities = {RecordBean.class}, version = 1)
public abstract class RecordDatabase extends OrmDatabase {
}
