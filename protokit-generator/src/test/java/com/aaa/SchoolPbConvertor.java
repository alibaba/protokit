package com.aaa;

import java.util.Map;

import com.aaa.SchoolPB.Builder;
import com.alibaba.protokit.gen.PbConvertor;

public class SchoolPbConvertor implements PbConvertor<School>{

    @Override
    public <T2> T2 convertToPbObject(School object) {
        // TODO Auto-generated method stub
        Builder builder = SchoolPB.newBuilder();
        builder.setName(object.getName());
        
//        builder.putAllStudentNameMap(values);
        
//        StudentPbConvertor studentPbConvertor = new StudentPbConvertor();
//        studentPbConvertor.convertToPbObject(object)
//        builder.addStudents(value)
//        builder.addAllStudents(object.getStudents());
        
        return null;
    }

    @Override
    public School fromPbObject(Object pbObject) {
        // TODO Auto-generated method stub
        
        School school = new School();
        
        SchoolPB schoolPb = (SchoolPB)pbObject;
        Map<String, StudentPB> nameToStudentsMap = schoolPb.getNameToStudentsMap();
//        school.setNameToStudents(nameToStudentsMap);
        
        Map<Long, StudentPB> idToStudentsMap = schoolPb.getIdToStudentsMap();
        
        return null;
    }





}
