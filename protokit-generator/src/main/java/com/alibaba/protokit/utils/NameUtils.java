package com.alibaba.protokit.utils;

import com.alibaba.protokit.common.Constants;

import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.FieldType;

/**
 * 
 * @author hengyunabc 2021-02-08
 *
 */
public class NameUtils {

    public static String pbClassName(Class<?> clazz) {
        return clazz.getSimpleName() + Constants.PB_MESSAGE_SUFFIX;
    }

    public static String pbFieldTypeName(Class<?> clazz) {
        return clazz.getCanonicalName() + Constants.PB_MESSAGE_SUFFIX;
    }

    public static String originClassName(String pbClassName) {
        if (pbClassName.endsWith(Constants.PB_MESSAGE_SUFFIX)) {
            return pbClassName.substring(0, pbClassName.length() - Constants.PB_MESSAGE_SUFFIX.length());
        }
        return pbClassName;
    }

    public static String pbFullyQualifiedName(Class<?> clazz) {
        return "." + clazz.getCanonicalName() + Constants.PB_MESSAGE_SUFFIX;
    }

    public static String pbFileName(Class<?> clazz) {
        return clazz.getCanonicalName().replace(".", "/") + Constants.PB_MESSAGE_SUFFIX + Constants.PB_FILE_EXT;
    }

    public static String pbFileName(String pbTypeName) {
        return pbTypeName.replace(".", "/") + Constants.PB_FILE_EXT;
    }

    public static String convertorFileName(Class<?> clazz) {
        return clazz.getCanonicalName().replace(".", "/") + Constants.CONVERTOR_SUFFIX + Constants.JAVA_FILE_EXT;
    }

    public static String convertorClassName(String className) {
        // 判断原始类型，再返回对应的
        if (className.equals("int32")) {
            return "com.alibaba.protokit.gen.IntegerPbConvertor";
        } else {
            // Student -> StudentPbConvertor
            return className + Constants.CONVERTOR_SUFFIX;
        }
    }

    public static String upperFirstChar(String name) {
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    // pb类里 list field的函数
    public static String putAllMethodName(String fieldName) {
        return "putAll" + upperFirstChar(fieldName);
    }

    // builder.addAllStudents
    public static String addAllMethodName(String fieldName) {
        return "addAll" + upperFirstChar(fieldName);
    }

    // schoolPb.getStudentsList
    public static String getPbListMethodName(String fieldName) {
        return "get" + upperFirstChar(fieldName) + "List";
    }

    // schoolPb.getIdToStudentsMap();
    public static String getPbMapMethodName(String fieldName) {
        return "get" + upperFirstChar(fieldName) + "Map";
    }

    public static String getterMethod(Field field, boolean checkBool) {
        String name = field.getName();
        String startPart = name.substring(0, 1).toUpperCase();
        if (checkBool && "bool".equals(field.getTypeName())) {
            return "is" + startPart + name.substring(1);
        }
        return "get" + startPart + name.substring(1);
    }

    public static String setterMethod(Field field) {
        String name = field.getName();
        String startPart = name.substring(0, 1).toUpperCase();
        return "set" + startPart + name.substring(1);
    }
}
