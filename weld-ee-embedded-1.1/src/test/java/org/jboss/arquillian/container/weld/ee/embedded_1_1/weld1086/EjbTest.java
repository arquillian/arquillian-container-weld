package org.jboss.arquillian.container.weld.ee.embedded_1_1.weld1086;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 */
@RunWith(Arquillian.class)
public class EjbTest {
    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap
                .create(JavaArchive.class, "test.jar")
                .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Stateless
    public static class SomeService {
        public String someMethod() {
            return "test";
        }
    }

    @Inject
    SomeService someService;

    @Test
    public void testStatelessCall() {
        Assert.assertEquals("test", someService.someMethod());
    }
}