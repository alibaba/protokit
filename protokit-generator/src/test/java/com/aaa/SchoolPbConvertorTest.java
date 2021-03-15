package com.aaa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.abc.Student;

/**
 * 
 * @author hengyunabc 2021-03-10
 *
 */
public class SchoolPbConvertorTest {

    @Test
    public void test() {
        SchoolPbConvertor convertor = new SchoolPbConvertor();

        School school = new School();

        school.setName("sss");

        Student student1 = new Student();
        student1.setAge(11);
        student1.setId(1234);
        student1.setName("nnnn");
        student1.setChecked(true);

        List<Student> students = new ArrayList<>();
        students.add(student1);

        school.setStudents(students);

        Map<Long, Student> idToStudents = new HashMap<>();
        idToStudents.put(student1.getId(), student1);
        school.setIdToStudents(idToStudents);

        Map<String, Student> nameToStudents = new HashMap<>();
        nameToStudents.put(student1.getName(), student1);
        school.setNameToStudents(nameToStudents);
        
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1234);
        school.setIds(ids);

        SchoolPB pbObject = convertor.convertToPbObject(school);
        
        School school2 = convertor.fromPbObject(pbObject);

        Student student2 = school2.getStudents().get(0);

        Assertions.assertThat(school.getName()).isEqualTo(school2.getName());

        Assertions.assertThat(student2).isEqualTo(student1);

        Assertions.assertThat(school).isEqualTo(school2);

    }

}
