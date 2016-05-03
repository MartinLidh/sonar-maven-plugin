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
import org.sonar.wsclient.Sonar;

/**
 * MOJO to validate a project against a given quality gate.
 *
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = "validate-qualitygate", aggregator = true)
public class SonarMavenBuildBreakerMojo extends AbstractMojo {
    @Parameter(property = "sonarServer", required = true)
    protected String sonarServer;
    @Parameter(property = "sonar.projectKey", required = true)
    protected String sonarKey;
    @Parameter(property = "sonar.branch")
    protected String branchName;
    @Parameter(property = "sonar.login", required = true)
    protected String sonarUser;
    @Parameter(property = "sonar.password", required = true)
    protected String sonarPassword;

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Sonar sonar = Sonar.create(sonarServer, sonarUser, sonarPassword);
        try {
            getLog().info(String.format("validate quality gate for projectKey[%s] and branch [%s]", sonarKey, branchName));
            qualityGateService.validateQualityGate(sonar, qualityGateService.composeSonarProjectKey(sonarKey, branchName));
        } catch (SonarQualityException e) {
            throw new MojoFailureException(e.getLocalizedMessage(), e);
        }
    }
}
