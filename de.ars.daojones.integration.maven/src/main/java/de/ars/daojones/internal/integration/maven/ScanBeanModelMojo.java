package de.ars.daojones.internal.integration.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Goal which scans the project's classes for bean annotations and writes the
 * model into an XML file. This prevents annotation scanning during runtime.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
// RequiresDependencyResolution because of Reflections that has to scan bytecode for annotations.
@Mojo( name = "scan-bean-model", defaultPhase = LifecyclePhase.PROCESS_CLASSES, inheritByDefault = true, requiresDependencyResolution = ResolutionScope.COMPILE )
public class ScanBeanModelMojo extends AbstractMojo {

  private static final Messages bundle = Messages.create( "ScanMojo" );

  @Component
  private MavenProject project;

  private static URL[] toURLs( final Collection<String> files ) throws IOException {
    final URL[] result = new URL[files.size()];
    int idx = 0;
    for ( final String file : files ) {
      result[idx] = new File( file ).toURI().toURL();
      idx++;
    }
    return result;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void execute() throws MojoExecutionException {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      // set bytecode directory
      command.setBytecodeDirectory( new File( project.getBuild().getOutputDirectory() ) );
      // set the target directory (the same)
      command.setTargetDirectory( new File( project.getBuild().getOutputDirectory() ) );
      // dependency-resolving class loader must contain all project dependencies with compile scope
      final List<String> classpathElements = project.getCompileClasspathElements();
      getLog().debug( ScanBeanModelMojo.bundle.get( "debug.classpath", classpathElements ) );
      final URL[] classpathElementUrls = ScanBeanModelMojo.toURLs( classpathElements );
      final URLClassLoader dependenciesClassLoader = new URLClassLoader( classpathElementUrls,
              ScanBeanModelMojo.class.getClassLoader() );
      command.setDependenciesClassLoader( dependenciesClassLoader );
      try {
        command.execute();
      } finally {
        // Remove this later
        dependenciesClassLoader.clearAssertionStatus();
        // Replace by (since Java 7)
        // dependenciesClassloader.close();
      }
    } catch ( final Exception e ) {
      throw new MojoExecutionException( ScanBeanModelMojo.bundle.get( "error" ), e );
    }
  }

}
