package org.cid15.aem.groovy.console.components

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

@Model(adaptables = Resource)
class AboutPanel {

    String getVersion() {
        GroovySystem.version
    }
}