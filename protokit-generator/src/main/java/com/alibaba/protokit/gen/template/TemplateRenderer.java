package com.alibaba.protokit.gen.template;

import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author hengyunabc 2021-01-22
 *
 */
public interface TemplateRenderer {

    String render(String templateName, Map<String, ?> model) throws IOException;

}