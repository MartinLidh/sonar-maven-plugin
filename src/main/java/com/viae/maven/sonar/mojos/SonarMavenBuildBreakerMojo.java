/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarPropertyNames;
import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonar.wsclient.SonarClient;

/**
 * MOJO to validate a project against a given quality gate.
 *
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = "validate-qualitygate", aggregator = true)
public class SonarMavenBuildBreakerMojo extends AbstractMojo {
    @Parameter(property = SonarPropertyNames.SERVER, required = true)
    protected String sonarServer;
    @Parameter(property = SonarPropertyNames.PROJECT_KEY, required = true)
    protected String sonarKey;
    @Parameter(property = SonarPropertyNames.BRANCH)
    protected String branchName;
    @Parameter(property = SonarPropertyNames.LOGIN, required = true)
    protected String sonarUser;
    @Parameter(property = SonarPropertyNames.PASSWORD, required = true)
    protected String sonarPassword;
    @Parameter(property = SonarPropertyNames.EXECUTION_START)
    protected String sonarExecutionStart;

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();

    /**
     * Validate a project against a given quality gate.
     *
     * @throws MojoExecutionException, will not be thrown.
     * @throws MojoFailureException, whll be thrown when the quality gate is not met by the given project.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final SonarClient client = SonarClient.builder()
                                              .url( sonarServer )
                                              .login( sonarUser )
                                              .password( sonarPassword )
                                              .build();
        try {
            getLog().info(String.format("validate quality gate for projectKey[%s] and branch [%s]", sonarKey, branchName));
            qualityGateService.validateQualityGate( client, qualityGateService.composeSonarProjectKey( sonarKey, branchName ) );
        }
        catch ( final SonarQualityException e ) {
            throw new MojoFailureException(e.getLocalizedMessage(), e);
        }
    }
}
