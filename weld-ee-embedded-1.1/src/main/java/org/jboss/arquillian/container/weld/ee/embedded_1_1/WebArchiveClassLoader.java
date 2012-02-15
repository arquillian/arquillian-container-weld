package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.classloader.ShrinkWrapClassLoader;

/**
 * Intended solely as a workaround for loading CDI extensions in web archives. Specifications (Servlet, EE, ...) are rather unclear as
 * regards lookup of service providers in web archives. See <a
 * href="https://issues.jboss.org/browse/SHRINKWRAP-369">SHRINKWRAP-369</a> for more information.
 * 
 * @author <a href="mailto:mkouba@redhat.com">Martin Kouba</a>
 */
public class WebArchiveClassLoader extends ShrinkWrapClassLoader {

    private static final String SERVICES = "META-INF/services";

    private static final String WAR_SERVICES = "WEB-INF/classes/" + SERVICES;
    
    
    public WebArchiveClassLoader(Archive<?>... archives) {
        super(archives);
    }

    public WebArchiveClassLoader(ClassLoader parent, Archive<?>... archives) {
        super(parent, archives);
    }

    @Override
    public URL findResource(String name) {
       
        URL url = null;
        
        if (name.contains(SERVICES) && name.contains(Extension.class.getName())) {
             url = super.findResource(name.replace(SERVICES, WAR_SERVICES));
        }
        if (url == null) {
            // Fallback
            url = super.findResource(name);
        }
        return url;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
 
        Enumeration<URL> urls = null;
        
        if (name.contains(SERVICES) && name.contains(Extension.class.getName())) {
            urls = super.findResources(name.replace(SERVICES, WAR_SERVICES));
       }
       if (urls == null || !urls.hasMoreElements()) {
           // Fallback
           urls = super.findResources(name);
       }
       return urls;
    }
    
}
