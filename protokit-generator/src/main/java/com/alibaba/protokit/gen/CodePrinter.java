package com.alibaba.protokit.gen;

import java.io.*;

public class CodePrinter {
    private String packagePrefix = "";
    private PrintWriter pw;
    private String indent = "";

    public CodePrinter(PrintWriter pw) {
        this.pw = pw;
    }

    public CodePrinter(File file) throws FileNotFoundException {
        this.pw = new PrintWriter(file);
    }

    public CodePrinter(String file) throws FileNotFoundException {
        this.pw = new PrintWriter(file);
    }

    public void indent() {
        indent += "  ";
    }

    public void outdent() {
        indent = indent.substring(0, indent.length() - 2);
    }

    public void println() {
        pw.println();
    }

    public void println(String lines) {
        for (String line : lines.split("\n")) {
            pw.print(indent);
            pw.println(line);
        }
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public void close() {
        this.pw.close();
    }
}
