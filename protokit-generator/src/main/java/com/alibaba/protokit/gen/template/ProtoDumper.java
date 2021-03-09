package com.alibaba.protokit.gen.template;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.alibaba.protokit.annotation.PbAnnotationParser;
import com.alibaba.protokit.utils.NameUtils;

import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Proto;

/**
 * 
 * @author hengyunabc 2021-01-22
 *
 */
public class ProtoDumper {

    public static void dump(List<Class<?>> classList, String absolutePath) {
        // TODO Auto-generated method stub
        
    }
    
    public static String dumpMessage(Class<?> clazz) throws IOException {
        String filePath = clazz.getCanonicalName().replace(".", "/") + ".proto";
        PbAnnotationParser pbAnnotationParser = new PbAnnotationParser();
        Optional<Proto> protoOptional = pbAnnotationParser.parse(clazz);

        Proto proto = protoOptional.get();
        STGroup group = new STGroupFile("proto3.stg");
        group.registerRenderer(Field.class, new FieldRenderer(proto, group));

        ST st = group.getInstanceOf("proto_compiler_template");
        st.add("proto", proto);
        String result = st.render();
        return result;
    }

    public static void dumpMessage(Class<?> clazz, String outputDir) throws IOException {
        String filePath = NameUtils.pbFileName(clazz);

        String result = dumpMessage(clazz);

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

            String pkg = proto.getPackage().getValue();
            if (field.isRepeated() && (!field.getTypeName().endsWith("_entry"))) {
                return String.format("%s %s %s = %d", field.getModifier(), field.getType().getCanonicalName(), field.getName(),
                        field.getTag());
            } else if (field.isRepeated() && field.getTypeName().endsWith("_entry")) {
                Message type = (Message) field.getType();

                Field keyField = type.getField("key");
                Field valueField = type.getField("value");
                String keyType = keyField.getTypeName();
                String valueType = valueField.getTypeName();
                return String.format("map<%s, %s> %s = %d", keyType, valueType, field.getName(), field.getTag());
            } else {
                return String.format("%s %s = %d", field.getTypeName(), field.getName(), field.getTag());
            }

        }
    }

}
