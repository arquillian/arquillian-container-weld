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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ejb.EnterpriseBean;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;

import org.jboss.weld.ejb.spi.EjbDescriptor;

public class Ejbs {

    private Ejbs() {
    }

    public static Collection<EjbDescriptor<?>> createEjbDescriptors(Iterable<Class<?>> classes) {
        // EJB API dependency is optional
        if (!Utils.isClassAccessible("jakarta.ejb.Singleton", Ejbs.class.getClassLoader())) {
            return Collections.emptySet();
        }
        List<EjbDescriptor<?>> ejbs = new ArrayList<EjbDescriptor<?>>();
        for (Class<?> ejbClass : findEjbs(classes)) {
            ejbs.add(MockEjbDescriptor.of(ejbClass));
        }
        return ejbs;
    }

    private static Iterable<Class<?>> findEjbs(Iterable<Class<?>> classes) {
        Set<Class<?>> ejbs = new HashSet<Class<?>>();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Stateless.class) || clazz.isAnnotationPresent(Stateful.class) || clazz.isAnnotationPresent(MessageDriven.class)
                    || clazz.isAnnotationPresent(Singleton.class) || EnterpriseBean.class.isAssignableFrom(clazz)) {
                ejbs.add(clazz);
            }
        }
        return ejbs;
    }

}
