package com.alibaba.protokit.gen.template;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.alibaba.protokit.annotation.PbAnnotationParser;
import com.alibaba.protokit.common.Constants;
import com.alibaba.protokit.model.MetaData;
import com.alibaba.protokit.utils.NameUtils;

import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Proto;

/**
 * 
 * @author hengyunabc 2021-03-09
 *
 */
public class JavaDumper {

    public static String dumpCode(Class<?> clazz) throws IOException {
        String filePath = clazz.getCanonicalName().replace(".", "/") + ".proto";
        PbAnnotationParser pbAnnotationParser = new PbAnnotationParser();
        Optional<MetaData> metaDataOptional = pbAnnotationParser.parse(clazz);

        MetaData metaData = metaDataOptional.get();

        Proto proto = metaData.getProto();
        STGroup group = new STGroupFile("convertor.stg");
        group.registerRenderer(Field.class, new FieldRenderer(proto, group));

        ST st = group.getInstanceOf("convertor_compiler_template");
        st.add("meta", metaData);
        String result = st.render();
        return result;
    }

    public static void dumpCode(Class<?> clazz, String outputDir) throws IOException {
        String filePath = NameUtils.convertorFileName(clazz);

        String result = dumpCode(clazz);

        FileUtils.write(new File(outputDir, filePath), result, "utf-8");
    }

    static class FieldRenderer implements AttributeRenderer<Field> {
        private Proto proto;
        private STGroup group;

        public FieldRenderer(Proto proto, STGroup group) {
            this.proto = proto;
            this.group = group;
        }

        @Override
        public String toString(Field field, String formatString, Locale locale) {

            if ("to".equals(formatString)) { // to method
                String pkg = proto.getPackage().getValue();
                if (field.isRepeated() && (!field.getTypeName().endsWith("_entry"))) {

                    // StudentPbConvertor convertor = new StudentPbConvertor();
                    // builder.putAllIdToStudents(PbConvertorUtils.convertToPbMap(object.getIdToStudents(),
                    // convertor));

                    // builder.addAllStudents(PbConvertorUtils.convertToPbList(object.getStudents(),
                    // convertor));

                    // Field{name=students, modifier=repeated, typeName=StudentPB, tag=2,
                    // options=DynamicMessage{fields={}}}

                    String className = NameUtils.originClassName(field.getTypeName());
                    String convertorClassName = NameUtils.convertorClassName(className);

                    String createConvertor = String.format("    %s convertor = new %s();\n", convertorClassName, convertorClassName);
                    String callConvertor = String.format("    builder.%s(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbList(object.%s(), convertor));",
                            NameUtils.addAllMethodName(field.getName()), NameUtils.getterMethod(field, true));
                    return "{\n" + createConvertor + callConvertor + "\n}";
//                    return String.format("%s %s %s = %d", field.getModifier(), field.getType().getCanonicalName(),
//                            field.getName(), field.getTag());

                } else if (field.isRepeated() && field.getTypeName().endsWith("_entry")) {
                    Message type = (Message) field.getType();

                    Field keyField = type.getField("key");
                    Field valueField = type.getField("value");
                    String keyType = keyField.getTypeName();
                    String valueType = valueField.getTypeName();
                    
                    String className = NameUtils.originClassName(valueField.getTypeName());
                    String convertorClassName = NameUtils.convertorClassName(className);

                    String createConvertor = String.format("    %s convertor = new %s();\n", convertorClassName, convertorClassName);
                    String callConvertor = String.format("    builder.%s(com.alibaba.protokit.gen.PbConvertorUtils.convertToPbMap(object.%s(), convertor));",
                            NameUtils.putAllMethodName(field.getName()), NameUtils.getterMethod(field, true));
                    return "{\n" + createConvertor + callConvertor + "\n}";
                    
                } else {
                    String sss = "builder.%s(object.%s());";
                    return String.format(sss, NameUtils.setterMethod(field), NameUtils.getterMethod(field, true));
                }
            } else if ("from".equals(formatString)) { // from method
                String pkg = proto.getPackage().getValue();
                if (field.isRepeated() && (!field.getTypeName().endsWith("_entry"))) {

                    // StudentPbConvertor convertor = new StudentPbConvertor();
                    // object.setStudents(PbConvertorUtils.fromPbList(fromPbObject.getStudentsList(), convertor));
                    
                    String className = NameUtils.originClassName(field.getTypeName());
                    String convertorClassName = NameUtils.convertorClassName(className);

                    String createConvertor = String.format("    %s convertor = new %s();\n", convertorClassName, convertorClassName);
                    String callConvertor = String.format("    object.%s(com.alibaba.protokit.gen.PbConvertorUtils.fromPbList(fromPbObject.%s(), convertor));",
                            NameUtils.setterMethod(field), NameUtils.getPbListMethodName(field.getName()));
                    return "{\n" + createConvertor + callConvertor + "\n}";
                } else if (field.isRepeated() && field.getTypeName().endsWith("_entry")) {
                    Message type = (Message) field.getType();

                    Field keyField = type.getField("key");
                    Field valueField = type.getField("value");
                    String keyType = keyField.getTypeName();
                    String valueType = valueField.getTypeName();
                    
                    String className = NameUtils.originClassName(valueField.getTypeName());
                    String convertorClassName = NameUtils.convertorClassName(className);

                    String createConvertor = String.format("    %s convertor = new %s();\n", convertorClassName, convertorClassName);
                    String callConvertor = String.format("    object.%s(com.alibaba.protokit.gen.PbConvertorUtils.fromPbMap(fromPbObject.%s(), convertor));",
                            NameUtils.setterMethod(field), NameUtils.getPbMapMethodName(field.getName()));
                    return "{\n" + createConvertor + callConvertor + "\n}";
                } else {
                    String sss = "object.%s(fromPbObject.%s());";
                    return String.format(sss, NameUtils.setterMethod(field), NameUtils.getterMethod(field, false));
                }
            }

            return field.toString();
        }
    }

}
