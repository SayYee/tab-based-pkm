package com.sayyi.software.tbp.client.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

/**
 * 用于列表展示的对象，仅提供必要的内容
 */
public class ObservableMetadata {

    private SimpleLongProperty id;
    private SimpleStringProperty name;
    private SimpleIntegerProperty type;
    private SimpleSetProperty<String> tags;
    private SimpleLongProperty lastUpdateTime;

    public ObservableMetadata() {
    }

    public ObservableMetadata(long id, String name, int type, Set<String> tags, long lastUpdateTime) {
        this.id = new SimpleLongProperty(id);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleIntegerProperty(type);
        this.tags = new SimpleSetProperty<>(FXCollections.observableSet());
        this.tags.addAll(tags);
        this.lastUpdateTime = new SimpleLongProperty(lastUpdateTime);
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getType() {
        return type.get();
    }

    public SimpleIntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
    }

    public ObservableSet<String> getTags() {
        return tags.get();
    }

    public SimpleSetProperty<String> tagsProperty() {
        return tags;
    }

    public void setTags(ObservableSet<String> tags) {
        this.tags.set(tags);
    }

    public long getLastUpdateTime() {
        return lastUpdateTime.get();
    }

    public SimpleLongProperty lastUpdateTimeProperty() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime.set(lastUpdateTime);
    }
}
