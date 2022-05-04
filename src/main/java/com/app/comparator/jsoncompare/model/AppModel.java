package com.app.comparator.jsoncompare.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AppModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    String status;
    String fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "AppModel{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", fields='" + fields + '\'' +
                '}';
    }
}
