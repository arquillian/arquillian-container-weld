/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.container.weld.embedded;

import jakarta.decorator.Decorator;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.NormalScope;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Stereotype;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.interceptor.Interceptor;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Filter;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.metadata.FilterPredicate;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.util.collections.ImmutableList;
import org.jboss.weld.util.collections.ImmutableSet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * BeanUtils
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
final class Utils {

    private static final String BEANS_XML_REGEX = ".*/beans\\.xml";
    private static final List<Class<? extends Annotation>> META_ANNOTATIONS = ImmutableList.of(Stereotype.class, NormalScope.class);
    private static final Set<Class<? extends Annotation>> BEAN_DEFINING_ANNOTATIONS = ImmutableSet.of(
            // built-in scopes
            Dependent.class, RequestScoped.class, ConversationScoped.class, SessionScoped.class, ApplicationScoped.class,
            Interceptor.class, Decorator.class,
            // built-in stereotype
            Model.class,
            // meta-annotations
            NormalScope.class, Stereotype.class);
    private static final Set<Class<? extends Annotation>> ADDITIONAL_EE_BEAN_DEFINING_ANNOTATIONS = ImmutableSet.of(
            // EJB annotations are to be considered bean defining in annotated discovery mode
            jakarta.ejb.Singleton.class, Stateful.class, Stateless.class);

    private Utils() {
    }

    @SuppressWarnings("unchecked")
    static <T> T getBeanReference(BeanManager manager, Class<T> type) {
        Bean<?> bean = manager.resolve(manager.getBeans(type));
        return (T) manager.getReference(
                bean,
                type,
                manager.createCreationalContext(null));
    }

    public static String findArchiveId(Archive<?> archive) {
        return archive.getName();
    }

    public static Collection<URL> findBeansXml(Archive<?> archive) {
        Validate.notNull(archive, "Archive must be specified");
        List<URL> beansXmls = new ArrayList<URL>();
        Map<ArchivePath, Node> nestedArchives = archive.getContent(Filters.include(".*\\.jar|.*\\.war"));
        for (final Map.Entry<ArchivePath, Node> nestedArchiveEntry : nestedArchives.entrySet()) {
            if (!(nestedArchiveEntry.getValue().getAsset() instanceof ArchiveAsset)) {
                continue;
            }
            ArchiveAsset nestedArchive = (ArchiveAsset) nestedArchiveEntry.getValue().getAsset();
            Map<ArchivePath, Node> classes = nestedArchive.getArchive().getContent(Filters.include(BEANS_XML_REGEX));
            for (final Map.Entry<ArchivePath, Node> entry : classes.entrySet()) {
                try {
                    beansXmls.add(
                            new URL(null, "archive://" + nestedArchive.getArchive().getName() + entry.getKey().get(), new URLStreamHandler() {
                                @Override
                                protected java.net.URLConnection openConnection(URL u) throws java.io.IOException {
                                    return new URLConnection(u) {
                                        @Override
                                        public void connect() throws IOException {
                                        }

                                        @Override
                                        public InputStream getInputStream()
                                                throws IOException {
                                            return entry.getValue().getAsset().openStream();
                                        }
                                    };
                                }

                            }));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Map<ArchivePath, Node> classes = archive.getContent(Filters.include(BEANS_XML_REGEX));
        for (final Map.Entry<ArchivePath, Node> entry : classes.entrySet()) {
            try {
                beansXmls.add(
                        new URL(null, "archive://" + entry.getKey().get(), new URLStreamHandler() {
                            @Override
                            protected java.net.URLConnection openConnection(URL u) throws java.io.IOException {
                                return new URLConnection(u) {
                                    @Override
                                    public void connect() throws IOException {
                                    }

                                    @Override
                                    public InputStream getInputStream()
                                            throws IOException {
                                        return entry.getValue().getAsset().openStream();
                                    }
                                };
                            }

                        }));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beansXmls;
    }

    public static Collection<Class<?>> findBeanClasses(Archive<?> archive, ClassLoader classLoader, BeansXml beansXml, ResourceLoader resourceLoader, Environment environment) {
        Validate.notNull(archive, "Archive must be specified");
        List<Class<?>> beanClasses = new ArrayList<Class<?>>();

        try {
            Map<ArchivePath, Node> nestedArchives = archive.getContent(Filters.include(".*\\.jar|.*\\.war|.*\\.rar"));
            for (final Map.Entry<ArchivePath, Node> nestedArchiveEntry : nestedArchives.entrySet()) {
                if (!(nestedArchiveEntry.getValue().getAsset() instanceof ArchiveAsset)) {
                    continue;
                }
                ArchiveAsset nestedArchive = (ArchiveAsset) nestedArchiveEntry.getValue().getAsset();
                Map<ArchivePath, Node> markerFiles = nestedArchive.getArchive().getContent(Filters.include(BEANS_XML_REGEX));
                if (markerFiles.isEmpty()) {
                    continue;
                }

                beanClasses.addAll(filterClasses(nestedArchive.getArchive(), classLoader, beansXml, resourceLoader, environment));
            }
            Map<ArchivePath, Node> markerFiles = archive.getContent(Filters.include(BEANS_XML_REGEX));
            if (!markerFiles.isEmpty()) {
                beanClasses.addAll(filterClasses(archive, classLoader, beansXml, resourceLoader, environment));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load class from archive " + archive.getName(), e);
        }
        return beanClasses;
    }

    /*
     *  input:  /org/MyClass.class
     *  output: org.MyClass
     */
    public static String findClassName(ArchivePath path) {
        String className = path.get();
        className = className.replaceAll("/WEB-INF/classes/", "")
                .replaceAll("/META-INF/versions/\\d*/", "");
        if (className.charAt(0) == '/') {
            className = className.substring(1);
        }
        className = className.replaceAll("\\.class", "");
        className = className.replaceAll("/", ".");
        return className;
    }

    private static Collection<Class<?>> filterClasses(Archive<?> archive, ClassLoader classLoader, BeansXml beansXml, ResourceLoader resourceLoader, Environment environment)
            throws ClassNotFoundException {
        List<Class<?>> beanClasses = new ArrayList<Class<?>>();
        Map<ArchivePath, Node> classes = archive.getContent(Filters.include(".*\\.class"));
        BeanDiscoveryMode discoveryMode = beansXml.getBeanDiscoveryMode();
        for (Map.Entry<ArchivePath, Node> classEntry : classes.entrySet()) {
            if (beansXml.getScanning().getExcludes().isEmpty()) {
                Class<?> loadedClass = classLoader.loadClass(
                        findClassName(classEntry.getKey()));
                addBeanClassIfNeeded(loadedClass, beanClasses, discoveryMode, environment);
            } else {
                boolean isExcluded = false;
                for (Metadata<Filter> filterMetadata : beansXml.getScanning().getExcludes()) {
                    FilterPredicate excludePredicate = new FilterPredicate(filterMetadata, resourceLoader);
                    if (excludePredicate.test(findClassName(classEntry.getKey()))) {
                        isExcluded = true;
                        break;
                    }
                }
                if (!isExcluded) {
                    Class<?> loadedClass = classLoader.loadClass(findClassName(classEntry.getKey()));
                    addBeanClassIfNeeded(loadedClass, beanClasses, discoveryMode, environment);
                }
            }
        }
        return beanClasses;
    }

    private static void addBeanClassIfNeeded(Class<?> potentialBeanClass, List<Class<?>> knownBeanClasses, BeanDiscoveryMode mode, Environment environment) {
        if (mode.equals(BeanDiscoveryMode.ANNOTATED)) {
            Set<Class<? extends Annotation>> completeSetOfBeanDefiningAnnotations;
            // if we are in EE, we need to consider EJB annotation to be bean defining
            if (environment != null && (environment.equals(Environments.EE_INJECT) || environment.equals(Environments.EE))) {
                completeSetOfBeanDefiningAnnotations = new HashSet<>();
                completeSetOfBeanDefiningAnnotations.addAll(BEAN_DEFINING_ANNOTATIONS);
                completeSetOfBeanDefiningAnnotations.addAll(ADDITIONAL_EE_BEAN_DEFINING_ANNOTATIONS);
            } else {
                completeSetOfBeanDefiningAnnotations = BEAN_DEFINING_ANNOTATIONS;
            }
            // discovery mode ANNOTATED, only adding classes that have some bean defining annotation
            if (hasBeanDefiningAnnotation(potentialBeanClass, completeSetOfBeanDefiningAnnotations)) {
                knownBeanClasses.add(potentialBeanClass);
            }
        } else {
            // discovery mode ALL, just add all classes
            knownBeanClasses.add(potentialBeanClass);
        }
    }

    /**
     * Checks given class for presence of any bean-defining annotation; returns true if the class has any of them.
     * The set of bean defining annotations is provided as a parameter.
     * <p>
     * This method is copied from Weld's org.jboss.weld.environment.util.Reflections#hasBeanDefiningAnnotation.
     *
     * @param clazz                          Class to check for annotations
     * @param initialBeanDefiningAnnotations Set of annotations that are considered bean defining
     * @return true if the class contains at least one bean defining annotation, false otherwise
     */
    private static boolean hasBeanDefiningAnnotation(Class<?> clazz, Set<Class<? extends Annotation>> initialBeanDefiningAnnotations) {
        for (Class<? extends Annotation> beanDefiningAnnotation : initialBeanDefiningAnnotations) {
            if (clazz.isAnnotationPresent(beanDefiningAnnotation)) {
                return true;
            }
        }
        for (Class<? extends Annotation> metaAnnotation : META_ANNOTATIONS) {
            if (hasBeanDefiningMetaAnnotationSpecified(clazz.getAnnotations(), metaAnnotation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks provided array of annotations for presence of given meta-annotation.
     * Returns true if any
     *
     * @param annotations        Annotations to check for presence of meta annotation
     * @param metaAnnotationType Meta-annotation (most likely {@code @Stereotype} or {@code @NormalScoped}) to check for
     * @return <code>true</code> if any of the annotations specified has the given meta annotation type specified, <code>false</code> otherwise
     */
    private static boolean hasBeanDefiningMetaAnnotationSpecified(Annotation[] annotations, Class<? extends Annotation> metaAnnotationType) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(metaAnnotationType)) {
                return true;
            }
        }
        return false;
    }

}
