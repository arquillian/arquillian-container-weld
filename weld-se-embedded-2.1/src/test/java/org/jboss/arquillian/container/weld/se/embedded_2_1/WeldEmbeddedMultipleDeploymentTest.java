/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.container.weld.se.embedded_2_1;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

@RunWith(Arquillian.class)
public class WeldEmbeddedMultipleDeploymentTest {

    @Deployment
    public static Archive<?> defaultDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Deployment(name = "foo")
    public static Archive<?> fooDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            // Test classes
            .addClass(FooService.class)
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Deployment(name = "bar")
    public static Archive<?> barDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            // Test classes
            .addClass(BarService.class)
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void shouldGetNoServiceFromDefaultDeployment(Instance<Service> service) throws Exception {
        Assert.assertTrue(service.isUnsatisfied());
        Assert.assertTrue(CDI.current().select(Service.class).isUnsatisfied());
    }

    @Test
    @OperateOnDeployment("foo")
    public void shouldGetServiceFromFooDeployment(Service service) throws Exception {
        Assert.assertEquals("foo", service.getName());
    }

    @Test
    @OperateOnDeployment("bar")
    public void shouldGetServiceFromBarDeployment(Service service) throws Exception {
        Assert.assertEquals("bar", service.getName());
    }
}

interface Service {

    String getName();
}

class FooService implements Service {

    public String getName() {
        return "foo";
    }
}
class BarService implements Service {

    public String getName() {
        return "bar";
    }
}