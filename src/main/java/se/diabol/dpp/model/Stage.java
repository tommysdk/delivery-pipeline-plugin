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

    // TODO: Implement class
}
