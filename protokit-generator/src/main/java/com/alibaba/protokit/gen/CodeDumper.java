package com.alibaba.protokit.gen;

import com.google.protobuf.DescriptorProtos;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO enum需要添加前缀
public class CodeDumper {
    public static void dump(List<Class<?>> interfaceClass, String outputDir) throws IOException {
        MetadataBuilder gen = new MetadataBuilder();
        for (Class<?> clazz : interfaceClass) {
            gen.processService(clazz);
        }
        gen.processTypes();

        Map<String, Set<String>> imports = gen.getImports();

        for (Map.Entry<String, DescriptorProtos.FileDescriptorProto> entry : gen.getFileProtoMap().entrySet()) {
            String filePath = entry.getKey();
            DescriptorProtos.FileDescriptorProto fileDescriptor = entry.getValue();
            File file = new File(outputDir, filePath);
            FileUtils.forceMkdirParent(file);
            CodePrinter codePrinter = new CodePrinter(file);
            codePrinter.println("syntax = \"" + fileDescriptor.getSyntax() + "\";");
            codePrinter.println();

            printOptions(fileDescriptor.getOptions(), codePrinter);
            codePrinter.println();

            if (StringUtils.isNotEmpty(fileDescriptor.getPackage())) {
                codePrinter.println("package " + fileDescriptor.getPackage() + ";");
                codePrinter.setPackagePrefix("." + fileDescriptor.getPackage() + ".");
                codePrinter.println();
            }
            Set<String> importSet = imports.get(filePath);
            if (importSet != null) {
                for (String anImport : importSet) {
                    codePrinter.println("import \"" + anImport + "\";");
                }
            }

            printServices(fileDescriptor.getServiceList(), codePrinter);
            codePrinter.println();

            printMessages(fileDescriptor.getMessageTypeList(), codePrinter);
            codePrinter.println();

            codePrinter.close();
        }

    }

    private static void printMessages(List<DescriptorProtos.DescriptorProto> messageTypeList, CodePrinter codePrinter) {
        for (DescriptorProtos.DescriptorProto message : messageTypeList) {
            printMessage(message, codePrinter);
        }
    }

    private static void printMessage(DescriptorProtos.DescriptorProto message, CodePrinter codePrinter) {
        printMessage(message, codePrinter, codePrinter.getPackagePrefix());
    }

    private static void printMessage(DescriptorProtos.DescriptorProto message, CodePrinter codePrinter, String packagePrefix) {
        codePrinter.println("message " +
                StringUtils.removeStart(message.getName(), packagePrefix)
                + " {");
        codePrinter.indent();
        for (DescriptorProtos.DescriptorProto nestedType : message.getNestedTypeList()) {
            if (!nestedType.getOptions().getMapEntry()) {
                printMessage(
                        nestedType, codePrinter,
                        message.getName() + "."
                );
            }
        }

        for (DescriptorProtos.FieldDescriptorProto field : message.getFieldList()) {
            printField(field, message, codePrinter, packagePrefix);
        }
        codePrinter.outdent();
        codePrinter.println("}");
        codePrinter.println();
    }

    private static void printField(
            DescriptorProtos.FieldDescriptorProto field,
            DescriptorProtos.DescriptorProto message,
            CodePrinter codePrinter,
            String packagePrefix
    ) {
        // Map 判断
        String fieldName = field.getName();
        DescriptorProtos.DescriptorProto entry = null;
        for (DescriptorProtos.DescriptorProto subType : message.getNestedTypeList()) {
            if (field.getLabel().equals(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED)
                    && getSimpleNameFromCanonicalName(subType.getName()).equals(StringUtils.capitalize(fieldName) + "Entry")
                    && subType.getOptions().getMapEntry()) {
                entry = subType;
                break;
            }
        }
        if (entry == null) {
            if (field.getType().equals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)) {
                String code = "";
                if (field.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) {
                    code += "repeated ";
                }
                codePrinter.println(code +
                        StringUtils.removeStart(field.getTypeName(), packagePrefix) +
                        " " + field.getName() +
                        " = " + field.getNumber() + ";"
                );
            } else {
                String code = "";
                if (field.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) {
                    code += "repeated ";
                }
                codePrinter.println(code + typeToString(field.getType()) +
                        " " + field.getName() +
                        " = " + field.getNumber() + ";"
                );
            }
        } else {
            for (DescriptorProtos.DescriptorProto nestedType : entry.getNestedTypeList()) {
                printMessage(nestedType, codePrinter, StringUtils.substringBeforeLast(entry.getName(), ".") + ".");
            }
            DescriptorProtos.FieldDescriptorProto keyField = getByName(entry, "key");
            String keyTypeString;
            if (keyField.getType() == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE) {
                keyTypeString = keyField.getTypeName();
            } else {
                keyTypeString = typeToString(keyField.getType());
            }
            DescriptorProtos.FieldDescriptorProto valueField = getByName(entry, "value");
            DescriptorProtos.FieldDescriptorProto.Type valueType = valueField.getType();
            String valueTypeString = "";
            if (valueType.equals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)) {
                if (valueField.getTypeName().startsWith(packagePrefix)) {
                    valueTypeString = StringUtils.substringAfterLast(valueField.getTypeName(), ".");
                } else {
                    valueTypeString = valueField.getTypeName();
                }
            } else {
                valueTypeString = typeToString(valueType);
            }
            // TODO 依赖文件还没有处理好
            codePrinter.println(
                    "map<" + keyTypeString + ", " + valueTypeString + "> " +
                            field.getName() + " = " + field.getNumber() + ";");
        }
    }

    private static String typeToString(DescriptorProtos.FieldDescriptorProto.Type type) {
        switch (type) {
            case TYPE_INT32:
                return "int32";
            case TYPE_INT64:
                return "int64";
            case TYPE_STRING:
                return "string";
            case TYPE_BOOL:
                return "bool";
            default:
                return "<unknown>";
        }
    }

    private static String getSimpleNameFromCanonicalName(String canonicalName) {
        String simpleName = canonicalName;
        int lIndex = StringUtils.lastIndexOf(canonicalName, ".");
        if (lIndex != -1) {
            simpleName = StringUtils.substring(canonicalName, lIndex + 1);
        }
        return simpleName;
    }

    private static void printServices(List<DescriptorProtos.ServiceDescriptorProto> serviceList, CodePrinter codePrinter) {
        for (DescriptorProtos.ServiceDescriptorProto serviceDescriptorProto : serviceList) {
            printService(serviceDescriptorProto, codePrinter);
        }
    }

    private static void printService(DescriptorProtos.ServiceDescriptorProto service, CodePrinter codePrinter) {
        codePrinter.println("service " + service.getName() + " {");
        codePrinter.indent();
        for (DescriptorProtos.MethodDescriptorProto methodDescriptorProto : service.getMethodList()) {
            printMethod(methodDescriptorProto, codePrinter);
        }
        codePrinter.outdent();
        codePrinter.println("}");
        codePrinter.println();
    }

    private static void printMethod(DescriptorProtos.MethodDescriptorProto method, CodePrinter codePrinter) {
        StringBuilder sb = new StringBuilder();
        sb.append("rpc ")
                .append(method.getName())
                .append("(")
                .append(StringUtils.removeStart(method.getInputType(), codePrinter.getPackagePrefix()))
                .append(")");
        codePrinter.println(sb.toString());
        codePrinter.indent();
        sb = new StringBuilder();
        sb.append("returns (")
                .append(StringUtils.removeStart(method.getOutputType(), codePrinter.getPackagePrefix()))
                .append(")").append("{};");
        codePrinter.println(sb.toString());
        codePrinter.outdent();
    }

    private static void printOptions(DescriptorProtos.FileOptions options, CodePrinter codePrinter) {
        if (StringUtils.isNotEmpty(options.getJavaPackage())) {
            codePrinter.println("option java_package = \"" + options.getJavaPackage() + "\";");
        }
//        if (options.hasOptimizeFor()) {
//            codePrinter.println("option optimize_for = " + options.getOptimizeFor() + ";");
//        }

        codePrinter.println("option java_multiple_files = " + options.getJavaMultipleFiles() + ";");
    }

    private static DescriptorProtos.FieldDescriptorProto getByName(
            DescriptorProtos.DescriptorProto mapEntry, String name
    ) {
        for (DescriptorProtos.FieldDescriptorProto field : mapEntry.getFieldList()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }
}
