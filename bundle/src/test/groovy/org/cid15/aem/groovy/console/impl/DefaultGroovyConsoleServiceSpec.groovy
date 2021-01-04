package org.cid15.aem.groovy.console.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import com.google.common.base.Charsets
import org.cid15.aem.groovy.console.GroovyConsoleService
import org.cid15.aem.groovy.console.api.impl.RequestScriptContext
import org.cid15.aem.groovy.console.api.impl.RequestScriptData
import org.cid15.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.sling.event.jobs.JobManager
import org.apache.sling.jcr.resource.JcrResourceConstants
import org.cid15.aem.groovy.console.audit.AuditService
import org.cid15.aem.groovy.console.constants.GroovyConsoleConstants
import org.cid15.aem.groovy.console.extension.impl.DefaultBindingExtensionProvider
import org.cid15.aem.groovy.console.extension.impl.DefaultExtensionService
import org.cid15.aem.groovy.console.extension.impl.DefaultScriptMetaClassExtensionProvider

import static org.cid15.aem.groovy.console.constants.GroovyConsoleConstants.PATH_SCRIPTS_FOLDER
import static org.cid15.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT

class DefaultGroovyConsoleServiceSpec extends ProsperSpec {

    static final def SCRIPT_NAME = "Script"

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FILE = "$PATH_SCRIPTS_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    def setupSpec() {
        slingContext.registerService(JobManager, Mock(JobManager))
        slingContext.registerService(QueryBuilder, Mock(QueryBuilder))
        slingContext.registerService(ConfigurationService, Mock(ConfigurationService))
        slingContext.registerService(AuditService, Mock(AuditService))
        slingContext.registerService(Replicator, Mock(Replicator))
        slingContext.registerInjectActivateService(new DefaultBindingExtensionProvider())
        slingContext.registerInjectActivateService(new DefaultExtensionService())
        slingContext.registerInjectActivateService(new DefaultScriptMetaClassExtensionProvider())
        slingContext.registerInjectActivateService(new DefaultGroovyConsoleService())
    }

    def "run script"() {
        setup:
        def consoleService = slingContext.getService(GroovyConsoleService)

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def outputStream = new ByteArrayOutputStream()

        def scriptContext = new RequestScriptContext(
            request: request,
            response: response,
            outputStream: outputStream,
            printStream: new PrintStream(outputStream, true, Charsets.UTF_8.name()),
            script: scriptAsString
        )

        when:
        def map = consoleService.runScript(scriptContext)

        then:
        assertScriptResult(map)
    }

    def "save script"() {
        setup:
        nodeBuilder.var {
            groovyconsole(JcrResourceConstants.NT_SLING_FOLDER)
        }

        def consoleService = slingContext.getService(GroovyConsoleService)

        def request = requestBuilder.build {
            parameterMap = this.parameterMap
        }

        def scriptData = new RequestScriptData(request)

        when:
        consoleService.saveScript(scriptData)

        then:
        assertNodeExists(PATH_SCRIPTS_FOLDER, JcrConstants.NT_FOLDER)
        assertNodeExists(PATH_FILE, JcrConstants.NT_FILE)
        assertNodeExists(PATH_FILE_CONTENT, JcrConstants.NT_RESOURCE,
            [(JcrConstants.JCR_MIMETYPE): "application/octet-stream"])

        and:
        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == scriptAsString
    }

    void assertScriptResult(map) {
        assert !map.result
        assert map.output == "BEER" + System.getProperty("line.separator")
        assert !map.exceptionStackTrace
        assert map.runningTime
    }

    private String getScriptAsString() {
        def scriptAsString = null

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            scriptAsString = stream.text
        }

        scriptAsString
    }

    private Map<String, Object> getParameterMap() {
        [(GroovyConsoleConstants.FILE_NAME): (SCRIPT_NAME), (SCRIPT): scriptAsString]
    }
}