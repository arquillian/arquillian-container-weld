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
package org.jboss.arquillian.container.weld.ee.embedded_2_0.mock;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

public class MockValidator implements Validator
{

   public BeanDescriptor getConstraintsForClass(Class<?> clazz)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public <T> T unwrap(Class<T> type)
   {
      if (type.equals(Validator.class))
      {
         return type.cast(this);
      }
      else
      {
         throw new ValidationException();
      }
   }

   public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups)
   {
      // TODO Auto-generated method stub
      return null;
   }


}
