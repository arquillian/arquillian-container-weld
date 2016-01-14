package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.manager.api.WeldManager;

public class JpaLifecycleEventExecuter {

	   public void on(@Observes After event, WeldManager manager) throws Throwable
	   {
          cleanupJpaInjectionServices(manager);
	   }

	private void cleanupJpaInjectionServices(WeldManager manager) {
		JpaInjectionServices jpaInjectionServices = manager.getServices().get(JpaInjectionServices.class);
          jpaInjectionServices.cleanup();
	}

}
