package com.abc;

import com.aaa.StudentPB;
import com.aaa.StudentPB.Builder;
import com.alibaba.protokit.gen.PbConvertor;

public class StudentPbConvertor implements PbConvertor<Student>{

    @Override
    public <T> T convertToPbObject(Student object) {
        Builder builder = StudentPB.newBuilder();
        builder.setAge(object.getAge());
        builder.setName(object.getName());
        builder.setId(object.getId());
        builder.setChecked(object.isChecked());
        StudentPB pbObject = builder.build();
        return (T) pbObject;
    }

    @Override
    public Student fromPbObject(Object pbObject) {
        StudentPB object = (StudentPB)pbObject;
        Student returnObject = new Student();
        returnObject.setId(object.getId());
        returnObject.setAge(object.getAge());
        returnObject.setName(object.getName());
        returnObject.setChecked(object.getChecked());
        return null;
    }


}
