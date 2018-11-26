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

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.model.Result;
import org.kohsuke.stapler.export.Exported;
import se.diabol.dpp.core.AbstractItem;
import se.diabol.jenkins.pipeline.domain.Change;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Stage extends AbstractItem {

    private long id;
    private int row;
    private int column;
    private List<Stage> downstreamStages;
    private List<Long> downstreamStageIds;
    private List<Task> tasks;
    private Map<String, List<String>> taskConnections;
    private Set<Change> changes = new HashSet<>();
    private String version;
    // TODO: List<Task> for tasks or taskConnections map?

    Stage(String name) {
        super(name);
    }

    public static Stage getPrototypeStage(String name, List<Task> tasks) {
        Stage stage = new Stage(name);
        stage.setTasks(tasks);
        return stage;
    }

    public static List<Stage> extractStages(AbstractProject firstProject, AbstractProject lastProject) {
        // TODO: Adapt method signature and implement
        return null;
    }

    public Stage createAggregatedStage(ItemGroup context, AbstractProject firstProject) {
        // TODO: Adapt method signature and implement
        return null;
    }

    public Stage createLatestStage(ItemGroup context, AbstractBuild firstBuild) {
        // TODO: Adapt method signature and implement
        return null;
    }

    public static List<Stage> placeStages(AbstractProject firstProject, Collection<Stage> stages) {
        // TODO: Adapt method signature and implement
        return null;
    }

    protected static void sortByRowsCols(List<Stage> stages) {
        // TODO: Implement
        return;
    }

    @CheckForNull
    protected static Stage findStageForJob(String name, Collection<Stage> stages) {
        // TODO: Implement class
        return null;
    }

    @CheckForNull
    public AbstractBuild getHighestBuild(AbstractProject firstProject, ItemGroup context) {
        return getHighestBuild(firstProject, context, null);
    }

    @CheckForNull
    public AbstractBuild getHighestBuild(AbstractProject firstProject, ItemGroup context, Result minResult) {
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
    public List<Stage> getDownstreamStages() {
        return downstreamStages;
    }

    public void setDownstreamStages(List<Stage> downstreamStages) {
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
        private List<Stage> downstreamStages;
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

        public Builder withDownstreamStages(List<Stage> downstreamStages) {
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
