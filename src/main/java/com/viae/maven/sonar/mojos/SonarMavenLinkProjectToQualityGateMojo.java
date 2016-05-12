/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonar.wsclient.SonarClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
@Mojo(name = "link-project-to-qualitygate", aggregator = true)
public class SonarMavenLinkProjectToQualityGateMojo extends AbstractMojo {
    @Parameter(property = "sonarServer", required = true)
    protected String sonarServer;
    @Parameter(property = "sonar.projectKey", required = true)
    protected String sonarKey;
    @Parameter(property = "sonar.login", required = true)
    protected String sonarUser;
    @Parameter(property = "sonar.password", required = true)
    protected String sonarPassword;
    @Parameter(property = "sonar.qualitygate", required = true)
    protected String qualityGateName;

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final SonarClient client = SonarClient.builder()
                .url(sonarServer)
                .login(sonarUser)
                .password(sonarPassword)
                .build();
        try {
            qualityGateService.linkQualityGateToProject(client, sonarKey, qualityGateName);
        } catch (SonarQualityException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
