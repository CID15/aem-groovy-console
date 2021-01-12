package org.cid15.aem.groovy.console.notification

import org.cid15.aem.groovy.console.response.RunScriptResponse

/**
 * Services may implement this interface to send email notifications for Groovy Console script executions.
 */
interface EmailNotificationService extends NotificationService {

    /**
     * Send an email notification for given script response and recipients, optionally attaching the script output.
     *
     * @param response script execution response
     * @param recipients email to recipients
     * @param attachOutput if true, attach the script output file
     */
    void notify(RunScriptResponse response, Set<String> recipients, boolean attachOutput)

    /**
     * Send an email notification for given script response and recipients, with the provided success/failure
     * templates, optionally attaching the script output.
     *
     * @param response script execution response
     * @param recipients email to recipients
     * @param successTemplate GStringTemplate for successful executions
     * @param failureTemplate GStringTemplate for failed executions
     * @param attachOutput if true, attach the script output file
     */
    void notify(RunScriptResponse response, Set<String> recipients, String successTemplate,
        String failureTemplate, boolean attachOutput)
}
