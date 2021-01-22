package com.alibaba.protokit.gen.template;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

/**
 * 
 * @author hengyunabc 2021-01-22
 *
 */
public class MustacheTemplateRenderer implements TemplateRenderer {

    private final Compiler mustache;

    private String suffix;

    public MustacheTemplateRenderer(String resourcePrefix, String suffix) {
        String prefix = (resourcePrefix.endsWith("/") ? resourcePrefix : resourcePrefix + "/");
        this.mustache = Mustache.compiler().withLoader(mustacheTemplateLoader(prefix)).escapeHTML(false);
        this.suffix = suffix;
    }

    private TemplateLoader mustacheTemplateLoader(String prefix) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return (name) -> {
            String location = prefix + name + "." + suffix;
            return new InputStreamReader(resourceLoader.getResource(location).getInputStream(), StandardCharsets.UTF_8);
        };
    }

    @Override
    public String render(String templateName, Map<String, ?> model) throws IOException {
        Template template = getTemplate(templateName);
        return template.execute(model);
    }

    private Template getTemplate(String name) {
        try {
            return loadTemplate(name);
        } catch (Throwable ex) {
            throw new IllegalStateException("Cannot load template " + name, ex);
        }
    }

    private Template loadTemplate(String name) throws Exception {
        Reader template = this.mustache.loader.getTemplate(name);
        return this.mustache.compile(template);
    }

}
