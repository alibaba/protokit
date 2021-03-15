package com.alibaba.protokit.gen;

/**
 * 
 * @author hengyunabc 2021-03-15
 *
 */
public class IntegerPbConvertor implements PbConvertor<Integer> {

    @Override
    public <T2> T2 convertToPbObject(Integer object) {
        return (T2) object;
    }

    @Override
    public Integer fromPbObject(Object pbObject) {
        return (Integer) pbObject;
    }

}