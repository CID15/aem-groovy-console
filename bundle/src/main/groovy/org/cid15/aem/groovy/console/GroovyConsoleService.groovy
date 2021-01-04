package org.cid15.aem.groovy.console

import org.cid15.aem.groovy.console.api.ActiveJob
import org.cid15.aem.groovy.console.api.JobProperties
import org.cid15.aem.groovy.console.api.context.ScriptContext
import org.cid15.aem.groovy.console.api.context.ScriptData
import org.cid15.aem.groovy.console.response.RunScriptResponse
import org.cid15.aem.groovy.console.response.SaveScriptResponse

/**
 * Service for executing and saving Groovy scripts.
 */
interface GroovyConsoleService {

    /**
     * Run a Groovy script with the given script context.
     *
     * @param scriptContext script context
     * @return response containing script output
     */
    RunScriptResponse runScript(ScriptContext scriptContext)

    /**
     * Save a Groovy script with the file name and content provided in the given script data.
     *
     * @param scriptData script data
     * @return response containing the name of the saved script
     */
    SaveScriptResponse saveScript(ScriptData scriptData)

    /**
     * Run or schedule a Groovy Console job execution.
     *
     * @param jobProperties job properties
     * @return true if job was successfully added
     */
    boolean addScheduledJob(JobProperties jobProperties)

    /**
     * Get a list of all active jobs.
     *
     * @return list of active jobs
     */
    List<ActiveJob> getActiveJobs()
}