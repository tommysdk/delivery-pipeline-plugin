package se.diabol.dpp.model;

import se.diabol.dpp.core.AbstractItem;
import se.diabol.jenkins.pipeline.domain.Change;
import se.diabol.jenkins.workflow.model.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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


    // TODO: Implement class
    
    public static class Builder {

        private long id;
        private int row;
        private int column;
        private List<Stage> downstreamStages;
        private List<Long> downstreamStageIds;
        private List<Task> tasks;
        private Map<String, List<String>> taskConnections;
        private Set<Change> changes = new HashSet<>();
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

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }
    }
}
