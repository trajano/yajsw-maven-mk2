package org.yajsw.maven.plugin;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @goal generate
 * @phase package
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */

public class YAJSWMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
	Logger logger=Logger.getLogger(YAJSWMojo.class);

	/** @parameter default-value="${project}" */
	private org.apache.maven.project.MavenProject mavenProject;
    public void execute()
        throws MojoExecutionException
    {
        File f = outputDirectory;
       Set artifacts= mavenProject.getArtifacts();
       System.out.println("listing dep artifacts");
       for (Object artifact: artifacts)
       {
           System.out.println("dep artifact:" +artifact.toString());
    	   
       }
//getProjectReferenceId(, artifactId, version)
        
        if ( !f.exists() )
        {
            f.mkdirs();
        }
        System.out.println("getting dependencies");
        for (String dependency: IncludeProjectDependenciesComponentConfigurator.getDependencyStrings())
        {
        	System.out.println("dependency: "+dependency.toString());
        }
        File touch = new File( f, "touch.txt" );
        this.getPluginContext();
        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
