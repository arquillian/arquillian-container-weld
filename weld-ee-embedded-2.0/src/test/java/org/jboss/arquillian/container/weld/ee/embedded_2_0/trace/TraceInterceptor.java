package org.jboss.arquillian.container.weld.ee.embedded_2_0.trace;

import java.util.ArrayList;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor @Trace
public class TraceInterceptor {

	public static List<String> called = new ArrayList<String>();

	@AroundInvoke
	public Object manageTransaction(InvocationContext ctx) throws Exception {
		called.add(ctx.getMethod().getDeclaringClass().getSimpleName());
		return ctx.proceed();
	}
}
