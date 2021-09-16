package com.sayyi.software.tbp.ui.api;

import com.sayyi.software.tbp.db.api.component.DbHelper;

public interface Plugin {

    void init(DbHelper dbHelper, UiHelper uiHelper);
}
