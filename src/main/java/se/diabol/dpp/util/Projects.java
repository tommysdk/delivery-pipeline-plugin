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
package se.diabol.dpp.util;

import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import se.diabol.dpp.api.RelationshipResolver;
import se.diabol.dpp.model.Project;
import se.diabol.jenkins.pipeline.domain.PipelineException;
import se.diabol.jenkins.pipeline.util.JenkinsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class Projects {

    private static final Logger LOG = Logger.getLogger(Projects.class.getName());

    private Projects() {
    }

    public static AbstractProject<?, ?> getAbstractProject(String name, ItemGroup context) {
        return JenkinsUtil.getInstance().getItem(name, context, AbstractProject.class);
    }

    public static WorkflowJob getWorkflowJob(String projectName, ItemGroup<? extends TopLevelItem> ownerItemGroup)
            throws PipelineException {
        Jenkins jenkins = JenkinsUtil.getInstance();
        WorkflowJob job = jenkins.getItem(projectName, ownerItemGroup, WorkflowJob.class);
        if (job == null) {
            throw new PipelineException("Could not find project: " + projectName);
        }
        return job;
    }

    public static List<Project> resolveAllSupportedProjects() {
        List<Project> projects = new ArrayList<>();
        List<AbstractProject> abstractProjects = JenkinsUtil.getInstance().getAllItems(AbstractProject.class);
        List<WorkflowJob> workflowJobs = JenkinsUtil.getInstance().getAllItems(WorkflowJob.class);
        abstractProjects.parallelStream().forEach(project -> projects.add(new Project(project, AbstractProject.class)));
        workflowJobs.parallelStream().forEach(project -> projects.add(new Project(project, WorkflowJob.class)));
        return projects;
    }

    public static Project resolve(String name, ItemGroup<? extends TopLevelItem> context) {
        Job project = getAbstractProject(name, context);
        if (project != null) {
            return new Project(project, AbstractProject.class);
        }
        try {
            project = getWorkflowJob(name, context);
            if (project != null) {
                return new Project(project, WorkflowJob.class);
            }
        } catch (PipelineException ignored) {
        }
        return Project.EMPTY_PROJECT;
    }

    public static Map<String, Project> getProjects(String regexp) {
        if (regexp == null || regexp.trim().equals("")) {
            return Collections.emptyMap();
        }
        try {
            Pattern pattern = Pattern.compile(regexp);
            Map<String, Project> result = new HashMap<>();
            for (Project project : resolveAllSupportedProjects()) {
                Matcher matcher = pattern.matcher(project.getName());
                if (matcher.find()) {
                    if (matcher.groupCount() >= 1) {
                        String name = matcher.group(1);
                        result.put(name, project);
                    } else {
                        LOG.log(Level.WARNING, "Could not find expected match group");
                    }
                }
            }
            return result;
        } catch (PatternSyntaxException e) {
            LOG.log(Level.WARNING, "Could not find projects using regular expression", e);
            return Collections.emptyMap();
        }
    }

    public static Map<String, Project> getAllDownstreamProjects(Project firstProject, Set<Project> lastProjects) {
        Map<String, Project> projects = new LinkedHashMap<>();
        for (Project lastProject : lastProjects) {
            projects.putAll(getAllDownstreamProjects(firstProject, lastProject, projects));
        }
        return projects;
    }

    private static Map<String, Project> getAllDownstreamProjects(Project first, Project last,
                                                                 Map<String, Project> projects) {
        if (first == null) {
            return projects;
        }

        if (projects.containsValue(first)) {
            return projects;
        }

        if (last != null && first.getName().equals(last.getName())) {
            projects.put(last.getName(), last);
            return projects;
        }

        projects.put(first.getName(), first);

        for (Project project : getDownstreamProjects(first)) {
            projects.putAll(getAllDownstreamProjects(project, last, projects));
        }

        return projects;
    }

    public static List<Project> getDownstreamProjects(Project project) {
        List<Project> result = new ArrayList<>();
        List<RelationshipResolver> resolvers = RelationshipResolver.all();
        for (RelationshipResolver resolver : resolvers) {
            result.addAll(resolver.getDownstreamProjects(project));
        }
        return result;
    }
}
