package com.alibaba.protokit.gen;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class CodeDumperTest {
    public static void main(String[] args) throws IOException {
        Class<?> interfaceClass = CartService.class;
        String outputDir = Files.createTempDirectory(null).toString();
        System.out.println("ourputDir=" + outputDir);

        CodeDumper.dump(Arrays.asList(interfaceClass), outputDir);
    }

}
