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

import jakarta.enterprise.inject.spi.CDI;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.embedded.beans.MyBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Note that this test will fail if using any of CDI 1.0 implementations (i.e. Weld 1.0.x and Weld 1.1.x).
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class WeldEmbeddedCDIProviderTest {

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(WeldEmbeddedCDIProviderTest.class, MyBean.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Test
    public void testCDIProvider() throws Exception {
        assertNotNull(CDI.current().getBeanManager());
        assertEquals("aslak", CDI.current().select(MyBean.class).get().getName());
    }

}
