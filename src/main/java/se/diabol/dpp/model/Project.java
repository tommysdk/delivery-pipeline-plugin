package se.diabol.dpp.model;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import se.diabol.jenkins.pipeline.trigger.TriggerException;

import javax.servlet.ServletException;
import java.io.IOException;

public final class Project {

    public static final Project EMPTY_PROJECT = new Project(null, null);

    private Job delegate;
    private Class<? extends Job> implementation;

    public Project(Job delegate, Class<? extends Job> implementation) {
        this.delegate = delegate;
        this.implementation = implementation;
    }

    /**
     * Returns the full name of the underlying job represented by this project
     * @return the full name of the underlying job
     */
    public String getName() {
        String name = "";
        if (delegate != null && delegate.getFullName() != null) {
            name = delegate.getFullName();
        }
        return name;
    }

    public void abortBuild(int buildNumber) throws TriggerException {
        if (getDelegate() != null) {
            if (AbstractProject.class.equals(implementation)) {
                AbstractBuild build = ((AbstractProject) getDelegate()).getBuildByNumber(buildNumber);
                throwTriggerExceptionIfNull(build, buildNumber);
                try {
                    build.doStop();
                } catch (IOException | ServletException ignored) {
                    throw new TriggerException("Failed to abort build on project: " + getName()
                            + ", with build number: " + buildNumber);
                }
            } else if (WorkflowJob.class.equals(implementation)) {
                WorkflowRun build = ((WorkflowJob) getDelegate()).getBuildByNumber(buildNumber);
                throwTriggerExceptionIfNull(build, buildNumber);
                build.doStop();
            }
        }
    }

    public Job getDelegate() {
        return delegate;
    }
    public Class<? extends Job> getImplementation() {
        return implementation;
    }

    private void throwTriggerExceptionIfNull(Run build, int buildNumber) throws TriggerException {
        if (build == null) {
            throw new TriggerException("Failed to resolve build to abort on project: " + getName()
                    + ", with build number: " + buildNumber);
        }
    }
}
