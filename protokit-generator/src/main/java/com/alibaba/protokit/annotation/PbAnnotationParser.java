package com.alibaba.protokit.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.protokit.model.MetaData;
import com.alibaba.protokit.utils.NameUtils;

import io.protostuff.compiler.model.DynamicMessage.Value;
import io.protostuff.compiler.model.FieldModifier;
import io.protostuff.compiler.model.FieldType;
import io.protostuff.compiler.model.Import;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Package;
import io.protostuff.compiler.model.Proto;
import io.protostuff.compiler.model.ScalarFieldType;
import io.protostuff.compiler.model.Syntax;

/**
 * 从传入的类里解析出 具体的 pb 信息
 * 
 * @author hengyunabc 2021-01-20
 *
 */
public class PbAnnotationParser {
    private static final Logger logger = LoggerFactory.getLogger(PbAnnotationParser.class);

    public Optional<MetaData> parse(Class<?> clazz) {

        MetaData metaData = new MetaData();

        @SuppressWarnings("unchecked")
        Set<Annotation> annotations = ReflectionUtils.getAnnotations(clazz,
                a -> a.annotationType().equals(PbMessage.class));

        if (annotations.isEmpty()) {
            logger.debug("class: {} can not find annotation: {}", clazz, PbMessage.class);
            return Optional.empty();
        }

        String fileName = NameUtils.pbFileName(clazz);

        String packageName = clazz.getPackage().getName();

        Proto proto = new Proto();
        proto.setFilename(fileName);
        proto.setSyntax(new Syntax(proto, "proto3"));
        Package pkg = new Package(proto, packageName);
        proto.setPackage(pkg);

        metaData.setClassName(clazz.getSimpleName());
        metaData.setPbClassName(NameUtils.pbClassName(clazz));
        metaData.setPackageName(packageName);

        Message message = new Message(proto);
        message.setName(NameUtils.pbClassName(clazz));
        proto.addMessage(message);

        // options
        // option java_package = "com.aaa";
        // option java_multiple_files = true;
        proto.getOptions().set("java_package", Value.createString(packageName));
        proto.getOptions().set("java_multiple_files", Value.createBoolean(true));

        Set<Field> fields = ReflectionUtils.getFields(clazz, f -> f.getAnnotationsByType(PbField.class).length > 0);
        List<Field> fieldList = new ArrayList<>(fields);
        Collections.sort(fieldList, new Comparator<Field>() {
            @Override
            public int compare(Field f1, Field f2) {
                return f1.getAnnotationsByType(PbField.class)[0].tag()
                        - f2.getAnnotationsByType(PbField.class)[0].tag();
            }
        });

        for (Field field : fieldList) {
            Class<?> fieldType = field.getType();
            java.lang.reflect.Type genericType = field.getGenericType();

            PbField pbFieldAnnotation = field.getAnnotationsByType(PbField.class)[0];

            if (genericType instanceof ParameterizedType) {
                java.lang.reflect.Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();

                Class<?> rawType = (Class<?>) ((ParameterizedType) genericType).getRawType();

                if (Collection.class.isAssignableFrom(rawType)) {

                    FieldType pbType = toPbType((Class<?>) actualTypes[0]);
                    addImport(proto, pbType.getCanonicalName());
                    io.protostuff.compiler.model.Field pbField = new io.protostuff.compiler.model.Field(message);
                    pbField.setTag(pbFieldAnnotation.tag());
                    pbField.setName(field.getName());
                    pbField.setComments(Arrays.asList(pbFieldAnnotation.comment()));
                    pbField.setType(pbType);
                    pbField.setTypeName(pbType.getName());
                    pbField.setModifier(FieldModifier.REPEATED);
                    message.addField(pbField);

                } else if (Map.class.isAssignableFrom(rawType)) {

                    Message mapMessage = mapFieldToMessage(field, actualTypes);
                    io.protostuff.compiler.model.Field pbField = new io.protostuff.compiler.model.Field(message);
                    pbField.setTag(pbFieldAnnotation.tag());
                    pbField.setName(field.getName());
                    pbField.setComments(Arrays.asList(pbFieldAnnotation.comment()));
                    pbField.setType(mapMessage);
                    pbField.setTypeName(mapMessage.getName());
                    pbField.setModifier(FieldModifier.REPEATED);
                    message.addField(pbField);

                    message.addMessage(mapMessage);
                } else {
                    String errMsg = "can not process class: " + clazz.getName() + ", field: " + field.getName();
                    throw new UnsupportedOperationException(errMsg);
                }
            } else {
                FieldType pbType = toPbType(fieldType);
                addImport(proto, pbType.getCanonicalName());

                io.protostuff.compiler.model.Field pbField = new io.protostuff.compiler.model.Field(message);
                pbField.setTag(pbFieldAnnotation.tag());
                pbField.setName(field.getName());
                pbField.setComments(Arrays.asList(pbFieldAnnotation.comment()));
                pbField.setType(pbType);
                pbField.setTypeName(pbType.getName());
                message.addField(pbField);

            }

        }

        metaData.setProto(proto);
        return Optional.of(metaData);
    }

    private FieldType toPbType(Class<?> clazz) {
        if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.DOUBLE;
        } else if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.FLOAT;
        } else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.INT64;
        } else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.INT32;
        } else if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.BOOL;
        } else if (String.class.isAssignableFrom(clazz)) {
            return ScalarFieldType.STRING;
        } else if (clazz.isEnum()) {
            return null;
        } else {
            // TODO Message?
            // 对于已经是PB的类，要处理，比如 List<StudentPB>
            Message message = new Message(null);
            message.setName(NameUtils.pbFieldTypeName(clazz));
            message.setFullyQualifiedName(NameUtils.pbFullyQualifiedName(clazz));
            return message;
        }
    }

    private Message mapFieldToMessage(Field field, java.lang.reflect.Type[] actualTypes) {
//        messages=[
//        Message{name=nameToStudents_entry, fullyQualifiedName=.com.aaa.SchoolPB.nameToStudents_entry, 
//        fields=[
//            Field{name=key, modifier=optional, typeName=string, tag=1, options=DynamicMessage{fields={}}}, 
//            Field{name=value, modifier=optional, typeName=StudentPB, tag=2, options=DynamicMessage{fields={}}}], 
//        options=DynamicMessage{fields={map_entry=true}}}, 
//        
        Message message = new Message(null);
        message.setName(field.getName() + "_entry");
        String fieldPackage = field.getDeclaringClass().getPackage().getName();
        message.setFullyQualifiedName("." + fieldPackage + "." + field.getName() + "_entry");
        io.protostuff.compiler.model.Field key = new io.protostuff.compiler.model.Field(message);
        key.setName("key");
        key.setType(toPbType((Class<?>) actualTypes[0]));
        key.setTypeName(key.getType().getName());
        key.setTag(1);
        io.protostuff.compiler.model.Field value = new io.protostuff.compiler.model.Field(message);
        value.setName("value");
        value.setType(toPbType((Class<?>) actualTypes[1]));
        value.setTypeName(value.getType().getCanonicalName());
        value.setTag(2);

        message.getOptions().set("map_entry", Value.createBoolean(true));

        message.addField(key);
        message.addField(value);
        return message;
    }

    private void addImport(Proto proto, String pbTypeName) {
        // 排掉pb本身的类型
        if (ScalarFieldType.getByName(pbTypeName) != null) {
            return;
        }
        Import imp = new Import(proto, NameUtils.pbFileName(pbTypeName), false);
        List<Import> imports = proto.getImports();
        if (!imports.contains(imp)) {
            imports.add(imp);
        }
    }
}
