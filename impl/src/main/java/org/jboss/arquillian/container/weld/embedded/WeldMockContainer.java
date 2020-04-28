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

import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.context.annotation.ContainerScoped;
import org.jboss.arquillian.container.spi.context.annotation.DeploymentScoped;
import org.jboss.arquillian.container.weld.embedded.mock.BeansXmlUtil;
import org.jboss.arquillian.container.weld.embedded.mock.TestContainer;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.classloader.ShrinkWrapClassLoader;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.manager.api.WeldManager;
import static org.jboss.arquillian.container.weld.embedded.Utils.findArchiveId;
import static org.jboss.arquillian.container.weld.embedded.Utils.findBeanClasses;
import static org.jboss.arquillian.container.weld.embedded.Utils.findBeansXml;
import org.jboss.weld.resources.ClassLoaderResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.xml.BeansXmlParser;

/**
 * WeldEEMockConainer
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:mkouba@redhat.com">Martin Kouba</a>
 * @version $Revision: $
 */
public class WeldMockContainer implements DeployableContainer<WeldMockConfiguration> {
    /**
     * Suffix denoting a web archive is being used
     */
    private static final String SUFFIX_WEB_ARCHIVE = ".war";

    @Inject @ContainerScoped
    private InstanceProducer<WeldMockConfiguration> configuration;

    @Inject @DeploymentScoped
    private InstanceProducer<TestContainer> testContainerProducer;

    @Inject @DeploymentScoped
    private InstanceProducer<Bootstrap> bootstrapProducer;

    @Inject @DeploymentScoped
    private InstanceProducer<WeldManager> weldManagerProducer;

    // ContextLookup is Strict typed, so we have to expose WeldManager for LifeCycleHandler and BeanManager for CDIEnrichment
    @Inject @DeploymentScoped
    private InstanceProducer<BeanManager> beanManagerProducer;

    @Inject @DeploymentScoped
    private InstanceProducer<ContextClassLoaderManager> contextClassLoaderManagerProducer;

    private ServiceRegistry serviceRegistry;
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription("Local");
    }

    public Class<WeldMockConfiguration> getConfigurationClass() {
        return WeldMockConfiguration.class;
    }

    public void setup(WeldMockConfiguration configuration) {
        this.configuration.set(configuration);
    }

    public void start() throws LifecycleException {
    }

    public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
        final ShrinkWrapClassLoader classLoader = getClassLoader(archive);
        ContextClassLoaderManager classLoaderManager = new ContextClassLoaderManager(classLoader);
        classLoaderManager.enable();

        serviceRegistry = new SimpleServiceRegistry();
        serviceRegistry.add(ResourceLoader.class, new ClassLoaderResourceLoader(classLoader));

        BeansXmlParser beansXmlParser = new BeansXmlParser();
        BeansXml beansXml = BeansXmlUtil.prepareBeansXml(beansXmlParser, findBeansXml(archive), true);
        Environment environment = Environments.valueOf(configuration.get().getEnvironment());
        TestContainer container = new TestContainer(findArchiveId(archive), beansXml,
                findBeanClasses(archive, classLoader, beansXml, serviceRegistry.get(ResourceLoader.class)), environment, true);
        Bootstrap bootstrap = container.getBootstrap();

        contextClassLoaderManagerProducer.set(classLoaderManager);

        try {
            container.startContainer(environment);
        } finally {
            // always set container - even if an exception occurs, we want the container to be cleaned up properly in undeploy()
            testContainerProducer.set(container);
            bootstrapProducer.set(bootstrap);
        }

        // Assume a flat structure
        WeldManager manager = container.getBeanManager(container.getDeployment().getBeanDeploymentArchives().iterator().next());
        weldManagerProducer.set(manager);
        beanManagerProducer.set(manager);

        return new ProtocolMetaData();
    }

    public void undeploy(Archive<?> archive) throws DeploymentException {
        TestContainer container = testContainerProducer.get();
        if (container != null) {
            container.stopContainer();
        }
        ContextClassLoaderManager classLoaderManager = contextClassLoaderManagerProducer.get();
        classLoaderManager.disable();
    }

    public void stop() throws LifecycleException {
    }

    public void deploy(Descriptor descriptor) throws DeploymentException {
        throw new UnsupportedOperationException("Weld EE Container does not support deployment of Descriptors");
    }

    public void undeploy(Descriptor descriptor) throws DeploymentException {
        throw new UnsupportedOperationException("Weld EE Container does not support undeployment of Descriptors");
    }

    private ShrinkWrapClassLoader getClassLoader(Archive<?> archive) {
        return (archive.getName().endsWith(SUFFIX_WEB_ARCHIVE)) ? new WebArchiveClassLoader(archive.getClass().getClassLoader(), archive)
                : new ShrinkWrapClassLoader(archive.getClass().getClassLoader(), archive);
    }

}
