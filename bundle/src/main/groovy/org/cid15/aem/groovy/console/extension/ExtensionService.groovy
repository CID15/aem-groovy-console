package org.cid15.aem.groovy.console.extension

import org.cid15.aem.groovy.console.api.BindingExtensionProvider
import org.cid15.aem.groovy.console.api.CompilationCustomizerExtensionProvider
import org.cid15.aem.groovy.console.api.context.ScriptContext
import org.cid15.aem.groovy.console.api.StarImportExtensionProvider

/**
 * Service that dynamically binds extensions providing additional script bindings, star imports, and script metaclasses.
 */
interface ExtensionService extends BindingExtensionProvider, CompilationCustomizerExtensionProvider,
    StarImportExtensionProvider {

    /**
     * Get a list of all script metaclass closures for bound extensions.
     *
     * @param scriptContext current script execution context
     * @return list of metaclass closures
     */
    List<Closure> getScriptMetaClasses(ScriptContext scriptContext)
}