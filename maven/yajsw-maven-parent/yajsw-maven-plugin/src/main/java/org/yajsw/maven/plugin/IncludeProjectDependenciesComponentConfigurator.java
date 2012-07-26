package org.yajsw.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;


/**
 * A custom ComponentConfigurator which adds the project's runtime classpath elements
 * to the
 *
 * @author Brian Jackson
 * @since Aug 1, 2008 3:04:17 PM
 *
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 *                   role-hint="include-project-dependencies"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 *                   role-hint="default"
 * @requiresDependencyResolution compile+runtime
 */

public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator { 
	private static List<URL> dependencies=new ArrayList<URL>();
	private static List<String> dependencyStrings=new ArrayList<String>();
    /**
	 * @return the dependencies
	 */
	public static List<URL> getDependencies() {
		return dependencies;
	}

	private static final Logger LOGGER = Logger.getRootLogger();

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException {
    	System.out.println("configurecomp");
        addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);

        converterLookup.registerConverter( new ClassRealmConverter( containerRealm ) );

        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();

        converter.processConfiguration( converterLookup, component, containerRealm.getClassLoader(), configuration,
                                        expressionEvaluator, listener );
    }
    @SuppressWarnings("unchecked")
	private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
        List<String> runtimeClasspathElements;
    	System.out.println("addProjectDependenciesToClassRealm");
      try {
            //noinspection unchecked
        	Object rce=expressionEvaluator.evaluate("${project.compileClasspathElements}");
       		runtimeClasspathElements = (List<String>) rce;

        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project.runtimeClasspathElements}", e);
        }

        // Add the project dependencies to the ClassRealm
        final URL[] urls = buildURLs(runtimeClasspathElements);
        for (URL url : urls) {
            containerRealm.addConstituent(url);
            dependencies.add(url);
        }
    }

    private URL[] buildURLs(List<String> runtimeClasspathElements) throws ComponentConfigurationException {
        // Add the projects classes and dependencies
    	System.out.println("buildURLs");
    	List<URL> urls = new ArrayList<URL>(runtimeClasspathElements.size());
        for (String element : runtimeClasspathElements) {
            try {
                final URL url = new File(element).toURI().toURL();
                urls.add(url);
                getDependencyStrings().add(element);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added to project class loader: " + url);
                }
            } catch (MalformedURLException e) {
                throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
            }
        }

        // Add the plugin's dependencies (so Trove stuff works if Trove isn't on
        return urls.toArray(new URL[urls.size()]);
    }
	/**
	 * @return the dependencyStrings
	 */
	public static List<String> getDependencyStrings() {
		return dependencyStrings;
	}


}