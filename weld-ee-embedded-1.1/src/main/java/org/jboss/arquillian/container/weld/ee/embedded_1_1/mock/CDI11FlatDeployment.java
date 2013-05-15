package org.jboss.arquillian.container.weld.ee.embedded_1_1.mock;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.CDI11Deployment;

public class CDI11FlatDeployment extends FlatDeployment implements CDI11Deployment {

    private final BeanDeploymentArchive beanDeploymentArchive;
    
    protected CDI11FlatDeployment(BeanDeploymentArchive beanDeploymentArchive) {
        super(beanDeploymentArchive);
        this.beanDeploymentArchive = beanDeploymentArchive;
    }

    @Override
    public BeanDeploymentArchive getBeanDeploymentArchive(Class<?> beanClass) {
       return beanDeploymentArchive;
    }
}
