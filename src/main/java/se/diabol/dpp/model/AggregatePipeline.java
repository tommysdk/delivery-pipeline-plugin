package se.diabol.dpp.model;

import se.diabol.jenkins.pipeline.domain.Change;

import java.util.HashMap;
import java.util.Map;

public class AggregatePipeline extends Pipeline {

    private final Map<Stage, Change> stageChanges = new HashMap<>();

}
