/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.container.weld.embedded.mock;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Metadata;

public final class BeansXmlUtil {

    private BeansXmlUtil() {
    }

    public static BeansXml prepareBeansXml(Bootstrap bootstrap, Collection<URL> beansXml, boolean merge) throws Exception {
        if (merge) {
            Method parseWithMerge = Bootstrap.class.getMethod("parse", new Class[] { Iterable.class, Boolean.TYPE });
            return (BeansXml) parseWithMerge.invoke(bootstrap, new Object[] { beansXml, true });

        }
        return bootstrap.parse(beansXml);
    }

    @SuppressWarnings("unchecked")
    public static BeansXml removeDuplicate(BeansXml xml) throws Exception {
        Class<?> clazz = xml.getClass();
        removeDuplicate((List<Metadata<String>>) clazz.getMethod("getEnabledAlternativeStereotypes").invoke(xml));
        removeDuplicate((List<Metadata<String>>) clazz.getMethod("getEnabledAlternativeClasses").invoke(xml));
        removeDuplicate((List<Metadata<String>>) clazz.getMethod("getEnabledDecorators").invoke(xml));
        removeDuplicate((List<Metadata<String>>) clazz.getMethod("getEnabledInterceptors").invoke(xml));
        return xml;
    }

    private static void removeDuplicate(List<Metadata<String>> list) {
        for (int i = 0; i < list.size(); i++) {
            Metadata<String> item = list.get(i);
            for (int n = i + 1; n < list.size(); n++) {
                if (item.getValue().equals(list.get(n).getValue())) {
                    list.remove(i);
                    i--;
                }
            }
        }
    }
}