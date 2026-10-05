package com.skillbridge.model;

import java.io.Serializable;

public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String icon;
    private String description;
    private int workerCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getWorkerCount() { return workerCount; }
    public void setWorkerCount(int workerCount) { this.workerCount = workerCount; }
}
