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
package org.jboss.arquillian.container.weld.ee.embedded_1_1.mock;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.jboss.weld.injection.spi.JpaInjectionServices;

public class MockJpaInjectionServices implements JpaInjectionServices
{
	private Map<String, EntityManager> entityManagersByUnitName = new HashMap<String, EntityManager>();
   
   public EntityManager resolvePersistenceContext(InjectionPoint injectionPoint)
   {
	   String persistenceUnitName = getPersistenceUnitName(injectionPoint);
		if (persistenceUnitNotYetInitialised(persistenceUnitName)) {
			initialiseEntityManager(persistenceUnitName);
		}
       return entityManagersByUnitName.get(persistenceUnitName);
   }

   private String getPersistenceUnitName(InjectionPoint injectionPoint) {
	   String nameOnAnnotation = injectionPoint.getAnnotated().getAnnotation(PersistenceContext.class).unitName();
	   if (isEmpty(nameOnAnnotation)) {
		   return null;
	   } else {
		   return nameOnAnnotation;
	   }
   }

	private void initialiseEntityManager(String persistenceUnitName) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory(persistenceUnitName);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManagersByUnitName.put(persistenceUnitName, entityManager);
	}

	private boolean persistenceUnitNotYetInitialised(String persistenceUnitName) {
		return !entityManagersByUnitName.containsKey(persistenceUnitName);
	}
	
	private boolean isEmpty(String nameOnAnnotation) {
		return nameOnAnnotation == null || nameOnAnnotation.equals("");
	}
   
   public EntityManagerFactory resolvePersistenceUnit(InjectionPoint injectionPoint)
   {
      return null;
   }
   
   public void cleanup() {
	   for (String persistenceUnitName : entityManagersByUnitName.keySet()) {
		   entityManagersByUnitName.get(persistenceUnitName).close();
	   }
	   entityManagersByUnitName = new HashMap<String, EntityManager>(); 
   }

}
