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
