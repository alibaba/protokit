package com.alibaba.protokit.annotation;

import com.google.protobuf.ByteString;

/**
 * @see com.google.protobuf.Descriptors.FieldDescriptor.JavaType
 * @author hengyunabc 2021-01-18
 *
 */
public enum JavaType {
    INT(0), LONG(0L), FLOAT(0F), DOUBLE(0D), BOOLEAN(false), STRING(""), BYTE_STRING(ByteString.EMPTY), ENUM(null),
    MESSAGE(null), UNKNOWN(null);

    JavaType(final Object defaultDefault) {
        this.defaultDefault = defaultDefault;
    }

    /**
     * The default default value for fields of this type, if it's a primitive type.
     * This is meant for use inside this file only, hence is private.
     */
    private final Object defaultDefault;
}