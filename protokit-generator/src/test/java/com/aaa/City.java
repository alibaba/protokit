package com.aaa;

import java.util.List;

import com.alibaba.protokit.annotation.PbField;
import com.alibaba.protokit.annotation.PbMessage;

@PbMessage
public class City {
    @PbField(tag = 1)
    private String name;
    @PbField(tag = 2)
    private List<School> schools;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

}
