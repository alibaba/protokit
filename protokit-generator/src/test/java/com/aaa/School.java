package com.aaa;

import java.util.List;
import java.util.Map;

import com.abc.Student;
import com.alibaba.protokit.annotation.PbField;
import com.alibaba.protokit.annotation.PbMessage;

@PbMessage
public class School {

    @PbField(tag = 1)
    private String name;
    @PbField(tag = 2)
    private List<Student> students;

    @PbField(tag = 3)
    private Map<String, Student> nameToStudents;

    @PbField(tag = 4)
    private Map<Long, Student> idToStudents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, Student> getNameToStudents() {
        return nameToStudents;
    }

    public void setNameToStudents(Map<String, Student> nameToStudents) {
        this.nameToStudents = nameToStudents;
    }

    public Map<Long, Student> getIdToStudents() {
        return idToStudents;
    }

    public void setIdToStudents(Map<Long, Student> idToStudents) {
        this.idToStudents = idToStudents;
    }

}
