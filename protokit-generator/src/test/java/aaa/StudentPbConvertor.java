package aaa;
import com.abc.StudentPB;
import com.abc.StudentPB.Builder;
import com.alibaba.protokit.gen.PbConvertor;
public class StudentPbConvertor implements com.alibaba.protokit.gen.PbConvertor<com.abc.Student>{

    @Override
    public <T> T convertToPbObject(com.abc.Student object) {
        com.abc.StudentPB.Builder builder = com.abc.StudentPB.newBuilder();
        builder.setId(object.getId());
        builder.setName(object.getName());
        builder.setAge(object.getAge());
        builder.setChecked(object.isChecked());
        return (T) builder.build();
    }
    @Override
    public com.abc.Student fromPbObject(Object pbObject) {
        com.abc.Student object = new com.abc.Student();
        com.abc.StudentPB fromPbObject = (com.abc.StudentPB) pbObject;
        object.setId(fromPbObject.getId());
        object.setName(fromPbObject.getName());
        object.setAge(fromPbObject.getAge());
        object.setChecked(fromPbObject.getChecked());
        return object;
    }
}