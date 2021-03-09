package com.alibaba.protokit.gen.template;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.aaa.School;
import com.abc.Student;
import com.alibaba.protokit.utils.NameUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.protostuff.compiler.ParserModule;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Proto;
import io.protostuff.compiler.parser.Importer;
import io.protostuff.compiler.parser.LocalFileReader;
import io.protostuff.compiler.parser.ProtoContext;

/**
 * 
 * @author hengyunabc 2021-01-22
 *
 */
public class ProtoDumperTest {

    @Test
    public void test() throws IOException {
        String file = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String outputPath = new File(file, "../test-output").getAbsolutePath();

        ProtoDumper.dumpMessage(Student.class, outputPath);
        ProtoDumper.dumpMessage(School.class, outputPath);

        String studentPbString = ProtoDumper.dumpMessage(Student.class);

        studentPbString = ProtoDumper.dumpMessage(School.class);

        ProtoDumper.dumpMessage(School.class, outputPath);

        Injector injector = Guice.createInjector(new ParserModule());
        Importer importer = injector.getInstance(Importer.class);

        System.err.println(studentPbString);

        LocalFileReader localFileReader = new LocalFileReader(new File(outputPath).toPath());

        ProtoContext context = importer.importFile(localFileReader, NameUtils.pbFileName(School.class));
        Proto proto = context.getProto();

        System.err.println(proto);

        Message studentPB = context.resolve(Message.class, "SchoolPB");

        System.err.println(studentPB);

        System.err.println(proto.getMessage("SchoolPB"));
        System.err.println(context.getImports().get(0).getProto().getMessage("StudentPB"));
    }
}
