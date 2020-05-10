package com.github.nemanovich.db.oracle.recursive.entity;

public class DeptCfoEntity {

    Integer departId;
    String cfo;

    public DeptCfoEntity(Integer departId, String cfo) {
        this.departId = departId;
        this.cfo = cfo;
    }

    public DeptCfoEntity() {
    }

    public Integer getDepartId() {
        return departId;
    }

    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    public String getCfo() {
        return cfo;
    }

    public void setCfo(String cfo) {
        this.cfo = cfo;
    }
}
