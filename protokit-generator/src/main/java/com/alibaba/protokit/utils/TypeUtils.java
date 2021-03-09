package com.alibaba.protokit.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.protokit.annotation.Type;

import io.protostuff.compiler.model.FieldType;
import io.protostuff.compiler.model.ScalarFieldType;

/**
 * 
 * @author hengyunabc 2021-02-04
 *
 */
public class TypeUtils {

    private static Map<Type, FieldType> typeToFieldTypeMap = new HashMap<>();
    private static Map<FieldType, Type> fieldToTypeMap = new HashMap<>();

    static {
        typeToFieldTypeMap.put(Type.BOOL, ScalarFieldType.BOOL);

        for (ScalarFieldType scalarFieldType : ScalarFieldType.values()) {
            for (Type type : Type.values()) {
                if (scalarFieldType.name().equals(type.name())) {
                    typeToFieldTypeMap.put(type, scalarFieldType);
                    fieldToTypeMap.put(scalarFieldType, type);
                }
            }
        }

    }

    public static FieldType toFieldType(Type type) {
        // TODO 没对上的类型怎么处理？
        return typeToFieldTypeMap.get(type);
    }

}
