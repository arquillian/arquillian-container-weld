/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.container.weld.embedded.ftests;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.embedded.ftests.uc001.RequestScopedBean;
import org.jboss.arquillian.container.weld.embedded.ftests.uc001.SimpleBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class DeploySimpleBeanTest {
    @Deployment
    public static JavaArchive createArchive() {
        return ShrinkWrap.create(JavaArchive.class).addPackage(SimpleBean.class.getPackage())
                .addAsManifestResource(new StringAsset("<beans version=\"1.1\" bean-discovery-mode=\"all\"/>"), "beans.xml");
    }

    @Inject
    private SimpleBean simpleBean;

    @Inject
    private RequestScopedBean requestScopedBean;

    @Test
    public void shouldBeInjectedAndFunctional() {
        assertNotNull(simpleBean);
        assertNotNull(simpleBean.greet());
    }

    @Test
    public void shouldInjectARequestScopedBean() {
        assertNotNull(requestScopedBean);
        assertNotNull(requestScopedBean.doRequest());
    }
}
