package com.sayyi.software.tbp.db.api.component;

import com.sayyi.software.tbp.common.Tree;

/**
 * tree组件，目前就提供两个功能，加载和存储。暂时不做增量处理
 */
public interface TreeComponent {

    Tree load();

    void store(Tree tree);
}
