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

    @PbField(tag = 5)
    private List<Integer> ids;

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

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idToStudents == null) ? 0 : idToStudents.hashCode());
        result = prime * result + ((ids == null) ? 0 : ids.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nameToStudents == null) ? 0 : nameToStudents.hashCode());
        result = prime * result + ((students == null) ? 0 : students.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        School other = (School) obj;
        if (idToStudents == null) {
            if (other.idToStudents != null)
                return false;
        } else if (!idToStudents.equals(other.idToStudents))
            return false;
        if (ids == null) {
            if (other.ids != null)
                return false;
        } else if (!ids.equals(other.ids))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nameToStudents == null) {
            if (other.nameToStudents != null)
                return false;
        } else if (!nameToStudents.equals(other.nameToStudents))
            return false;
        if (students == null) {
            if (other.students != null)
                return false;
        } else if (!students.equals(other.students))
            return false;
        return true;
    }

}
