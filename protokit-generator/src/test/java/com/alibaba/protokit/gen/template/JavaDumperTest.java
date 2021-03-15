package com.alibaba.protokit.gen.template;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.aaa.School;
import com.abc.Student;

/**
 * 
 * @author hengyunabc 2021-03-09
 *
 */
public class JavaDumperTest {
    @Test
    public void test() throws IOException {
        
        String file = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String outputPath = new File(file, "../test-output").getAbsolutePath();

        JavaDumper.dumpCode(Student.class, outputPath);
        JavaDumper.dumpCode(School.class, outputPath);

        String studentCodeString = JavaDumper.dumpCode(Student.class);
        
        System.err.println(studentCodeString);

        String schoolCodeString = JavaDumper.dumpCode(School.class);
        
        System.err.println(schoolCodeString);
    }
}
