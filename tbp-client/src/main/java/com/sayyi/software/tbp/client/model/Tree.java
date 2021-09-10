package com.sayyi.software.tbp.client.model;

import java.util.ArrayList;
import java.util.List;

public class Tree {

    public Tree() {
    }

    public Tree(String name) {
        this.name = name;
    }

    public Tree(String name, List<Tree> children) {
        this.name = name;
        this.children = children;
    }

    private String name;
    private List<Tree> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }
}
