package org.jboss.arquillian.container.weld.embedded.extension;

import org.jboss.weld.lite.extension.translator.LiteExtensionTranslator;

/**
 * Meant to be same as {@link LiteExtensionTranslator}; this class is present only so that we can register a service
 * provider that originates from this JAR
 *
 * @see org.jboss.weld.lite.extension.translator.LiteExtensionTranslator
 */
public class BuildCompatibleExtensionTranslator extends LiteExtensionTranslator {
}