package com.sayyi.software.tbp.client.component.util;

import com.sayyi.software.tbp.ui.api.sidebar.SidebarToolRegister;
import javafx.scene.control.Label;

import java.util.LinkedList;
import java.util.List;

public class SidebarToolFactory implements SidebarToolRegister {

    private static final SidebarToolFactory instance = new SidebarToolFactory();

    private SidebarToolFactory() {}

    public static SidebarToolFactory getInstance() {
        return instance;
    }

    private List<Label> labels = new LinkedList<>();

    @Override
    public void registry(Label label) {
        labels.add(label);
    }

    public List<Label> getLabels() {
        return labels;
    }
}
