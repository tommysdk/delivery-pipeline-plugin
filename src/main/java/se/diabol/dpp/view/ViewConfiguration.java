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
