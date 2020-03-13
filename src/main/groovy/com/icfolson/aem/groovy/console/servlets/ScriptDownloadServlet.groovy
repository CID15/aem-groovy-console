package com.icfolson.aem.groovy.console.servlets

import com.day.cq.commons.jcr.JcrUtil
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.apache.sling.commons.mime.MimeTypeService
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_FORMAT_FILE_NAME
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/download"
])
class ScriptDownloadServlet extends SlingSafeMethodsServlet {

    private static final String DEFAULT_MEDIA_TYPE = MediaType.PLAIN_TEXT_UTF_8.withoutParameters().toString()

    @Reference
    private AuditService auditService

    @Reference
    private MimeTypeService mimeTypeService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)
        def result = request.getParameter(GroovyConsoleConstants.RESULT)

        def auditRecord = auditService.getAuditRecord(userId, script)

        if (auditRecord) {
            def mediaType = result ? DEFAULT_MEDIA_TYPE : getMediaType(auditRecord)
            def fileName = getFileName(auditRecord, result as Boolean, mediaType)
            def text = result ? auditRecord.result : auditRecord.output

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName.toString()}")

            response.contentType = mediaType
            response.characterEncoding = GroovyConsoleConstants.CHARSET
            response.contentLength = text.length()
            response.outputStream.write(text.getBytes(GroovyConsoleConstants.CHARSET))
        } else {
            response.status = SC_BAD_REQUEST
        }
    }

    private String getFileName(AuditRecord auditRecord, Boolean result, String mediaType) {
        new StringBuilder()
            .append(result ? "result-" : getOutputFileName(auditRecord))
            .append(auditRecord.date.format(DATE_FORMAT_FILE_NAME))
            .append(".")
            .append(mimeTypeService.getExtension(mediaType))
            .toString()
    }

    private String getOutputFileName(AuditRecord auditRecord) {
        def outputFileName

        if (auditRecord.jobProperties?.jobTitle) {
            outputFileName = JcrUtil.createValidName(auditRecord.jobProperties.jobTitle,
                JcrUtil.HYPHEN_LABEL_CHAR_MAPPING).toLowerCase() + "-"
        } else {
            outputFileName = "output-"
        }

        outputFileName
    }

    private String getMediaType(AuditRecord auditRecord) {
        def mediaType

        if (auditRecord.jobProperties?.mediaType) {
            mediaType = MediaType.parse(auditRecord.jobProperties.mediaType)
        } else {
            mediaType = MediaType.PLAIN_TEXT_UTF_8
        }

        mediaType.withoutParameters().toString()
    }
}
