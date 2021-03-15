package com.test.demo;

import java.util.List;
import java.util.Map;

import com.alibaba.protokit.annotation.PbField;
import com.alibaba.protokit.annotation.PbMessage;

@PbMessage
public class Company {

    @PbField(tag = 1)
    private String name;
    @PbField(tag = 2)
    private List<User> users;

    @PbField(tag = 3)
    private Map<String, User> nameToUsers;

    @PbField(tag = 4)
    private Map<Long, User> idToUsers;

    @PbField(tag = 5)
    private List<Integer> ids;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Map<String, User> getNameToUsers() {
        return nameToUsers;
    }

    public void setNameToUsers(Map<String, User> nameToUsers) {
        this.nameToUsers = nameToUsers;
    }

    public Map<Long, User> getIdToUsers() {
        return idToUsers;
    }

    public void setIdToUsers(Map<Long, User> idToUsers) {
        this.idToUsers = idToUsers;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

}
