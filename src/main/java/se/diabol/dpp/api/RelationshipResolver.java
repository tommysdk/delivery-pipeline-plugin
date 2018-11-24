package se.diabol.dpp.api;

import hudson.ExtensionPoint;
import se.diabol.dpp.model.Project;
import se.diabol.jenkins.pipeline.util.JenkinsUtil;

import java.util.List;

public abstract class RelationshipResolver implements ExtensionPoint {

    /**
     * Returns the downstream projects for the given project
     *
     * @param project the project which to resolve downstream dependencies for
     * @return the downstream projects for the given project
     */
    public abstract List<Project> getDownstreamProjects(Project project);

    /**
     * Returns a list of all loaded implementations of this extension point
     *
     * @return a list of all loaded implementations of this extension point
     */
    public static List<RelationshipResolver> all() {
        return JenkinsUtil.getInstance().getExtensionList(RelationshipResolver.class);
    }
}
