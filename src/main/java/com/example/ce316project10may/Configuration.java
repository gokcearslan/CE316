package com.example.ce316project10may;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private String filePath;
    private String language;
    private String command;

    private String className;
    private String configName;
    private String input;
    private String projectName;
    // ...
    // Add a getter and setter for the projectName property
    public String getProjectName() {
        return projectName;
    }
    public Configuration(String X, String Y) {
        this.X = X;
        this.Y = Y;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }  public String X;
    public String Y;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Configuration(String filePath, String language, String command, String className, String configName) {
        this.filePath = filePath;
        this.language = language;
        this.command = command;
        this.className = className;
        this.configName = configName;
    }

    public Configuration(String language, String command, String className, String configName) {
        this.language = language;
        this.command = command;
        this.className = className;
        this.configName = configName;
    }

    public Configuration(String language,String configName, String input) {
        this.language = language;
        this.configName = configName;
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}