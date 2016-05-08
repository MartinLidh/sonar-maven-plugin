/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.GitException;
import com.viae.maven.sonar.services.GitService;
import com.viae.maven.sonar.services.GitServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

/**
 * Mojo to set the sonar.branch property to the git branch name (if the property is not defined).
 *
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = "set-git-branch", aggregator = true)
public class SonarMavenSetGitBranchMojo extends AbstractMojo {

    @Component
    protected MavenProject project;

    private final GitService gitService = new GitServiceImpl(getLog());

    /**
     * Set the sonar.branch property to the git branch name (if the property is not defined).
     * @throws MojoExecutionException, will be thrown when something goes wrong while retrieving the git branch name.
     * @throws MojoFailureException, will not be thrown
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if(StringUtils.isBlank(project.getProperties().getProperty("sonar.branch"))) {
                String sonarBranchName = gitService.getBranchName(Runtime.getRuntime());
                project.getProperties().setProperty("sonar.branch", sonarBranchName);
            }
        } catch (GitException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
