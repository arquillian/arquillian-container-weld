package org.jboss.arquillian.container.weld.ee.embedded_1_1.mock;

import org.jboss.weld.bootstrap.spi.Deployment;

public class DeploymentFactory {

    private static final String CDI11DEPLOYMENT_CLASS_NAME = "org.jboss.weld.bootstrap.spi.CDI11Deployment";
    
    private DeploymentFactory() {
    }

    public static Deployment forBeanDeploymentArchive(BeanDeploymentArchiveImpl beanDeploymentArchive) {
        if (isAccessible(CDI11DEPLOYMENT_CLASS_NAME, DeploymentFactory.class.getClassLoader())) {
            return new CDI11FlatDeployment(beanDeploymentArchive);
        } else {
            return new FlatDeployment(beanDeploymentArchive);
        }
    }
    
    private static boolean isAccessible(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
