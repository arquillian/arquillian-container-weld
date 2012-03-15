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

import org.jboss.weld.ejb.api.SessionObjectReference;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.ejb.spi.EjbServices;
import org.jboss.weld.ejb.spi.InterceptorBindings;

import javax.ejb.NoSuchEJBException;

public class MockEjBServices implements EjbServices
{


    public SessionObjectReference resolveEjb(EjbDescriptor<?> ejbDescriptor)
    {
        return new MockSessionObjectReference(createInstance(ejbDescriptor));
    }

    private Object createInstance(EjbDescriptor<?> ejbDescriptor)
    {
        try
        {
            return ejbDescriptor.getBeanClass().newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Could not instantiate EJB " + ejbDescriptor.getBeanClass(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Could not instantiate EJB " + ejbDescriptor.getBeanClass(), e);
        }
    }

    public void registerInterceptors(EjbDescriptor<?> ejbDescriptor, InterceptorBindings interceptorBindings)
    {
        // do nothing
    }

    public void cleanup()
    {
    }

    private class MockSessionObjectReference implements SessionObjectReference
    {

        private static final long serialVersionUID = 2L;
        private Object ejb;
        private boolean removed;

        public MockSessionObjectReference(Object ejb)
        {
            this.ejb = ejb;
        }

        public <S> S getBusinessObject(Class<S> businessInterfaceType)
        {
            if (removed)
            {
                throw new NoSuchEJBException("already removed");
            }
            return (S) ejb;
        }

        public void remove()
        {
            ejb = null;
            removed = true;
        }

        public boolean isRemoved()
        {
            return removed;
        }
    }
}
