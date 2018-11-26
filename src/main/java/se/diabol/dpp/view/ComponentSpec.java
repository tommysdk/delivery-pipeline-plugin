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
package se.diabol.dpp.view;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.ItemGroup;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import se.diabol.jenkins.pipeline.util.ProjectUtil;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ComponentSpec extends AbstractDescribableImpl<ComponentSpec> {

    private String name;
    private String initialJob;
    private Set<String> lastJobs = new HashSet<>();
    private boolean showUpstream;

    @DataBoundConstructor
    public ComponentSpec(String name,
                         String initialJob,
                         Set<String> optionalLastJobs,
                         boolean showUpstream) {
        this.name = name;
        this.initialJob = initialJob;
        this.lastJobs.addAll(optionalLastJobs);
        this.showUpstream = showUpstream;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitialJob() {
        return initialJob;
    }

    public void setInitialJob(String initialJob) {
        this.initialJob = initialJob;
    }

    public Set<String> getLastJobs() {
        return lastJobs;
    }

    public void setLastJobs(Set<String> lastJobs) {
        this.lastJobs = lastJobs;
    }

    public boolean isShowUpstream() {
        return showUpstream;
    }

    public void setShowUpstream(boolean showUpstream) {
        this.showUpstream = showUpstream;
    }

    @Extension
    @SuppressWarnings("unused")
    public static class DescriptorImpl extends Descriptor<ComponentSpec> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillInitialJobItems(@AncestorInPath ItemGroup<?> context) {
            return ProjectUtil.fillAllProjects(context, Arrays.asList(AbstractProject.class, WorkflowJob.class));
        }

        public ListBoxModel doFillLastJobsItems(@AncestorInPath ItemGroup<?> context) {
            ListBoxModel options = new ListBoxModel();
            options.add("");
            options.addAll(ProjectUtil.fillAllProjects(context,
                    Arrays.asList(AbstractProject.class, WorkflowJob.class)));
            return options;
        }

        public FormValidation doCheckName(@QueryParameter String value) {
            if (value != null && !"".equals(value.trim())) {
                return FormValidation.ok();
            } else {
                return FormValidation.error("Please supply a title");
            }
        }
    }
}
