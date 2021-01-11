package com.alibaba.protokit.gen;

import com.google.protobuf.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class MetadataBuilder {

    private Map<String, DescriptorProtos.FileDescriptorProto> fileProtoMap = new HashMap<>();

    private Map<String, Set<String>> imports = new HashMap<>();

    private Set<String> processedTypes = new HashSet<>();
    private Map<String, Class<?>> toProcessTypes = new HashMap<>();

    public void processService(Class<?> interfaceClass) {
        String fileName = interfaceClass.getCanonicalName().replace(".", "/") + ".proto";
        DescriptorProtos.FileDescriptorProto fileProto = fileProtoMap.get(fileName);
        if (fileProto == null) {
            String packageName = interfaceClass.getPackage().getName();
            DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
                    .setJavaPackage(packageName)
                    .setJavaMultipleFiles(true)
                    .build();

            // message
            DescriptorProtos.ServiceDescriptorProto.Builder serviceProtoBuilder =
                    DescriptorProtos.ServiceDescriptorProto.newBuilder()
                            .setName(interfaceClass.getSimpleName());

            // file
            DescriptorProtos.FileDescriptorProto.Builder fileProtoBuilder =
                    DescriptorProtos.FileDescriptorProto.newBuilder()
                            .setSyntax("proto3")
                            .setName(fileName)
                            .setPackage(packageName)
                            .setOptions(options);

            for (Method declaredMethod : interfaceClass.getDeclaredMethods()) {
                processMethod(declaredMethod, serviceProtoBuilder, fileProtoBuilder, fileName);
            }

            fileProto = fileProtoBuilder.addService(serviceProtoBuilder.build()).build();
            fileProtoMap.put(fileName, fileProto);
        }
    }

    private void processMethod(
            Method declaredMethod,
            DescriptorProtos.ServiceDescriptorProto.Builder serviceProtoBuilder,
            DescriptorProtos.FileDescriptorProto.Builder fileProtoBuilder,
            String fileName
    ) {
        Class<?> interfaceClass = declaredMethod.getDeclaringClass();
        String requestClassName = "." + interfaceClass.getCanonicalName() +
                StringUtils.capitalize(declaredMethod.getName()) + "Request";
        String replyClassName = "." + interfaceClass.getCanonicalName() +
                StringUtils.capitalize(declaredMethod.getName()) + "Reply";

        // 方法本身
        DescriptorProtos.MethodDescriptorProto methodProto = DescriptorProtos.MethodDescriptorProto.newBuilder()
                .setName(declaredMethod.getName())
                .setInputType(requestClassName)
                .setOutputType(replyClassName)
                .setClientStreaming(false)
                .setServerStreaming(false)
                .build();
        serviceProtoBuilder.addMethod(methodProto);

        // 参数 Request
        DescriptorProtos.DescriptorProto.Builder reqProtoBuilder = DescriptorProtos.DescriptorProto.newBuilder()
                .setName(requestClassName);
        Parameter[] parameters = declaredMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            processParameter(parameters[i], i + 1, reqProtoBuilder, fileName);
        }
        fileProtoBuilder.addMessageType(reqProtoBuilder.build());

        // 返回值 Reply
        DescriptorProtos.DescriptorProto.Builder replyProtoBuilder = DescriptorProtos.DescriptorProto.newBuilder()
                .setName(replyClassName);
        Type returnType = declaredMethod.getGenericReturnType();

        processMethodReturn(returnType, replyProtoBuilder, fileName);

        fileProtoBuilder.addMessageType(replyProtoBuilder.build());
    }

    private void processMethodReturn(
            Type returnType,
            DescriptorProtos.DescriptorProto.Builder paramBuilder,
            String fileName
    ) {
        processType(returnType, "returnValue", 1, paramBuilder, fileName, false);
    }

    private void processParameter(
            Parameter parameter,
            int index,
            DescriptorProtos.DescriptorProto.Builder paramBuilder,
            String fileName
    ) {
        Type type = parameter.getParameterizedType();
        String name = parameter.getName();
        processType(type, name, index, paramBuilder, fileName, false);
    }

    /**
     * @param pType          要处理的类型
     * @param name           类型对应的变量名
     * @param index          变量名对应的number
     * @param messageBuilder 变量名所在的message类
     */
    private void processCollection(
            ParameterizedType pType, String name, int index,
            DescriptorProtos.DescriptorProto.Builder messageBuilder,
            String fileName,
            boolean nested
    ) {
        DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName(name);

        Type elemType = pType.getActualTypeArguments()[0];
        if (elemType instanceof ParameterizedType) {
            System.out.println();
        } else if (elemType instanceof Class<?>) {
            Class<?> clazz = (Class<?>) elemType;
            DescriptorProtos.FieldDescriptorProto.Type grpcType = toGrpcType(clazz);
            fieldBuilder.setType(grpcType)
                    .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED);
            if (grpcType == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE) {
                String typeName = clazz.getCanonicalName();
                fieldBuilder.setTypeName("." + typeName);
            }
        }

        DescriptorProtos.FieldDescriptorProto fieldProto = fieldBuilder
                .setName(name)
                .setNumber(index)
                .build();
        messageBuilder.addField(fieldProto);
    }

    private void processMap(
            ParameterizedType pType, String name, int index,
            DescriptorProtos.DescriptorProto.Builder messageBuilder,
            String fileName,
            boolean nested
    ) {
        DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName(name);

        Type[] actualTypes = pType.getActualTypeArguments();
        String entryTypeName = "";
        if (nested) {
            entryTypeName = messageBuilder.getName() + "." +
                    StringUtils.substringAfterLast(messageBuilder.getName(), ".") + "Entry";
        } else {
            entryTypeName = messageBuilder.getName() + "." + StringUtils.capitalize(name) + "Entry";
        }
        // Build Entry
        DescriptorProtos.DescriptorProto.Builder entryTypeBuilder = DescriptorProtos.DescriptorProto.newBuilder()
                .setName(entryTypeName);
        //if (!nested) {
        entryTypeBuilder.setOptions(
                DescriptorProtos.MessageOptions.newBuilder()
                        .setMapEntry(true)
                        .build()
        );
        //}

        fieldBuilder.setTypeName(entryTypeName)
                .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED);

        // key
        DescriptorProtos.FieldDescriptorProto.Builder keyField =
                DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("key")
                        .setNumber(1)
                        .setType(toGrpcType((Class<?>) actualTypes[0]));
        entryTypeBuilder.addField(keyField.build());

        // value
        Type valueType = actualTypes[1];
        if (valueType instanceof ParameterizedType) {
            ParameterizedType valuePType = (ParameterizedType) valueType;
            Class<?> valueRawClass = (Class<?>) valuePType.getRawType();

            String mapTypeName = "";
            String fieldNameInMap = "";
            {
                if (nested) {
                    mapTypeName = StringUtils.capitalize(messageBuilder.getName()) + "." +
                            StringUtils.substringAfterLast(messageBuilder.getName(), ".");
                    fieldNameInMap = StringUtils.uncapitalize(StringUtils.substringAfterLast(messageBuilder.getName(), "."));
                } else {
                    mapTypeName = messageBuilder.getName() + "." + StringUtils.capitalize(name);
                    fieldNameInMap = StringUtils.uncapitalize(name);
                }
                if (Collection.class.isAssignableFrom(valueRawClass)) {
                    mapTypeName += "List";
                    fieldNameInMap += "List";
                } else if (Map.class.isAssignableFrom(valueRawClass)) {
                    mapTypeName += "Map";
                    fieldNameInMap += "Map";
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            // Build sub Map
            DescriptorProtos.DescriptorProto.Builder mapTypeBuilder = DescriptorProtos.DescriptorProto.newBuilder()
                    .setName(mapTypeName);
            // key
            DescriptorProtos.FieldDescriptorProto.Builder valueField =
                    DescriptorProtos.FieldDescriptorProto.newBuilder()
                            .setName("value")
                            .setNumber(2)
                            .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)
                            .setTypeName(mapTypeName);
            processType(valuePType, fieldNameInMap, 1, mapTypeBuilder, fileName, true);

            entryTypeBuilder.addField(valueField.build());

            messageBuilder.addNestedType(mapTypeBuilder.build());
        } else if (valueType instanceof Class) {
            Class<?> valueClass = (Class<?>) valueType;

            toProcessTypes.put(valueClass.getCanonicalName(), valueClass);
            String depFile = classToFileName(valueClass);
            Set<String> importSet = imports.get(fileName);
            if (importSet == null) {
                importSet = new HashSet<>();
            }
            importSet.add(depFile);
            imports.put(fileName, importSet);

            DescriptorProtos.FieldDescriptorProto.Type fieldType = toGrpcType(valueClass);
            DescriptorProtos.FieldDescriptorProto.Builder valueField =
                    DescriptorProtos.FieldDescriptorProto.newBuilder()
                            .setName("value")
                            .setNumber(2)
                            .setType(fieldType);
            if (fieldType.equals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)) {
                valueField.setTypeName("." + valueClass.getCanonicalName());
            }
            entryTypeBuilder.addField(valueField.build());
            fieldBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)
                    .setTypeName(entryTypeName);
        } else {
            throw new UnsupportedOperationException();
        }

        messageBuilder.addNestedType(entryTypeBuilder.build());

        DescriptorProtos.FieldDescriptorProto fieldProto = fieldBuilder
                .setName(name)
                .setNumber(index)
                .build();
        messageBuilder.addField(fieldProto);
    }

    private void processType(
            Type type,
            String name,
            int index,
            DescriptorProtos.DescriptorProto.Builder messageBuilder,
            String fileName,
            boolean nested
    ) {
        DescriptorProtos.FieldDescriptorProto.Type grpcType = null;
        Class<?> clazz = null;

        DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder();

        if (type instanceof ParameterizedType) {
            grpcType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE;
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) pType.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                processCollection(pType, name, index, messageBuilder, fileName, nested);
            } else if (Map.class.isAssignableFrom(rawType)) {
                processMap(pType, name, index, messageBuilder, fileName, nested);
            } else {
                System.err.println(pType.toString());
                throw new UnsupportedOperationException();
            }
        } else if (type instanceof Class) {
            clazz = (Class<?>) type;
            grpcType = toGrpcType(clazz);

            if (grpcType == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE) {
                fieldBuilder.setTypeName("." + clazz.getCanonicalName());
            }
            if (grpcType == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE) {
                toProcessTypes.put(clazz.getCanonicalName(), clazz);
                String depFile = classToFileName(clazz);
                Set<String> importSet = imports.get(fileName);
                if (importSet == null) {
                    importSet = new HashSet<>();
                }
                importSet.add(depFile);
                imports.put(fileName, importSet);
            }
            DescriptorProtos.FieldDescriptorProto fieldProto = fieldBuilder
                    .setName(name)
                    .setNumber(index)
                    .setType(grpcType)
                    .build();
            messageBuilder.addField(fieldProto);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    private DescriptorProtos.FieldDescriptorProto.Type toGrpcType(Class<?> clazz) {
        if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
        } else if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT;
        } else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64;
        } else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        } else if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL;
        } else if (String.class.isAssignableFrom(clazz)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
        } else if (clazz.isEnum()) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM;
        } else {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE;
        }
    }

    private void processMessage(Class<?> messageClass) {
        if (processedTypes.contains(messageClass.getCanonicalName())) {
            return;
        }
        String fileName = messageClass.getCanonicalName().replace(".", "/") + ".proto";

        DescriptorProtos.FileDescriptorProto fileProto = fileProtoMap.get(fileName);
        if (fileProto == null) {
            processedTypes.add(messageClass.getCanonicalName());
            String packageName = messageClass.getPackage().getName();
            DescriptorProtos.FileOptions options = DescriptorProtos.FileOptions.newBuilder()
                    .setJavaPackage(packageName)
                    .setJavaMultipleFiles(true)
                    .build();

            // fields
            Field[] fields = messageClass.getDeclaredFields();
            ArrayList<DescriptorProtos.FieldDescriptorProto> protoFields = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Class<?> fieldClass = field.getType();
                DescriptorProtos.FieldDescriptorProto.Type type = toGrpcType(fieldClass);
                DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder =
                        DescriptorProtos.FieldDescriptorProto.newBuilder()
                                .setType(type)
                                .setName(field.getName())
                                .setNumber(i + 1)
                                .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL);

                if (type == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE) {
                    fieldBuilder.setTypeName("." + fieldClass.getCanonicalName());
                }
                protoFields.add(fieldBuilder.build());
                if (type == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE &&
                        !processedTypes.contains(fieldClass.getCanonicalName())) {
                    toProcessTypes.put(fieldClass.getCanonicalName(), fieldClass);
                    String depFile = classToFileName(fieldClass);
                    Set<String> importSet = imports.get(fileName);
                    if (importSet == null) {
                        importSet = new HashSet<>();
                    }
                    importSet.add(depFile);
                    imports.put(fileName, importSet);
                }
            }

            // message
            DescriptorProtos.DescriptorProto messageProto = DescriptorProtos.DescriptorProto.newBuilder()
                    .setName(messageClass.getSimpleName())
                    .addAllField(protoFields)
                    .build();

            // file
            fileProto = DescriptorProtos.FileDescriptorProto.newBuilder()
                    .setSyntax("proto3")
                    .setName(fileName)
                    .setPackage(packageName)
                    .setOptions(options)
                    .addMessageType(messageProto)
                    .build();

//            int kBytesPerLine = 40;
//            int kLinesPerPart = 400;
//            int kBytesPerPart = kBytesPerLine * kLinesPerPart;
//            ByteString bytes = fileProto.toByteString();
//            StringBuilder code = new StringBuilder();
//            for (int i = 0; i < bytes.size(); i += kBytesPerLine) {
//                if (i > 0) {
//                    if (i % kBytesPerPart == 0) {
//                        code.append(",\n");
//                    } else {
//                        code.append(" +\n");
//                    }
//                }
//                String line = bytes.substring(i, Math.min(i + kBytesPerLine, bytes.size())).toString(Charset.forName("ISO-8859-1"));
//                code.append('"')
//                        .append(StringEscapeUtils.escapeJava(line))
//                        .append('"');
//            }
            fileProtoMap.put(fileName, fileProto);
        }
    }

    public void processTypes() {
        List<String> typeNameList = new ArrayList<>(toProcessTypes.keySet());
        typeNameList.sort(Comparator.naturalOrder());
        for (String typeName : typeNameList) {
            Class<?> messageClass = toProcessTypes.remove(typeName);
            processMessage(messageClass);
            processedTypes.add(typeName);
        }
    }

    public Map<String, DescriptorProtos.FileDescriptorProto> getFileProtoMap() {
        return fileProtoMap;
    }

    public Map<String, Set<String>> getImports() {
        return imports;
    }

    private static String classToFileName(Class<?> clazz) {
        return clazz.getCanonicalName().replace(".", "/")
                + ".proto";
    }

    private static String generateTypeName(Type type, String prefix) {
        String typeName = prefix;
        while (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) pType.getRawType();
            if (List.class.isAssignableFrom(rawType)) {
                typeName += "List";
                type = pType.getActualTypeArguments()[0];
            } else if (Map.class.isAssignableFrom(rawType)) {
                typeName += "Map";
                type = pType.getActualTypeArguments()[1];
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return typeName;
    }
}
