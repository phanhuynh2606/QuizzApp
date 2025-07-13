package com.example.quizzapp.models;

public class ExamType {
    private String _id;
    private String typeCode;
    private String typeName;
    private String description;

    public String getId() {
        return _id;
    }
    public void setId(String _id) {
        this._id = _id;
    }
    public String getTypeCode() {
        return typeCode;
    }
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
