package org.jboss.arquillian.container.weld.ee.embedded_2_0;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.ee.embedded_2_0.trace.Trace;
import org.jboss.arquillian.container.weld.ee.embedded_2_0.trace.TraceInterceptor;
import org.jboss.arquillian.container.weld.ee.embedded_2_0.trace.TracedBeanOne;
import org.jboss.arquillian.container.weld.ee.embedded_2_0.trace.TracedBeanTwo;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RemoveDuplicateBeanXmlTestCase {

	@Deployment
	public static WebArchive deploy() {
		String beansxml = "<beans><interceptors><class>" + TraceInterceptor.class.getName() + "</class></interceptors></beans>";

		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Trace.class, TraceInterceptor.class, TracedBeanOne.class)
				.addAsWebInfResource(new StringAsset(beansxml), "beans.xml")
				.addAsLibrary(
					ShrinkWrap.create(JavaArchive.class)
						.addClass(TracedBeanTwo.class)
						.addAsManifestResource(new StringAsset(beansxml), "beans.xml")
				);
	}

	@Test
	public void shouldBeAbleTo(TracedBeanOne one, TracedBeanTwo two) {
		one.call();
		two.call();

		Assert.assertEquals(TracedBeanOne.class.getSimpleName(), TraceInterceptor.called.get(0));
		Assert.assertEquals(TracedBeanTwo.class.getSimpleName(), TraceInterceptor.called.get(1));
	}
}
