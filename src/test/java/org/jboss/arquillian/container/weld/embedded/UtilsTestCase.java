/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

import java.util.Collection;

import org.jboss.arquillian.container.weld.embedded.beans.MyBean;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.resources.DefaultResourceLoader;
import org.junit.Assert;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.classloader.ShrinkWrapClassLoader;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;


/**
 * UtilsTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author Matt Benson
 * @version $Revision: $
 */
public class UtilsTestCase
{

   @Test
   public void shouldBeAbleToFindAllClasses() throws Exception
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                              .addClass(MyBean.class)
                              .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

      ShrinkWrapClassLoader classLoader = new ShrinkWrapClassLoader(archive.getClass().getClassLoader(), archive);

      try
      {
         Collection<Class<?>> classes = Utils.findBeanClasses(archive, classLoader, BeansXml.EMPTY_BEANS_XML, DefaultResourceLoader.INSTANCE);
         Assert.assertEquals(1, classes.size());
      }
      finally
      {
         classLoader.close();
      }

   }

   @Test
   public void shouldBeAbleToFindNoClasses() throws Exception
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
               .addClass(MyBean.class);

      ShrinkWrapClassLoader classLoader = new ShrinkWrapClassLoader(archive.getClass().getClassLoader(), archive);

      try
      {
         Collection<Class<?>> classes = Utils.findBeanClasses(archive, classLoader, BeansXml.EMPTY_BEANS_XML, DefaultResourceLoader.INSTANCE);
         Assert.assertTrue(classes.isEmpty());
      }
      finally
      {
         classLoader.close();
      }

   }
}
