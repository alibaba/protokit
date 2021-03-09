package com.alibaba.protokit.annotation;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;

/**
 * 
 * 
 * @see com.google.protobuf.Descriptors.FieldDescriptor.Type
 * 
 * @author hengyunabc 2021-01-18
 *
 */
public enum Type {
    DOUBLE(JavaType.DOUBLE), FLOAT(JavaType.FLOAT), INT64(JavaType.LONG), UINT64(JavaType.LONG), INT32(JavaType.INT),
    FIXED64(JavaType.LONG), FIXED32(JavaType.INT), BOOL(JavaType.BOOLEAN), STRING(JavaType.STRING),
    GROUP(JavaType.MESSAGE), MESSAGE(JavaType.MESSAGE), BYTES(JavaType.BYTE_STRING), UINT32(JavaType.INT),
    ENUM(JavaType.ENUM), SFIXED32(JavaType.INT), SFIXED64(JavaType.LONG), SINT32(JavaType.INT), SINT64(JavaType.LONG),
    UNKNOWN(JavaType.UNKNOWN);

    Type(final JavaType javaType) {
        this.javaType = javaType;
    }

    private JavaType javaType;

    public FieldDescriptorProto.Type toProto() {
        return FieldDescriptorProto.Type.forNumber(ordinal() + 1);
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public static Type valueOf(final FieldDescriptorProto.Type type) {
        return values()[type.getNumber() - 1];
    }
}