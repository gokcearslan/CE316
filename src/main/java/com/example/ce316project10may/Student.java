package com.example.ce316project10may;

public class Student {

    private String student_id;
    private String student_result;

    public Student(String student_id, String student_result) {
        this.student_id = student_id;
        this.student_result = student_result;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_result() {
        return student_result;
    }

    public void setStudent_result(String student_result) {
        this.student_result = student_result;
    }
}
