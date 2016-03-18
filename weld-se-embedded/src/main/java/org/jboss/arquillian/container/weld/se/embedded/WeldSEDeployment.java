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
package org.jboss.arquillian.container.weld.se.embedded;

import org.jboss.arquillian.container.weld.se.embedded.shrinkwrap.ShrinkwrapBeanDeploymentArchive;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.util.ServiceLoader;

import javax.enterprise.inject.spi.Extension;
import java.util.Collection;
import java.util.Collections;


public class WeldSEDeployment implements Deployment {

    private final ShrinkwrapBeanDeploymentArchive beanArchive;

    public WeldSEDeployment(ShrinkwrapBeanDeploymentArchive beanArchive) {
        this.beanArchive = beanArchive;
    }

    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives()
    {
        return Collections.singleton((BeanDeploymentArchive) beanArchive);
    }

    public ServiceRegistry getServices()
    {
        return beanArchive.getServices();
    }

    public BeanDeploymentArchive loadBeanDeploymentArchive(
        Class<?> beanClass)
    {
        return beanArchive;
    }

    /* (non-Javadoc)
     * @see org.jboss.weld.bootstrap.spi.Deployment#getExtensions()
     */
    public Iterable<Metadata<Extension>> getExtensions()
    {
        return ServiceLoader.load(Extension.class, beanArchive.getClassLoader());
    }
}
