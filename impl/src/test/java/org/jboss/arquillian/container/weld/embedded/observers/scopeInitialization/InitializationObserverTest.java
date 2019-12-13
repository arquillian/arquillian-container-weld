/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.container.weld.embedded.observers.scopeInitialization;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.arquillian.container.weld.embedded.mock.BeanDeploymentArchiveImpl;
import org.jboss.arquillian.container.weld.embedded.mock.FlatDeployment;
import org.jboss.arquillian.container.weld.embedded.mock.TestContainer;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.junit.Assert;
import org.junit.Test;

public class InitializationObserverTest {

    @Test
    public void testInitializeAndDestroyApplicationScopeObserver() throws Exception {
        BeanDeploymentArchive testBDA = new BeanDeploymentArchiveImpl("test", ApplicationObserver.class, AppScopedBean.class, InitializationObserverTest.class);
        TestContainer testContainer = new TestContainer(new FlatDeployment(testBDA));
        testContainer.startContainer();
        BeanManager bm = testContainer.getBeanManager(testBDA);
        AppScopedBean appScopedBean = getAppScopeBean(bm);
        appScopedBean.ping();

        Assert.assertTrue(ApplicationObserver.isAppScopeInitializationObserved.get());
        Assert.assertNotNull(ApplicationObserver.payload);
        testContainer.stopContainer();
        Assert.assertTrue(ApplicationObserver.isAppScopeDestroyObserved.get());
    }

    private AppScopedBean getAppScopeBean(BeanManager bm) {
        Bean<AppScopedBean> bean = (Bean<AppScopedBean>) bm.getBeans(AppScopedBean.class).iterator().next();
        CreationalContext<AppScopedBean> creationalContext = bm.createCreationalContext(bean);
        return (AppScopedBean) bm.getReference(bean, AppScopedBean.class, creationalContext);
    }
}
