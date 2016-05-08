/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.GitException;
import com.viae.maven.sonar.services.GitService;
import com.viae.maven.sonar.services.GitServiceImpl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = "set-git-branch", aggregator = true)
public class SonarMavenSetGitBranchMojo extends AbstractMojo {

    @Component
    protected MavenProject project;
    private final GitService gitService = new GitServiceImpl(getLog());

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String sonarBranchName = gitService.getBranchName(Runtime.getRuntime());
            project.getProperties().setProperty("sonar.branch", sonarBranchName);
        } catch (GitException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
