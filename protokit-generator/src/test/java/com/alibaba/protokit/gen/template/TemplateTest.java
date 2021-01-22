package com.alibaba.protokit.gen.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author hengyunabc 2021-01-22
 *
 */
public class TemplateTest {

    @Test
    public void test() throws IOException {
        MustacheTemplateRenderer mustacheTemplateRenderer = new MustacheTemplateRenderer("templates", "proto");

        Map<String, Object> model = new HashMap<>();

        model.put("javaPackage", "com.test");

        model.put("package", "com.test");

        model.put("messageName", "Student");
        model.put("fields", "  string name = 1;");

        List<String> imports = new ArrayList<String>();
        imports.add("google/protobuf/any.proto");
        imports.add("google/protobuf/ttt.proto");

        model.put("imports", imports);

        Map<String, Object> message = new HashMap<>();
        message.put("messageName", "Student");
        message.put("fields", "  string name = 1;");

        List<Object> messages = new ArrayList<>();
        messages.add(message);

        model.put("messages", messages);

        String render = mustacheTemplateRenderer.render("descriptor", model);

        System.err.println(render);

    }

}
