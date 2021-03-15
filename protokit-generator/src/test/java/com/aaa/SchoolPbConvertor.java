package com.aaa;

import java.util.List;
import java.util.Map;

import com.aaa.SchoolPB.Builder;
import com.alibaba.protokit.gen.PbConvertor;
import com.alibaba.protokit.gen.PbConvertorUtils;

public class SchoolPbConvertor implements PbConvertor<School> {

    @Override
    public <T> T convertToPbObject(School object) {
        Builder builder = SchoolPB.newBuilder();
        builder.setName(object.getName());

        com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();

        builder.putAllIdToStudents(PbConvertorUtils.convertToPbMap(object.getIdToStudents(), convertor));
        builder.putAllNameToStudents(PbConvertorUtils.convertToPbMap(object.getNameToStudents(), convertor));
        builder.addAllStudents(PbConvertorUtils.convertToPbList(object.getStudents(), convertor));
        builder.addAllIds(PbConvertorUtils.convertToPbList(object.getIds(), new IntegerPbConvertor()));

        return (T) builder.build();
    }

    @Override
    public School fromPbObject(Object pbObject) {
        School school = new School();

        com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
        
        SchoolPB schoolPb = (SchoolPB) pbObject;
        
        school.setName(schoolPb.getName());
        
        Map<String, com.abc.StudentPB> nameToStudentsMap = schoolPb.getNameToStudentsMap();
        school.setNameToStudents(PbConvertorUtils.fromPbMap(nameToStudentsMap, convertor));
        
        Map<Long, com.abc.StudentPB> idToStudentsMap = schoolPb.getIdToStudentsMap();
        school.setIdToStudents(PbConvertorUtils.fromPbMap(idToStudentsMap, convertor));
        
        List<com.abc.StudentPB> studentsList = schoolPb.getStudentsList();
        school.setStudents(PbConvertorUtils.fromPbList(studentsList, convertor));
        
        List<Integer> idsList = schoolPb.getIdsList();
        school.setIds(PbConvertorUtils.fromPbList(idsList, new IntegerPbConvertor()));

        return school;
    }
    
    public static class IntegerPbConvertor implements PbConvertor<Integer> {

        @Override
        public <T2> T2 convertToPbObject(Integer object) {
            return (T2) object;
        }

        @Override
        public Integer fromPbObject(Object pbObject) {
            return (Integer) pbObject;
        }

    }

}
