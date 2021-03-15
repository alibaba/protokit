package com.alibaba.protokit.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.alibaba.protokit.annotation.Type;

import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.ScalarFieldType;

/**
 * 
 * @author hengyunabc 2021-02-04
 *
 */
public class TypeUtilsTest {

    @Test
    public void test() {
        Assertions.assertThat(TypeUtils.toFieldType(Type.BOOL)).isEqualTo(ScalarFieldType.BOOL);
        Assertions.assertThat(TypeUtils.toFieldType(Type.FIXED32)).isEqualTo(ScalarFieldType.FIXED32);
    }

}
