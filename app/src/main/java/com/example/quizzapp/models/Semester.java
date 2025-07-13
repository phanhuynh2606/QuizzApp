package com.example.quizzapp.models;
public class Semester {
    private String _id;
    private String name;
    private String description;
    public Semester(String _id, String name, String description) {
        this._id = _id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return _id;
    }
    public void setId(String _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return "Semester{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
