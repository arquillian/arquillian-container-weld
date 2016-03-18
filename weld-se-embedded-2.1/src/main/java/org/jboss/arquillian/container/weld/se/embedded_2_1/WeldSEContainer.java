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
package org.jboss.arquillian.container.weld.se.embedded_2_1;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.context.annotation.DeploymentScoped;
import org.jboss.arquillian.container.weld.se.embedded.WeldSEBaseContainer;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.bootstrap.api.helpers.RegistrySingletonProvider;
import org.jboss.weld.bootstrap.spi.Deployment;

/**
 * WeldSEContainer
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
class WeldSEContainer extends WeldSEBaseContainer
{
   static
   {
      // Initialize Weld container registry
      SingletonProvider.initialize(new RegistrySingletonProvider());
   }

   @Inject
   @DeploymentScoped
   private Instance<DeploymentDescription> deploymentDescription;

   @Override
   public void startContainer(Bootstrap bootstrap, Deployment deployment)
   {
      if ("_DEFAULT_".equals(deploymentDescription.get().getName()))
      {
         bootstrap.startContainer(Environments.SE, deployment)
             .startInitialization()
             .deployBeans()
             .validateBeans()
             .endInitialization();
      }
      else
      {
         if (!(bootstrap instanceof CDI11Bootstrap))
         {
            throw new RuntimeException("Bootstrap is not CDI 1.1 compatible!");
         }

         // Support for multiple @Deployment methods
         ((CDI11Bootstrap) bootstrap).startContainer(deploymentDescription.get().getName(), Environments.SE, deployment)
             .startInitialization()
             .deployBeans()
             .validateBeans()
             .endInitialization();
      }
   }
}