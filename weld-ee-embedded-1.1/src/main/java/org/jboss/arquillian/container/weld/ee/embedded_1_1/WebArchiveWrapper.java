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
package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Intended solely as a workaround for loading CDI extensions in web archives. Specification (Servlet, EE, ...) are rather unclear as
 * regards lookup of service providers in web archives. See <a
 * href="https://issues.jboss.org/browse/SHRINKWRAP-369">SHRINKWRAP-369</a> for more information.
 * 
 * @author <a href="mailto:mkouba@redhat.com">Martin Kouba</a>
 */
public class WebArchiveWrapper implements Archive<WebArchive> {

    private static final String SERVICES = "META-INF/services";

    private static final String WAR_SERVICES = "WEB-INF/classes/" + SERVICES;

    private WebArchive delegate;

    /**
     * 
     * @param delegate
     */
    public WebArchiveWrapper(WebArchive delegate) {
        super();
        if (delegate == null)
            throw new NullPointerException();
        this.delegate = delegate;
    }

    @Override
    public Node get(ArchivePath path) throws IllegalArgumentException {

        Node node = null;

        if (path.get().contains(SERVICES) && path.get().contains(Extension.class.getName())) {
            node = this.delegate.get(ArchivePaths.create(path.get().replace(SERVICES, WAR_SERVICES)));
        }
        if (node == null) {
            node = this.delegate.get(path);
        }
        return node;
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public WebArchive add(Asset asset, ArchivePath target) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(Asset asset, ArchivePath target, String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(Asset asset, String target, String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(NamedAsset namedAsset) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(Asset asset, String target) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive addAsDirectory(String path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive addAsDirectories(String... paths) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive addAsDirectory(ArchivePath path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive addAsDirectories(ArchivePath... paths) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node get(String path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, String path, ArchiveFormat archiveFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path, ArchiveFormat archiveFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter, ArchiveFormat archiveFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ArchivePath path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(String path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node delete(ArchivePath path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node delete(String archivePath) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<ArchivePath, Node> getContent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<ArchivePath, Node> getContent(Filter<ArchivePath> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(Archive<?> archive, ArchivePath path, Class<? extends StreamExporter> exporter)
            throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive add(Archive<?> archive, String path, Class<? extends StreamExporter> exporter)
            throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source, Filter<ArchivePath> filter) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source, ArchivePath path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source, String path) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source, ArchivePath path, Filter<ArchivePath> filter) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebArchive merge(Archive<?> source, String path, Filter<ArchivePath> filter) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public String toString(boolean verbose) {
        return this.delegate.toString(verbose);
    }

    @Override
    public String toString(Formatter formatter) throws IllegalArgumentException {
        return this.delegate.toString(formatter);
    }

    @Override
    public void writeTo(OutputStream outputStream, Formatter formatter) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

}
