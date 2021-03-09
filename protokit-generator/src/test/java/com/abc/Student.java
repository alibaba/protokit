package com.abc;

import com.alibaba.protokit.annotation.PbField;
import com.alibaba.protokit.annotation.PbMessage;

@PbMessage
public class Student {
    @PbField(tag = 1)
    private long id;

    @PbField(tag = 2)
    private String name;
    @PbField(tag = 3)
    private int age;

    @PbField(tag = 4)
    private boolean checked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
