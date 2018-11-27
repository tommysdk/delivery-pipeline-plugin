/*
This file is part of Delivery Pipeline Plugin.

Delivery Pipeline Plugin is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Delivery Pipeline Plugin is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Delivery Pipeline Plugin.
If not, see <http://www.gnu.org/licenses/>.
*/
package se.diabol.dpp.util;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.RunList;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import se.diabol.dpp.model.Project;
import se.diabol.jenkins.pipeline.util.BuildUtil;

import javax.annotation.CheckForNull;

public final class Upstreams {

    private Upstreams() {
    }

    @CheckForNull
    public static Run getFirstUpstreamBuild(Project project, Project first,
                                            Result minResult) {
        RunList builds = project.getDelegate().getBuilds();
        for (Object build : builds) {
            if (AbstractProject.class.equals(project.getImplementation())) {
                AbstractBuild abstractBuild = (AbstractBuild) build;
                if (minResult == null || (!abstractBuild.isBuilding() && !abstractBuild.getResult().isWorseThan(minResult))) {
                    // TODO: (AbstractProject) first.getDelegate() may cast ClassCastException
                    AbstractBuild upstream = BuildUtil.getFirstUpstreamBuild(abstractBuild, (AbstractProject) first.getDelegate());
                    // TODO: Check first delegate implementation
                    if (upstream != null && upstream.getProject().equals(first.getDelegate())) {
                        return upstream;
                    }
                }
            }
            else if (WorkflowJob.class.equals(project.getImplementation())) {
                WorkflowRun workflowRun = (WorkflowRun) build;
                if (minResult == null || (!workflowRun.isBuilding() && !workflowRun.getResult().isWorseThan(minResult))) {
                    // TODO: (WorkflowJob) first.getDelegate() may cast ClassCastException
                    // TODO: Rebu
                    WorkflowRun upstream = BuildUtil.getFirstUpstreamBuild(workflowRun, (WorkflowJob) first.getDelegate());
                    // TODO: Check first delegate implementation
                    if (upstream != null && upstream.getParent().equals(first.getDelegate())) {
                        return upstream;
                    }
                }
            }
        }
        return null;
    }
}
