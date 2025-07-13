package com.example.quizzapp.models;

public class Subject {
    private String _id;
    private String subjectCode;
    private String subjectName;
    private Semester semester;
    private String description;

    public Subject(String _id, String subjectCode, String subjectName, Semester semester, String description) {
        this._id = _id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.semester = semester;
        this.description = description;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "_id='" + _id + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", semester=" + semester +
                ", description='" + description + '\'' +
                '}';
    }
}
