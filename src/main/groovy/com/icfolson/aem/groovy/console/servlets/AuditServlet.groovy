package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.jcr.Session

@SlingServlet(paths = "/bin/groovyconsole/audit")
class AuditServlet extends AbstractJsonResponseServlet {

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss"

    @Reference
    private AuditService auditService

    @Reference
    private ConfigurationService configurationService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def session = request.resourceResolver.adaptTo(Session)
        def script = request.getParameter(GroovyConsoleConstants.PARAMETER_SCRIPT)
        def userId = request.getParameter(GroovyConsoleConstants.PARAMETER_USER_ID)

        if (script) {
            writeJsonResponse(response, auditService.getAuditRecord(session, userId, script) ?: [:])
        } else {
            writeJsonResponse(response, getAuditRecordsData(request))
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def session = request.resourceResolver.adaptTo(Session)
        def script = request.getParameter(GroovyConsoleConstants.PARAMETER_SCRIPT)
        def userId = request.getParameter(GroovyConsoleConstants.PARAMETER_USER_ID)

        if (script) {
            auditService.deleteAuditRecord(session, userId, script)
        } else {
            auditService.deleteAllAuditRecords(session)
        }
    }

    private Map<String, Object> getAuditRecordsData(SlingHttpServletRequest request) {
        def auditRecords = getAuditRecords(request)
        def consoleHref = configurationService.consoleHref

        def data = []

        auditRecords.each { auditRecord ->
            def lines = auditRecord.script.readLines()

            def map = [
                date: auditRecord.date.format(DATE_FORMAT_DISPLAY),
                scriptPreview: lines.first() + (lines.size() > 1 ? " [...]" : ""),
                userId: auditRecord.userId,
                script: auditRecord.script,
                data: auditRecord.data,
                exception: auditRecord.exception,
                link: "$consoleHref?userId=${auditRecord.userId}&script=${auditRecord.relativePath}",
                relativePath: auditRecord.relativePath
            ]

            data.add(map)
        }

        [data: data]
    }

    private List<AuditRecord> getAuditRecords(SlingHttpServletRequest request) {
        def session = request.resourceResolver.adaptTo(Session)

        def startDateParameter = request.getParameter(GroovyConsoleConstants.PARAMETER_START_DATE)
        def endDateParameter = request.getParameter(GroovyConsoleConstants.PARAMETER_END_DATE)

        def auditRecords

        if (!startDateParameter || !endDateParameter) {
            auditRecords = auditService.getAllAuditRecords(session)
        } else {
            def startDate = Date.parse(DATE_FORMAT, startDateParameter)
            def endDate = Date.parse(DATE_FORMAT, endDateParameter)

            auditRecords = auditService.getAuditRecords(session, startDate.toCalendar(), endDate.toCalendar())
        }

        auditRecords.sort { a, b -> b.date.timeInMillis <=> a.date.timeInMillis }
    }
}
