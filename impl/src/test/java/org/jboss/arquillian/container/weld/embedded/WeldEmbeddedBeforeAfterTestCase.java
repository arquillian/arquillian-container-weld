/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.embedded.beans.MyBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * WeldEmbeddedIntegrationTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class WeldEmbeddedBeforeAfterTestCase
{
   @Deployment
   public static JavaArchive createdeployment() 
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                  .addClasses(
                        WeldEmbeddedBeforeAfterTestCase.class,
                        MyBean.class)
                  .addAsManifestResource(
                        EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
   }

   private static boolean afterCalled = false;
   
   @Inject
   private MyBean instanceVariable;
   
   private String name;

   
   @Before
   public void shouldExecutreBefore() throws Exception
   {
      Assert.assertNotNull(
            "Verify that the Bean has been injected",
            instanceVariable);

      this.name = instanceVariable.getName();
   }
   
   @After
   public void shouldExecuteAfter() throws Exception
   {
      afterCalled = true;
   }

   @Test @InSequence(0)
   public void shouldBeAbleToReadSetVariableFromBeforePhase() throws Exception 
   {
      Assert.assertEquals("@Before method should have been executed", "aslak", name);
   }
   
   @Test @InSequence(1)
   public void shouldBeAbleToReadSetVariableFromAfterPhase() throws Exception
   {
      Assert.assertTrue("@After method should have been executed(previous @Test)", afterCalled);
   }
}
