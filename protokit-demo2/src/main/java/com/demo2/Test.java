package com.demo2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.test.demo.Company;
import com.test.demo.CompanyPB;
import com.test.demo.CompanyPbConvertor;
import com.test.demo.User;
import com.test.demo.UserPB;
import com.test.demo.UserPbConvertor;

public class Test {

    public static void main(String[] args) {
        UserPbConvertor userConvertor = new UserPbConvertor();

        User user = new User();
        user.setId(999);
        user.setName("nnn");
        UserPB userPB = userConvertor.convertToPbObject(user);

        System.err.println(userPB);

        CompanyPbConvertor companyConvertor = new CompanyPbConvertor();
        Company company = new Company();

        company.setIds(Arrays.asList(1, 2, 3));
        Map<Long, User> idToUsers = new HashMap<>();
        idToUsers.put(user.getId(), user);
        company.setIdToUsers(idToUsers);

        company.setUsers(Arrays.asList(user));
        company.setName("ccc");
        CompanyPB companyPb = companyConvertor.convertToPbObject(company);

        System.err.println(company);
        System.err.println(companyPb);
    }

}
