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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.Enumeration;

/**
 * Tests that resources within /WEB-INF/classes/META-INF are readable within a WAR file
 *
 * @author John D. Ament john.d.ament@gmail.com
 */
@RunWith(Arquillian.class)
public class WebArchiveWithResourceReadTestCase {
    @Deployment
    public static Archive<?> createdeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(new StringAsset("key=value"),"classes/META-INF/somefile.properties")
                .addClass(WasCalledExtension.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void shouldLoadPropertiesInWarFile() throws Exception {
        // since the class is in the WAR and the property file is in the WAR, they're in the same class loader and should be loadable this way
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources("/META-INF/somefile.properties");
        Assert.assertTrue(resources.hasMoreElements());
    }

    @Test
    public void shouldLoadPropertiesInWarFileWithoutLeadingSlash() throws Exception {
        // since the class is in the WAR and the property file is in the WAR, they're in the same class loader and should be loadable this way
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources("META-INF/somefile.properties");
        Assert.assertTrue(resources.hasMoreElements());
    }


}
