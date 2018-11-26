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

import com.google.common.collect.Sets;
import hudson.Extension;
import hudson.model.Api;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.model.listeners.ItemListener;
import jenkins.model.Jenkins;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import se.diabol.dpp.model.Component;
import se.diabol.dpp.model.Project;
import se.diabol.dpp.util.Projects;
import se.diabol.jenkins.pipeline.PipelineApi;
import se.diabol.jenkins.pipeline.PipelineView;
import se.diabol.jenkins.pipeline.trigger.TriggerException;
import se.diabol.jenkins.pipeline.util.JenkinsUtil;
import se.diabol.jenkins.pipeline.util.PipelineUtils;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static se.diabol.dpp.view.ViewConfiguration.DEFAULT_NO_OF_PIPELINES;
import static se.diabol.dpp.view.ViewConfiguration.DEFAULT_SORTING_STRATEGY;
import static se.diabol.dpp.view.ViewConfiguration.DEFAULT_UPDATE_INTERVAL;

public class DeliveryPipelineView extends View implements PipelineView {

    private boolean allowAbort = false;
    private boolean allowManualTriggers = false;
    private boolean allowPipelineStart = false;
    private boolean allowRebuild = false;
    private boolean linkToConsoleLog = false;
    private boolean pagingEnabled = false;
    private boolean relativeLinksEnabled = false;
    private boolean showAggregatedChangeLog = false;
    private boolean showAggregatedPipeline = false;
    private boolean showChangeLog = false;
    private boolean showDescription = false;
    private boolean showPromotions = false;
    private boolean showStaticAnalysisResults = false;
    private boolean showTestResults = false;
    private boolean showTotalBuildTime = false;

    private int maxNumberOfVisiblePipelines = -1;
    private int noOfPipelinesPerComponent = DEFAULT_NO_OF_PIPELINES;
    private int noOfColumnsDisplayed = 1;
    private int updateInterval = DEFAULT_UPDATE_INTERVAL;

    private List<ComponentSpec> componentSpecs;
    private List<se.diabol.jenkins.pipeline.DeliveryPipelineView.RegExpSpec> firstJobRegExpSpecification;

    private String aggregatedChangeLogGroupingPattern = null;
    private String customCss = null;
    private String customFullScreenCss = null;
    private String description = null;
    private String sortingStrategyClass = DEFAULT_SORTING_STRATEGY;

    private transient String error;

    public DeliveryPipelineView(String name) {
        super(name);
    }

    public DeliveryPipelineView(String name, ViewGroup owner) {
        super(name, owner);
    }

    @Override
    public Collection<TopLevelItem> getItems() {
        Set<TopLevelItem> jobs = Sets.newHashSet();
        addJobsFromComponentSpecifications(jobs);
        addRegexpFirstJobs(jobs);
        return jobs;
    }

    @Override
    public boolean contains(TopLevelItem item) {
        return false;
    }

    @Override
    protected void submit(StaplerRequest req) throws ServletException {
        req.bindJSON(this, req.getSubmittedForm());
        componentSpecs = req.bindJSONToList(ComponentSpec.class,
                req.getSubmittedForm().get("componentSpecifications"));
        firstJobRegExpSpecification = req.bindJSONToList(
                se.diabol.jenkins.pipeline.DeliveryPipelineView.RegExpSpec.class,
                req.getSubmittedForm().get("firstJobRegExpSpecification"));
    }

    @Override
    public Item doCreateItem(StaplerRequest req, StaplerResponse rsp) {
        return null;
    }

    @Override
    public void triggerManual(String projectName, String upstreamName, String buildId) {
        // TODO: Implement
    }

    @Override
    public void triggerRebuild(String projectName, String buildId) {
        // TODO: Implement
    }

    @Override
    public void abortBuild(String projectName, String buildNumber) throws TriggerException, AuthenticationException {
        Project project = Projects.resolve(projectName, Jenkins.getInstance());
        if (project.getDelegate() == null) {
            throw new TriggerException("Failed to resolve project to abort: " + projectName);
        }
        if (!project.getDelegate().hasPermission(Item.CANCEL)) {
            throw new BadCredentialsException("Not authorized to abort build");
        }
        project.abortBuild(Integer.parseInt(buildNumber));
    }

    @Exported
    public List<Component> getPipelines() {
        List<Component> components = new ArrayList<>();
        // TODO: Implement
        return components;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void onProjectRenamed(Item item, String oldName, String newName) {
        if (componentSpecs != null) {
            List<ComponentSpec> subjectForRemoval = new ArrayList<>();
            componentSpecs.parallelStream().forEach(cs -> {
                if (cs.getInitialJob().equals(oldName)) {
                    if (newName == null) {
                        subjectForRemoval.add(cs);
                    } else {
                        cs.setInitialJob(newName);
                    }
                }
                if (cs.getLastJobs().contains(oldName)) {
                    if (newName == null) {
                        subjectForRemoval.add(cs);
                    } else {
                        cs.getLastJobs().remove(oldName);
                        cs.getLastJobs().add(newName);
                    }
                }
            });
            componentSpecs.removeAll(subjectForRemoval);
        }
    }

    @Exported
    @Override
    public String getViewUrl() {
        return super.getViewUrl();
    }

    @Override
    public Api getApi() {
        return new PipelineApi(this);
    }

    @Exported
    public String getLastUpdated() {
        return PipelineUtils.formatTimestamp(System.currentTimeMillis());
    }

    @Exported
    public boolean isAllowAbort() {
        return allowAbort;
    }

    public void setAllowAbort(boolean allowAbort) {
        this.allowAbort = allowAbort;
    }

    @Exported
    public boolean isAllowManualTriggers() {
        return allowManualTriggers;
    }

    public void setAllowManualTriggers(boolean allowManualTriggers) {
        this.allowManualTriggers = allowManualTriggers;
    }

    @Exported
    public boolean isAllowPipelineStart() {
        return allowPipelineStart;
    }

    public void setAllowPipelineStart(boolean allowPipelineStart) {
        this.allowPipelineStart = allowPipelineStart;
    }

    @Exported
    public boolean isAllowRebuild() {
        return allowRebuild;
    }

    public void setAllowRebuild(boolean allowRebuild) {
        this.allowRebuild = allowRebuild;
    }

    @Exported
    public boolean isLinkToConsoleLog() {
        return linkToConsoleLog;
    }

    public void setLinkToConsoleLog(boolean linkToConsoleLog) {
        this.linkToConsoleLog = linkToConsoleLog;
    }

    @Exported
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }

    @Exported
    public boolean isRelativeLinksEnabled() {
        return relativeLinksEnabled;
    }

    public void setRelativeLinksEnabled(boolean relativeLinksEnabled) {
        this.relativeLinksEnabled = relativeLinksEnabled;
    }

    @Exported
    public boolean isShowAggregatedChangeLog() {
        return showAggregatedChangeLog;
    }

    public void setShowAggregatedChangeLog(boolean showAggregatedChangeLog) {
        this.showAggregatedChangeLog = showAggregatedChangeLog;
    }

    public boolean isShowAggregatedPipeline() {
        return showAggregatedPipeline;
    }

    public void setShowAggregatedPipeline(boolean showAggregatedPipeline) {
        this.showAggregatedPipeline = showAggregatedPipeline;
    }

    public boolean isShowChangeLog() {
        return showChangeLog;
    }

    public void setShowChangeLog(boolean showChangeLog) {
        this.showChangeLog = showChangeLog;
    }

    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

    @Exported
    public boolean isShowPromotions() {
        return showPromotions;
    }

    public void setShowPromotions(boolean showPromotions) {
        this.showPromotions = showPromotions;
    }

    @Exported
    public boolean isShowStaticAnalysisResults() {
        return showStaticAnalysisResults;
    }

    public void setShowStaticAnalysisResults(boolean showStaticAnalysisResults) {
        this.showStaticAnalysisResults = showStaticAnalysisResults;
    }

    @Exported
    public boolean isShowTestResults() {
        return showTestResults;
    }

    public void setShowTestResults(boolean showTestResults) {
        this.showTestResults = showTestResults;
    }

    @Exported
    public boolean isShowTotalBuildTime() {
        return showTotalBuildTime;
    }

    public void setShowTotalBuildTime(boolean showTotalBuildTime) {
        this.showTotalBuildTime = showTotalBuildTime;
    }

    public int getMaxNumberOfVisiblePipelines() {
        return maxNumberOfVisiblePipelines;
    }

    public void setMaxNumberOfVisiblePipelines(int maxNumberOfVisiblePipelines) {
        this.maxNumberOfVisiblePipelines = maxNumberOfVisiblePipelines;
    }

    public int getNoOfPipelinesPerComponent() {
        return noOfPipelinesPerComponent;
    }

    public void setNoOfPipelinesPerComponent(int noOfPipelinesPerComponent) {
        this.noOfPipelinesPerComponent = noOfPipelinesPerComponent;
    }

    public int getNoOfColumnsDisplayed() {
        return noOfColumnsDisplayed;
    }

    public void setNoOfColumnsDisplayed(int noOfColumnsDisplayed) {
        this.noOfColumnsDisplayed = noOfColumnsDisplayed;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public List<ComponentSpec> getComponentSpecs() {
        return componentSpecs;
    }

    public void setComponentSpecs(List<ComponentSpec> componentSpecs) {
        this.componentSpecs = componentSpecs;
    }

    public List<se.diabol.jenkins.pipeline.DeliveryPipelineView.RegExpSpec> getFirstJobRegExpSpecification() {
        return firstJobRegExpSpecification;
    }

    public void setFirstJobRegExpSpecification(List<se.diabol.jenkins.pipeline.DeliveryPipelineView.RegExpSpec> firstJobRegExpSpecification) {
        this.firstJobRegExpSpecification = firstJobRegExpSpecification;
    }

    @Exported
    public String getAggregatedChangeLogGroupingPattern() {
        return aggregatedChangeLogGroupingPattern;
    }

    public void setAggregatedChangeLogGroupingPattern(String aggregatedChangeLogGroupingPattern) {
        this.aggregatedChangeLogGroupingPattern = aggregatedChangeLogGroupingPattern;
    }

    public String getCustomCss() {
        return customCss;
    }

    public void setCustomCss(String customCss) {
        this.customCss = customCss;
    }

    public String getCustomFullScreenCss() {
        return customFullScreenCss;
    }

    public void setCustomFullScreenCss(String customFullScreenCss) {
        this.customFullScreenCss = customFullScreenCss;
    }

    @Exported
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSortingStrategyClass() {
        return sortingStrategyClass;
    }

    public void setSortingStrategyClass(String sortingStrategyClass) {
        this.sortingStrategyClass = sortingStrategyClass;
    }

    @Exported
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private void addJobsFromComponentSpecifications(Set<TopLevelItem> jobs) {
        if (componentSpecs != null) {
            for (ComponentSpec cs : componentSpecs) {
                Project first = Projects.resolve(cs.getInitialJob(), getOwnerItemGroup());
                Set<Project> lastJobs = new HashSet<>();
                cs.getLastJobs().forEach(lastJob -> lastJobs.add(Projects.resolve(lastJob, getOwnerItemGroup())));

                Collection<Project> downstreamProjects = Projects.getAllDownstreamProjects(first, lastJobs).values();
                downstreamProjects.forEach(project -> jobs.add((TopLevelItem) project.getDelegate()));
            }
        }
    }

    private void addRegexpFirstJobs(Set<TopLevelItem> jobs) {
        if (firstJobRegExpSpecification != null) {
            for (se.diabol.jenkins.pipeline.DeliveryPipelineView.RegExpSpec spec : firstJobRegExpSpecification) {
                Collection<Project> projects = Projects.getProjects(spec.getRegexp()).values();
                projects.forEach(project -> jobs.add((TopLevelItem) project.getDelegate()));
            }
        }
    }

    public ItemGroup<? extends TopLevelItem> getOwnerItemGroup() {
        return getOwner().getItemGroup();
    }

    @Extension
    @SuppressWarnings("unused")
    public static class ItemListenerImpl extends ItemListener {

        @Override
        public void onRenamed(Item item, String oldName, String newName) {
            notifyView(item, oldName, newName);
        }

        @Override
        public void onDeleted(Item item) {
            notifyView(item, item.getFullName(), null);
        }

        private void notifyView(Item item, String oldName, String newName) {
            Collection<View> views = JenkinsUtil.getInstance().getViews();
            for (View view : views) {
                if (view instanceof DeliveryPipelineView) {
                    ((DeliveryPipelineView) view).onProjectRenamed(item, oldName, newName);
                }
            }
        }
    }
}
