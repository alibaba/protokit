package com.alibaba.protokit.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author hengyunabc 2021-03-10
 *
 */
public class PbConvertorUtils {
    // List<Student>
    public static List convertToPbList(java.lang.Iterable values, PbConvertor convertor) {
        if (values == null) {
            return Collections.emptyList();
        }
        List list = new ArrayList<>();
        for (Object object : values) {
            list.add(convertor.convertToPbObject(object));
        }
        return list;
    }

    // List<StudentPB>
    public static List fromPbList(java.lang.Iterable values, PbConvertor convertor) {
        if (values == null) {
            return Collections.emptyList();
        }
        List list = new ArrayList<>();
        for (Object object : values) {
            list.add(convertor.fromPbObject(object));
        }
        return list;
    }

    // Map<Integer, Student>
    public static Map convertToPbMap(Map map, PbConvertor convertor) {
        if (map == null) {
            return Collections.emptyMap();
        }
        Map result = new HashMap();

        Set entrySet = map.entrySet();
        Iterator iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object value = entry.getValue();
            Object convertToPbObject = convertor.convertToPbObject(value);
            result.put(entry.getKey(), convertToPbObject);
        }

        return result;
    }

    // Map<Integer, StudentPB>
    public static Map fromPbMap(Map map, PbConvertor convertor) {
        if (map == null) {
            return Collections.emptyMap();
        }
        Map result = new HashMap();
        Set entrySet = map.entrySet();
        Iterator iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object value = entry.getValue();
            Object fromPbObject = convertor.fromPbObject(value);
            result.put(entry.getKey(), fromPbObject);
        }
        return result;
    }
}
