/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.container.weld.embedded;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.classloader.ShrinkWrapClassLoader;

/**
 * {@link ClassLoader} implementation extending the {@link ShrinkWrapClassLoader} to serve
 * as an indirection between the requested resource and the Web Specification's
 * root under which resources are to be served ("WEB-INF/classes").
 *
 * @author <a href="mailto:mkouba@redhat.com">Martin Kouba</a>
 * @author <a href="mailto:alr@jboss.org">ALR</a>
 * @see https://issues.jboss.org/browse/SHRINKWRAP-369
 */
public class WebArchiveClassLoader extends ShrinkWrapClassLoader {

    /**
     * Web specification resource root
     */
    private static final String WAR_ROOT_LOCATION = "WEB-INF/classes/";

    public WebArchiveClassLoader(Archive<?>... archives) {
        super(archives);
    }

    public WebArchiveClassLoader(ClassLoader parent, Archive<?>... archives) {
        super(parent, archives);
    }

    @Override
    public URL findResource(String name) {

        final String adjustedName = WAR_ROOT_LOCATION + name;
        final URL url = super.findResource(adjustedName);

        return url;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {

        final String adjustedName = WAR_ROOT_LOCATION + name;
        final Enumeration<URL> urls = super.findResources(adjustedName);

        return urls;
    }

}
