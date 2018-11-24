package se.diabol.dpp.view;

import se.diabol.jenkins.pipeline.DeliveryPipelineView;

import java.io.Serializable;
import java.util.List;

public class ViewConfiguration implements Serializable {

    static final int DEFAULT_UPDATE_INTERVAL = 2;
    static final int DEFAULT_NO_OF_PIPELINES = 3;
    static final String DEFAULT_SORTING_STRATEGY = "none";
    static final int MAX_NO_OF_PIPELINES = 50;

    private boolean showAggregatedPipeline = false;
    private boolean showChangeLog = false;
    private boolean showTotalBuildTime = false;
    private boolean allowManualTriggers = false;
    private boolean allowRebuild = false;
    private boolean allowPipelineStart = false;
    private boolean allowAbort = false;
    private boolean showDescription = false;
    private boolean showPromotions = false;
    private boolean showTestResults = false;
    private boolean showStaticAnalysisResults = false;
    private boolean useRelativeLinks = false;
    private boolean pagingEnabled = false;
    private boolean showAggregatedChangeLog = false;
    private boolean linkToConsoleLog = false;

    private int noOfPipelinesPerComponent = DEFAULT_NO_OF_PIPELINES;
    private int noOfColumnsDisplayed = 1;
    private int updateInterval = DEFAULT_UPDATE_INTERVAL;
    private int maxNumberOfVisiblePipelines = -1;

    private List<ComponentSpec> componentSpecs;
    private List<DeliveryPipelineView.RegExpSpec> firstJobRegExpSpecification;

    private String sortingStrategyClass = DEFAULT_SORTING_STRATEGY;
    private String customFullScreenCss = null;
    private String customCss = null;
    private String aggregatedChangeLogGroupingPattern = null;
    private String description = null;

    private transient String error;
}
