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

import jenkins.model.Jenkins;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.SimpleDirectedGraph;
import se.diabol.dpp.core.AbstractItem;
import se.diabol.dpp.util.StageEdgeFactory;
import se.diabol.jenkins.pipeline.domain.PipelineException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StageGraph {

    public static List<Stage> placeStages(Project firstProject, Collection<Stage> stages) throws PipelineException {
        DirectedGraph<Stage, Edge> graph = new SimpleDirectedGraph<>(new StageEdgeFactory());
        for (Stage stage : stages) {
            stage.setTaskConnections(getStageConnections(stage, stages));
            graph.addVertex(stage);
            List<Stage> downstreamStages = getDownstreamStagesForStage(stage, stages);
            List<String> downstreamStageNames = new ArrayList<>();
            List<Long> downstreamStageIds = new ArrayList<>();
            for (Stage downstream : downstreamStages) {
                downstreamStageNames.add(downstream.getName());
                downstreamStageIds.add(downstream.getId());
                graph.addVertex(downstream);
                graph.addEdge(stage, downstream, new Edge(stage, downstream));
            }
            stage.setDownstreamStages(downstreamStageNames);
            stage.setDownstreamStageIds(downstreamStageIds);

        }

        validateNoCircularDependencies(graph);

        List<List<Stage>> allPaths = Stage.findAllRunnablePaths(findStageForJob(firstProject.getRelativeNameFrom(
                Jenkins.getInstance()), stages), graph);
        allPaths.sort((stages1, stages2) -> stages2.size() - stages1.size());

        // keeps track of which row has an available column
        final Map<Integer, Integer> columnRowMap = new HashMap<>();
        final List<Stage> processedStages = new ArrayList<>();

        for (List<Stage> path : allPaths) {
            for (int column = 0; column < path.size(); column++) {
                Stage stage = path.get(column);

                //skip processed stage since the row/column has already been set
                if (!processedStages.contains(stage)) {
                    stage.setColumn(Math.max(stage.getColumn(), column));

                    final int effectiveColumn = stage.getColumn();

                    final Integer previousRowForThisColumn = columnRowMap.get(effectiveColumn);
                    // set it to 0 if no previous setting is set; if found, previous value + 1
                    final int currentRowForThisColumn = previousRowForThisColumn == null
                            ? 0 : previousRowForThisColumn + 1;
                    // update/set row number in the columnRowMap for this effective column
                    columnRowMap.put(effectiveColumn, currentRowForThisColumn);

                    stage.setRow(currentRowForThisColumn);

                    processedStages.add(stage);
                }
            }
        }

        List<Stage> result = new ArrayList<>(stages);

        Stage.sortByRowsAndColumns(result);

        return result;
    }

    /**
     * Validates that no circular dependencies exist. Throws a <tt>PipelineException</tt> if any circular
     * dependencies are detected
     *
     * @param graph the graph to validate
     * @throws PipelineException if any circular dependencies exist
     */
    protected static void validateNoCircularDependencies(DirectedGraph<Stage, Edge> graph) throws PipelineException {
        CycleDetector<Stage, Edge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) {
            Set<Stage> stageSet = cycleDetector.findCycles();
            String message = "Circular dependencies between stages: ";
            message += stageSet.stream().map(AbstractItem::getName).collect(Collectors.joining(", "));
            throw new PipelineException(message);
        }
    }

    private static Map<String, List<String>> getStageConnections(Stage stage, Collection<Stage> stages) {
        Map<String, List<String>> result = new HashMap<>();
        for (int i = 0; i < stage.getTasks().size(); i++) {
            Task task = stage.getTasks().get(i);
            for (int j = 0; j < task.getDownstreamTasks().size(); j++) {
                String downstreamTask = task.getDownstreamTasks().get(j);
                Stage target = Stage.findStageForJob(downstreamTask, stages);
                if (!stage.equals(target)) {
                    if (result.get(task.getId()) == null) {
                        List<String> downstreamTaskList = new ArrayList<>();
                        downstreamTaskList.add(downstreamTask);
                        result.put(task.getId(), downstreamTaskList);
                    } else {
                        result.get(task.getId()).add(downstreamTask);
                    }
                }
            }
        }
        return result;
    }

    private static List<Stage> getDownstreamStagesForStage(Stage stage, Collection<Stage> stages) {
        List<Stage> result = new ArrayList<>();
        for (Task task : stage.getTasks()) {
            for (String jobName : task.getDownstreamTasks()) {
                Stage target = Stage.findStageForJob(jobName, stages);
                if (target != null && !target.getName().equals(stage.getName())) {
                    result.add(target);
                }
            }
        }
        return result;
    }
}
