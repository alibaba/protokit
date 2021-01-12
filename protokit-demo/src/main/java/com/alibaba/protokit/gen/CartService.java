package com.alibaba.protokit.gen;

import java.util.List;
import java.util.Map;

public interface CartService {
    User echoUser(User user);

    List<User> echoUserList(List<User> userList);

    Map<String, User> echoMap(Map<String, User> userMap);

    Map<String, List<User>> echoMapList(Map<String, List<User>> userMap);

    List<Map<String, User>> echoListMap(List<Map<String, User>> userMap);

    Map<String, Map<String, User>> echoMapMap(Map<String, Map<String, User>> user);

    Map<String, Map<String, Map<String, User>>> echoMapMapMap(Map<String, Map<String, Map<String, User>>> userDTO);

    boolean multiParam(User user, String productID, int quantity);
//    TODO InnerClass
//    // TODO 异常
//    String getProviderIp(String name, int age);

}