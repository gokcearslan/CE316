package com.example.ce316project10may;

public class ProjectDetail {
    private final String projectName;
    private final String studentId;
    private final String configName;

    private final String studentOutput;

    private final String expectedOutput;
    private final String studentResult;


    public ProjectDetail(String projectName, String studentId, String configName, String studentOutput, String expectedOutput, String studentResult) {
        this.projectName = projectName;
        this.studentId = studentId;
        this.configName = configName;
        this.studentOutput = studentOutput;
        this.expectedOutput = expectedOutput;
        this.studentResult = studentResult;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getConfigName() {
        return configName;
    }

    public String getStudentOutput() {
        return studentOutput;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public String getStudentResult() {
        return studentResult;
    }
}


