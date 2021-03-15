package com.abc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author hengyunabc 2021-03-10
 *
 */
public class StudentPbConvertorTest {

    @Test
    public void test() {
        StudentPbConvertor convertor = new StudentPbConvertor();

        Student student = new Student();
        student.setAge(10);
        student.setId(123);
        student.setName("hello");

        StudentPB pbObject = convertor.convertToPbObject(student);

        Student student2 = convertor.fromPbObject(pbObject);

        Assertions.assertThat(student.getAge()).isEqualTo(student2.getAge());

        Assertions.assertThat(student.getId()).isEqualTo(student2.getId());
        Assertions.assertThat(student.getName()).isEqualTo(student2.getName());
    }

}
