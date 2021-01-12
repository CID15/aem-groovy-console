package org.cid15.aem.groovy.console.servlets

import com.google.common.net.MediaType
import org.cid15.aem.groovy.console.constants.GroovyConsoleConstants
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

abstract class AbstractJsonResponseServlet extends SlingAllMethodsServlet {

    void writeJsonResponse(SlingHttpServletResponse response, json) {
        response.contentType = MediaType.JSON_UTF_8.withoutParameters().toString()
        response.characterEncoding = GroovyConsoleConstants.CHARSET

        new JsonBuilder(json).writeTo(response.writer)
    }
}
