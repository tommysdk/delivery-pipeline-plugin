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
