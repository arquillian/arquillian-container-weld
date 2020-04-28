package org.jboss.arquillian.container.weld.embedded;

import static org.junit.Assert.assertTrue;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.embedded.beans.Chicken;
import org.jboss.arquillian.container.weld.embedded.beans.MyBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tomas Remes
 */
@RunWith(Arquillian.class)
public class ExcludedBeansTest {

    private final static String NON_EXISTING_CLASS = "org.jboss.test.NonExistent";

    @Inject
    Instance<MyBean> myBeanInstance;

    @Inject
    Instance<Chicken> chickenInstance;

    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(MyBean.class)
                .addAsWebInfResource(
                        new StringAsset(
                                Descriptors.create(BeansDescriptor.class).createScan().createExclude().name(MyBean.class.getName()).createIfClassNotAvailable()
                                        .name(NON_EXISTING_CLASS).up()
                                        .up().up().getOrCreateScan().createExclude().name(Chicken.class.getName()).createIfClassAvailable()
                                        .name(Inject.class.getName()).up().up().up()
                                        .exportAsString()),
                        "beans.xml");
    }

    @Test
    public void testExcludedBeanIsNotAvailable() {
        assertTrue(myBeanInstance.isUnsatisfied());
        assertTrue(chickenInstance.isUnsatisfied());
    }
}
