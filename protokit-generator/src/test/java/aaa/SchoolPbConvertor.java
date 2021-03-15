package aaa;
import com.aaa.SchoolPB;
import com.aaa.SchoolPB.Builder;
import com.alibaba.protokit.gen.PbConvertor;
public class SchoolPbConvertor implements com.alibaba.protokit.gen.PbConvertor<com.aaa.School>{

    @Override
    public <T> T convertToPbObject(com.aaa.School object) {
        com.aaa.SchoolPB.Builder builder = com.aaa.SchoolPB.newBuilder();
        builder.setName(object.getName());
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            builder.addAllStudents(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbList(object.getStudents(), convertor));
        }
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            builder.putAllNameToStudents(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbMap(object.getNameToStudents(), convertor));
        }
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            builder.putAllIdToStudents(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbMap(object.getIdToStudents(), convertor));
        }
        {
            com.alibaba.protokit.gen.IntegerPbConvertor convertor = new com.alibaba.protokit.gen.IntegerPbConvertor();
            builder.addAllIds(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbList(object.getIds(), convertor));
        }
        return (T) builder.build();
    }
    @Override
    public com.aaa.School fromPbObject(Object pbObject) {
        com.aaa.School object = new com.aaa.School();
        com.aaa.SchoolPB fromPbObject = (com.aaa.SchoolPB) pbObject;
        object.setName(fromPbObject.getName());
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            object.setStudents(com.alibaba.protokit.gen.PbConvertorUtils.fromPbList(fromPbObject.getStudentsList(), convertor));
        }
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            object.setNameToStudents(com.alibaba.protokit.gen.PbConvertorUtils.fromPbMap(fromPbObject.getNameToStudentsMap(), convertor));
        }
        {
            com.abc.StudentPbConvertor convertor = new com.abc.StudentPbConvertor();
            object.setIdToStudents(com.alibaba.protokit.gen.PbConvertorUtils.fromPbMap(fromPbObject.getIdToStudentsMap(), convertor));
        }
        {
            com.alibaba.protokit.gen.IntegerPbConvertor convertor = new com.alibaba.protokit.gen.IntegerPbConvertor();
            object.setIds(com.alibaba.protokit.gen.PbConvertorUtils.fromPbList(fromPbObject.getIdsList(), convertor));
        }
        return object;
    }
}