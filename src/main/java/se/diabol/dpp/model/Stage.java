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
package se.diabol.dpp.model;

import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import org.kohsuke.stapler.export.Exported;
import se.diabol.dpp.core.AbstractItem;
import se.diabol.dpp.util.Projects;
import se.diabol.dpp.util.Upstreams;
import se.diabol.jenkins.pipeline.PipelineProperty;
import se.diabol.jenkins.pipeline.domain.Change;
import se.diabol.jenkins.pipeline.domain.PipelineException;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class Stage extends AbstractItem {

    private long id;
    private int row;
    private int column;
    private List<String> downstreamStages;
    private List<Long> downstreamStageIds;
    private List<Task> tasks;
    private Map<String, List<String>> taskConnections;
    private Set<Change> changes = new HashSet<>();
    private String version;

    Stage(String name) {
        super(name);
    }

    public static Stage getPrototypeStage(String name, List<Task> tasks) {
        Stage stage = new Stage(name);
        stage.setTasks(tasks);
        return stage;
    }

    public static List<Stage> extractStages(Project firstProject, Set<Project> lastProjects) throws PipelineException {
        Map<String, Stage> stages = new LinkedHashMap<>();
        Map<String, Project> downstreamProjects = Projects.getAllDownstreamProjects(firstProject, lastProjects);
        for (Project project : downstreamProjects.values()) {
            String stageName = project.getDelegate().getDisplayName();

            Task task = null; // TODO Impl: Task.getPrototypeTask(project, project.getName().equals(firstProject.getName()));
            Predicate<Project> isLastProject = p -> p.getName().equals(project.getName());
            if (lastProjects.stream().anyMatch(isLastProject)) {
                task.getDownstreamTasks().clear();
            }

            PipelineProperty property = (PipelineProperty) project.getDelegate().getProperty(PipelineProperty.class);
            if (property == null && project.getDelegate().getParent() instanceof AbstractProject) {
                property = (PipelineProperty) ((AbstractProject)
                        project.getDelegate().getParent()).getProperty(PipelineProperty.class);
            }
            if (property != null && property.isStageNameNotEmpty()) {
                stageName = property.getStageName();
            }

            Stage stage = stages.get(stageName);
            if (stage == null) {
                stage = Stage.getPrototypeStage(stageName, Collections.emptyList());
            }

            LinkedList<Task> tasks = new LinkedList<>(stage.getTasks());
            tasks.addLast(task);
            stages.put(stageName, Stage.getPrototypeStage(stageName, tasks));
        }
        return StageGraph.placeStages(firstProject, stages.values());
    }

    public Stage createAggregatedStage(ItemGroup context, AbstractProject firstProject) {
        // TODO: Adapt method signature and implement
        return null;
    }

    public Stage createLatestStage(ItemGroup context, Run firstBuild) {
        List<Task> stageTasks = new ArrayList<>();
        for (Task task : getTasks()) {
            stageTasks.add(task);//TODO task.getLatestTask(context, firstBuild));
        }

        return new Stage.Builder()
                .withId(getId())
                .withName(getName())
                .withColumn(getColumn())
                .withRow(getRow())
                .withDownstreamStages(getDownstreamStages())
                .withDownstreamStageIds(getDownstreamStageIds())
                .withTasks(stageTasks)
                .withTaskConnections(getTaskConnections())
                .withVersion(getVersion())
                .build();
    }

    /**
     * Sorts the specified list of stages, first by rows, then by columns.
     *
     * @param stages the list of stages to sort based on their rows and column properties.
     */
    protected static void sortByRowsAndColumns(List<Stage> stages) {
        stages.sort((stage1, stage2) -> {
            int result = Integer.compare(stage1.getRow(), stage2.getRow());
            if (result == 0) {
                return Integer.compare(stage1.getColumn(), stage2.getColumn());
            } else {
                return result;
            }
        });
    }

    @CheckForNull
    protected static Stage findStageForJob(String name, Collection<Stage> stages) {
        for (Stage stage : stages) {
            if (stage.getTasks().stream().anyMatch(task -> task.getId().equals(name))) {
                return stage;
            }
        }
        return null;
    }

    @CheckForNull
    public Run getHighestBuild(Project firstProject, ItemGroup context) {
        return getHighestBuild(firstProject, context, null);
    }

    @CheckForNull
    public Run getHighestBuild(Project firstProject,
                               ItemGroup<? extends TopLevelItem> context,
                               Result minResult) {
        int highest = -1;
        for (Task task : getTasks()) {
            Project project = Projects.resolve(task.getId(), context);

            Run firstBuild = Upstreams.getFirstUpstreamBuild(project, firstProject, minResult);
            if (firstBuild != null && firstBuild.getNumber() > highest) {
                highest = firstBuild.getNumber();
            }
        }

        if (highest <= 0) {
            return null;
        }
        return firstProject.getDelegate().getBuildByNumber(highest);
    }

    @Exported
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Exported
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Exported
    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Exported
    public List<String> getDownstreamStages() {
        return downstreamStages;
    }

    public void setDownstreamStages(List<String> downstreamStages) {
        this.downstreamStages = downstreamStages;
    }

    @Exported
    public List<Long> getDownstreamStageIds() {
        return downstreamStageIds;
    }

    public void setDownstreamStageIds(List<Long> downstreamStageIds) {
        this.downstreamStageIds = downstreamStageIds;
    }

    @Exported
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Exported
    public Map<String, List<String>> getTaskConnections() {
        return taskConnections;
    }

    public void setTaskConnections(Map<String, List<String>> taskConnections) {
        this.taskConnections = taskConnections;
    }

    @Exported
    public Set<Change> getChanges() {
        return changes;
    }

    public void setChanges(Set<Change> changes) {
        this.changes = changes;
    }

    @Exported
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stage stage = (Stage) o;
        return id == stage.id &&
                row == stage.row &&
                column == stage.column &&
                Objects.equals(downstreamStages, stage.downstreamStages) &&
                Objects.equals(downstreamStageIds, stage.downstreamStageIds) &&
                Objects.equals(tasks, stage.tasks) &&
                Objects.equals(taskConnections, stage.taskConnections) &&
                Objects.equals(changes, stage.changes) &&
                Objects.equals(version, stage.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, row, column, downstreamStages, downstreamStageIds, tasks, taskConnections, changes, version);
    }

    @Override
    public String toString() {
        return "Stage{" +
                "id=" + id +
                ", name='" + super.getName() + '\'' +
                ", version='" + version + '\'' +
                ", tasks=" + tasks +
                '}';
    }

    public static class Builder {

        private long id;
        private int row;
        private int column;
        private List<String> downstreamStages;
        private List<Long> downstreamStageIds;
        private List<Task> tasks;
        private Map<String, List<String>> taskConnections;
        private Set<Change> changes = new HashSet<>();
        private String name;
        private String version;

        public Builder() {
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withRow(int row) {
            this.row = row;
            return this;
        }

        public Builder withColumn(int column) {
            this.column = column;
            return this;
        }

        public Builder withDownstreamStages(List<String> downstreamStages) {
            this.downstreamStages = downstreamStages;
            return this;
        }

        public Builder withDownstreamStageIds(List<Long> downstreamStageIds) {
            this.downstreamStageIds = downstreamStageIds;
            return this;
        }

        public Builder withTasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder withTaskConnections(Map<String, List<String>> taskConnections) {
            this.taskConnections = taskConnections;
            return this;
        }

        public Builder withChanges(Set<Change> changes) {
            this.changes = changes;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Stage build() {
            Stage stage = new Stage(name);
            stage.setId(id);
            stage.setRow(row);
            stage.setColumn(column);
            stage.setDownstreamStages(downstreamStages);
            stage.setDownstreamStageIds(downstreamStageIds);
            stage.setTasks(tasks);
            stage.setTaskConnections(taskConnections);
            stage.setChanges(changes);
            stage.setVersion(version);
            return stage;
        }
    }
}
