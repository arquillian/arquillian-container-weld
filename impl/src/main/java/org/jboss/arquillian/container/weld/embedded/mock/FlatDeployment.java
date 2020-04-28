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
package org.jboss.arquillian.container.weld.embedded.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import jakarta.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.CDI11Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.config.ConfigurationKey;
import org.jboss.weld.configuration.spi.ExternalConfiguration;
import org.jboss.weld.configuration.spi.helpers.ExternalConfigurationBuilder;
import org.jboss.weld.ejb.spi.EjbServices;
import org.jboss.weld.security.spi.SecurityServices;
import org.jboss.weld.transaction.spi.TransactionServices;

public class FlatDeployment implements CDI11Deployment {
    private final Collection<BeanDeploymentArchive> beanDeploymentArchives;
    private final ServiceRegistry services;
    private Set<Extension> extensions;

    public FlatDeployment(BeanDeploymentArchive beanDeploymentArchive, Extension... extensions) {
        this(new BeanDeploymentArchive[] { beanDeploymentArchive }, Environments.SE, extensions);
    }

    public FlatDeployment(BeanDeploymentArchive beanDeploymentArchive, Environment environment, Extension... extensions) {
        this(new BeanDeploymentArchive[] { beanDeploymentArchive }, environment, extensions);
    }

    public FlatDeployment(BeanDeploymentArchive[] beanDeploymentArchives, Extension... extensions) {
        this(beanDeploymentArchives, Environments.SE, extensions);
    }

    public FlatDeployment(BeanDeploymentArchive[] beanDeploymentArchives, Environment environment, Extension... extensions) {
        this.services = new SimpleServiceRegistry();
        this.beanDeploymentArchives = Arrays.asList(beanDeploymentArchives);
        this.extensions = new HashSet<>(Arrays.asList(extensions));
        configureServices(environment);
    }

    protected void configureServices(Environment environment) {
        if (environment.equals(Environments.EE) || environment.equals(Environments.EE_INJECT)) {
            services.add(TransactionServices.class, new MockTransactionServices());
            services.add(EjbServices.class, new MockEjBServices());
            services.add(SecurityServices.class, new MockSecurityServices());
        }
        if (environment.equals(Environments.SE)) {
            ExternalConfigurationBuilder configurationBuilder = new ExternalConfigurationBuilder()
                // Use relaxed construction by default
                .add(ConfigurationKey.RELAXED_CONSTRUCTION.get(), true);
            services.add(ExternalConfiguration.class, configurationBuilder.build());
        }
    }

    public ServiceRegistry getServices() {
        return services;
    }

    public Iterable<Metadata<Extension>> getExtensions() {
        if (extensions.isEmpty()) {
            Iterator<Extension> extensionsIterator = ServiceLoader.load(Extension.class).iterator();
            while (extensionsIterator.hasNext()) {
                extensions.add(extensionsIterator.next());
            }
        }
        return transform(extensions.toArray(new Extension[] {}));
    }

    public static Iterable<Metadata<Extension>> transform(Extension... extensions) {
        List<Metadata<Extension>> result = new ArrayList<Metadata<Extension>>();
        for (final Extension extension : extensions) {
            result.add(new Metadata<Extension>() {

                public String getLocation() {
                    return "unknown";
                }

                public Extension getValue() {
                    return extension;
                }

            });
        }
        return result;
    }

    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return beanDeploymentArchives;
    }

    public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass) {
        return getWar();
    }

    protected BeanDeploymentArchive getWar() {
        return getBeanDeploymentArchives().iterator().next();
    }

    @Override
    public BeanDeploymentArchive getBeanDeploymentArchive(Class<?> beanClass) {

        for (BeanDeploymentArchive archive : beanDeploymentArchives) {
            if (archive.getBeanClasses().contains(beanClass)) {
                return archive;
            }
        }

        return null;
    }

}
