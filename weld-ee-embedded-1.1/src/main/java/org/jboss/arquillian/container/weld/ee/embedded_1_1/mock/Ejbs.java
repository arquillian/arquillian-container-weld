package org.jboss.arquillian.container.weld.ee.embedded_1_1.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EnterpriseBean;
import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import org.jboss.weld.ejb.spi.EjbDescriptor;

public class Ejbs
{
   
   private Ejbs() {}
   
   public static Collection<EjbDescriptor<?>> createEjbDescriptors(Iterable<Class<?>> classes)
   {
      // EJB API dependency is optional
      if (!Utils.isClassAccessible("javax.ejb.Singleton", Ejbs.class.getClassLoader()))
      {
         return Collections.emptySet();
      }
      List<EjbDescriptor<?>> ejbs = new ArrayList<EjbDescriptor<?>>();
      for (Class<?> ejbClass : findEjbs(classes))
      {
         ejbs.add(MockEjbDescriptor.of(ejbClass));
      }
      return ejbs;
   }
   
   
   private static Iterable<Class<?>> findEjbs(Iterable<Class<?>> classes)
   {
      Set<Class<?>> ejbs = new HashSet<Class<?>>();
      for (Class<?> clazz : classes)
      {
         if (clazz.isAnnotationPresent(Stateless.class) || clazz.isAnnotationPresent(Stateful.class) || clazz.isAnnotationPresent(MessageDriven.class) || clazz.isAnnotationPresent(Singleton.class) || EnterpriseBean.class.isAssignableFrom(clazz)) 
         {
            ejbs.add(clazz);
         }
      }
      return ejbs;
   }

}
