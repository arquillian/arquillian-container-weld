package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import org.jboss.weld.bootstrap.api.Bootstrap;

public class Version
{
   public enum Series {
      x_1_1,
      x_2
   }
   
   private static Series series;
   
   public static Series getSeries()
   {
      if(series == null)
      {
         series = determineSeries();
      }
      return series;
   }
   
   private static Series determineSeries() {
      try
      {
         Bootstrap.class.getMethod("parse", new Class[] {Iterable.class, Boolean.TYPE});
         return Series.x_2;
      }
      catch(NoSuchMethodException e) {
         return Series.x_1_1;
      }
   }
}
