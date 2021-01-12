package com.alibaba.protokit.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.alibaba.protokit.gen.CodeDumper;

/**
 * 
 * @author hengyunabc 2021-01-12
 *
 */

@Mojo(name = "compile", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class ProtoKitCompileMojo extends AbstractMojo {

    /**
     * The current Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    /**
     * Biz Pojo Classes
     */
    @Parameter(required = false)
    private List<String> classNames;

    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/proto")
    private File protoOutputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO 更丰富的指定配置的方式？

        ClassLoader classLoader = getClassLoader();

        try {
            List<Class<?>> classList = new ArrayList<Class<?>>();
            for (String clazz : classNames) {
                Class<?> pojoClass = classLoader.loadClass(clazz);
                classList.add(pojoClass);
            }
            CodeDumper.dump(classList, protoOutputDirectory.getAbsolutePath());
            getLog().error("" + classNames);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the an isolated classloader.
     *
     * @return ClassLoader
     * @noinspection unchecked
     */
    private ClassLoader getClassLoader() {
        try {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL urls[] = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (Exception e) {
            getLog().debug("Couldn't get the classloader.");
            return this.getClass().getClassLoader();
        }
    }

}
