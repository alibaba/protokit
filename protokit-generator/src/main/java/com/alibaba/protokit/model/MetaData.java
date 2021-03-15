package com.alibaba.protokit.model;

import java.util.List;

import io.protostuff.compiler.model.Proto;

/**
 * 
 * @author hengyunabc 2021-03-10
 *
 */
public class MetaData {

    private String className;
    private String pbClassName;
    private String packageName;
    private List<String> imports;
    private Proto proto;

    // 要 import 其它的类？ 在生成的 java代码里
    // 要有一些 field 的信息？
    // 可能还有一些转换类的信息！
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public Proto getProto() {
        return proto;
    }

    public void setProto(Proto proto) {
        this.proto = proto;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPbClassName() {
        return pbClassName;
    }

    public void setPbClassName(String pbClassName) {
        this.pbClassName = pbClassName;
    }
}
