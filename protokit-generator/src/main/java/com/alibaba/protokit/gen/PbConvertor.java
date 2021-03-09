package com.alibaba.protokit.gen;

import com.google.protobuf.Message;

// TODO 能支持 <? extends Message> 这样子的泛型不？
public interface PbConvertor<T> {
    <T2> T2 convertToPbObject(T object);
    T fromPbObject(Object pbObject);
}
