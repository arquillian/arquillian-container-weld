package org.jboss.arquillian.container.weld.embedded.trace;

import jakarta.enterprise.context.Dependent;

@Trace
@Dependent
public class TracedBeanOne {

	public void call() {
	}
}
