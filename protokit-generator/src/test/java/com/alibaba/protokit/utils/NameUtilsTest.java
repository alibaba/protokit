package com.alibaba.protokit.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.protostuff.compiler.model.Field;

/**
 * 
 * @author hengyunabc 2021-02-04
 *
 */
public class NameUtilsTest {

    @Test
    public void testGetterSetter() {
        Field field = new Field(null);
        field.setName("abc");
        Assertions.assertThat(NameUtils.getterMethod(field, true)).isEqualTo("getAbc");
        Assertions.assertThat(NameUtils.setterMethod(field)).isEqualTo("setAbc");

        Field boolField = new Field(null);
        boolField.setName("bbb");
        boolField.setTypeName("bool");
        Assertions.assertThat(NameUtils.getterMethod(boolField, true)).isEqualTo("isBbb");
        Assertions.assertThat(NameUtils.setterMethod(boolField)).isEqualTo("setBbb");
    }
}
