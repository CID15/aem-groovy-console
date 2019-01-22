package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

/**
 * Services may implement this interface to supply additional binding values for Groovy script executions.
 */
interface BindingExtensionProvider {

    /**
     * Get the binding for this request.  All bindings provided by extension services will be merged prior to script
     * execution.
     *
     * @param request current request
     * @return binding map for request
     * @deprecated use{@link #getBindingVariables} instead to supply documentation for binding variables
     */
    @Deprecated
    Binding getBinding(SlingHttpServletRequest request)

    /**
     * Get the binding variables for this request.  All bindings provided by extension services will be merged prior to
     * script execution.
     *
     * @param request current request
     * @param response current response
     * @return map of binding variables for request
     */
    Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request, SlingHttpServletResponse response)
}