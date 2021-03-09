package com.alibaba.protokit.utils;

import com.alibaba.protokit.common.Constants;

/**
 * 
 * @author hengyunabc 2021-02-08
 *
 */
public class NameUtils {

    public static String pbClassName(Class<?> clazz) {
        return clazz.getSimpleName() + Constants.PB_MESSAGE_SUFFIX;
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

}
