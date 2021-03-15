# protokit
A  kit for protobuf.


## 示例
### protokit-demo

在 `protokit-demo` 里可以看到具体示例。

用户定义：

```java
@PbMessage
public class User {
    @PbField(tag = 1)
    private long id;

    @PbField(tag = 2)
    private String name;
    @PbField(tag = 3)
    private int age;

    @PbField(tag = 4)
    private boolean checked;
```

在编译之后，会生成 `UserPB.proto`：

```js
syntax = "proto3";
package com.test.demo;
option java_multiple_files = true;
option java_package = 'com.test.demo';
message UserPB {
    int64 id = 1;
    string name = 2;
    int32 age = 3;
    bool checked = 4;
}
```

还会生成 `UserPbConvertor.java`

```java
public class UserPbConvertor implements com.alibaba.protokit.gen.PbConvertor<com.test.demo.User>{

    @Override
    public <T> T convertToPbObject(com.test.demo.User object) {
        com.test.demo.UserPB.Builder builder = com.test.demo.UserPB.newBuilder();
        builder.setId(object.getId());
        builder.setName(object.getName());
        builder.setAge(object.getAge());
        builder.setChecked(object.isChecked());
        return (T) builder.build();
    }
    @Override
    public com.test.demo.User fromPbObject(Object pbObject) {
        com.test.demo.User object = new com.test.demo.User();
        com.test.demo.UserPB fromPbObject = (com.test.demo.UserPB) pbObject;
        object.setId(fromPbObject.getId());
        object.setName(fromPbObject.getName());
        object.setAge(fromPbObject.getAge());
        object.setChecked(fromPbObject.getChecked());
        return object;
    }
}
```

### protokit-demo2

在 `protokit-demo2` 里引用 `protokit-demo` 中生成的 `UserPbConvertor`，完成 `User` 到 `UserPB`的相互转换。



