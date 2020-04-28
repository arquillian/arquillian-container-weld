package org.jboss.arquillian.container.weld.embedded.trace;

import java.util.ArrayList;
import java.util.List;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor @Trace
public class TraceInterceptor {

	public static List<String> called = new ArrayList<String>();

	@AroundInvoke
	public Object manageTransaction(InvocationContext ctx) throws Exception {
		called.add(ctx.getMethod().getDeclaringClass().getSimpleName());
		return ctx.proceed();
	}
}
