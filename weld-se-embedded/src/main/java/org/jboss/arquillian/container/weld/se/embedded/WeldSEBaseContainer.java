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

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.context.annotation.DeploymentScoped;
import org.jboss.arquillian.container.weld.se.embedded.shrinkwrap.ShrinkwrapBeanDeploymentArchive;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.manager.api.WeldManager;

import javax.enterprise.inject.spi.BeanManager;

/**
 * WeldSEContainer
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WeldSEBaseContainer implements DeployableContainer<WeldSEConfiguration>
{
   @Inject
   @DeploymentScoped
   protected InstanceProducer<ContextClassLoaderManager> classLoaderManagerInst;
   
   @Inject
   @DeploymentScoped
   protected InstanceProducer<WeldManager> weldManagerInst;

   // ContextLookup is Strict typed, so we have to expose WeldManager for LifeCycleHandler and BeanManager for CDIEnrichment
   @Inject
   @DeploymentScoped
   protected InstanceProducer<BeanManager> beanManagerInst;

   @Inject
   @DeploymentScoped
   protected InstanceProducer<WeldBootstrap> weldBootstrapInst;

   public ProtocolDescription getDefaultProtocol()
   {
      return new ProtocolDescription("Local");
   }
   
   public Class<WeldSEConfiguration> getConfigurationClass()
   {
      return WeldSEConfiguration.class;
   }

   public Deployment createDeployment(ShrinkwrapBeanDeploymentArchive beanArchive) {
      return new WeldSEDeployment(beanArchive);
   }

   public void setup(WeldSEConfiguration configuration)
   {
   }
   
   public void start() throws LifecycleException
   {
   }

   public void stop() throws LifecycleException
   {
   }
   
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Weld");
   }
   
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Weld");      
   }

   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      ShrinkwrapBeanDeploymentArchive beanArchive = archive.as(ShrinkwrapBeanDeploymentArchive.class);

      Deployment deployment = createDeployment(beanArchive);

      ContextClassLoaderManager classLoaderManager = new ContextClassLoaderManager(beanArchive.getClassLoader());
      classLoaderManager.enable();

      classLoaderManagerInst.set(classLoaderManager);

      WeldBootstrap bootstrap = new WeldBootstrap();
      beanArchive.setBootstrap(bootstrap);

      startContainer(bootstrap, deployment);

      WeldManager manager = bootstrap.getManager(beanArchive);

      weldBootstrapInst.set(bootstrap);
      weldManagerInst.set(manager);
      beanManagerInst.set(manager);

      return new ProtocolMetaData();
   }

   public void startContainer(Bootstrap bootstrap, Deployment deployment) {
      bootstrap.startContainer(Environments.SE, deployment)
          .startInitialization()
          .deployBeans()
          .validateBeans()
          .endInitialization();
   }

   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      WeldBootstrap bootstrap = weldBootstrapInst.get();
      if(bootstrap != null)
      {
         bootstrap.shutdown();
      }
      ContextClassLoaderManager classLoaderManager = classLoaderManagerInst.get();
      classLoaderManager.disable();
   }
}