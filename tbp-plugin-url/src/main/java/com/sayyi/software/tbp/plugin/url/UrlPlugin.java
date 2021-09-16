package com.sayyi.software.tbp.plugin.url;

import com.sayyi.software.tbp.db.api.component.DbHelper;
import com.sayyi.software.tbp.ui.api.Plugin;
import com.sayyi.software.tbp.ui.api.UiHelper;
import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;

/**
 * 添加元数据检索页的右键菜单项，从剪切板获取url，转储为元数据库中的html文件
 */
public class UrlPlugin implements Plugin {

    public UrlPlugin() {}

    @Override
    public void init(DbHelper dbHelper, UiHelper uiHelper) {
        MenuItemProvider pasteUrlMenuItemProvider = new PasteUrlMenuItemProvider(dbHelper);
        uiHelper.getMenuItemProviderRegister().registry(pasteUrlMenuItemProvider);
    }
}
