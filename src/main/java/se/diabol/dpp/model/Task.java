package se.diabol.dpp.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import se.diabol.dpp.core.AbstractItem;
import se.diabol.jenkins.pipeline.domain.results.StaticAnalysisResult;
import se.diabol.jenkins.pipeline.domain.results.TestResult;
import se.diabol.jenkins.pipeline.domain.status.Status;
import se.diabol.jenkins.pipeline.domain.status.StatusType;
import se.diabol.jenkins.pipeline.domain.task.ManualStep;

import java.util.Collections;
import java.util.List;

@ExportedBean(defaultVisibility = AbstractItem.VISIBILITY)
public class Task extends AbstractItem {

    private boolean initial;
    private ManualStep manual;
    private List<StaticAnalysisResult> staticAnalysisResults;
    private List<String> downstreamTasks;
    private List<TestResult> testResults;
    private Project project;
    private Status status;
    private String buildId;
    private String id;
    private String link;
    private String description;

    private Task(String name) {
        super(name);
    }

    public static List<Task> resolve(Project project) {
        // TODO: Implement. Might need signature adjustments
        return Collections.emptyList();
    }

    @Exported
    public boolean isRequiringInput() {
        return StatusType.PAUSED_PENDING_INPUT.equals(status.getType());
    }

    @Exported
    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    @Exported
    public ManualStep getManual() {
        return manual;
    }

    public void setManual(ManualStep manual) {
        this.manual = manual;
    }

    @Exported
    public List<StaticAnalysisResult> getStaticAnalysisResults() {
        return staticAnalysisResults;
    }

    public void setStaticAnalysisResults(List<StaticAnalysisResult> staticAnalysisResults) {
        this.staticAnalysisResults = staticAnalysisResults;
    }

    @Exported
    public List<String> getDownstreamTasks() {
        return downstreamTasks;
    }

    public void setDownstreamTasks(List<String> downstreamTasks) {
        this.downstreamTasks = downstreamTasks;
    }

    @Exported
    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    @Exported
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Exported
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Exported
    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    @Exported
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exported
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Exported
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Builder {

        private boolean initial;
        private ManualStep manual;
        private List<StaticAnalysisResult> staticAnalysisResults;
        private List<String> downstreamTasks;
        private List<TestResult> testResults;
        private Project project;
        private String buildId;
        private String description;
        private String id;
        private String link;
        private String name;
        private Status status;

        public Builder() {
        }

        public Builder withInitial(boolean initial) {
            this.initial = initial;
            return this;
        }

        public Builder withManual(ManualStep manual) {
            this.manual = manual;
            return this;
        }

        public Builder withStaticAnalysisResults(List<StaticAnalysisResult> staticAnalysisResults) {
            this.staticAnalysisResults = staticAnalysisResults;
            return this;
        }

        public Builder withDownstreamTasks(List<String> downstreamTasks) {
            this.downstreamTasks = downstreamTasks;
            return this;
        }

        public Builder withTestResults(List<TestResult> testResults) {
            this.testResults = testResults;
            return this;
        }

        public Builder withProject(Project project) {
            this.project = project;
            return this;
        }

        public Builder withBuildId(String buildId) {
            this.buildId = buildId;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withLink(String link) {
            this.link = link;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Task build() {
            Task task = new Task(name);
            task.setInitial(initial);
            task.setManual(manual);
            task.setStaticAnalysisResults(staticAnalysisResults);
            task.setDownstreamTasks(downstreamTasks);
            task.setTestResults(testResults);
            task.setProject(project);
            task.setStatus(status);
            task.setBuildId(buildId);
            task.setId(id);
            task.setLink(link);
            task.setDescription(description);
            return task;
        }
    }
}
