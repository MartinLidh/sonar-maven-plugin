/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarStrings;
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
    @Parameter(property = SonarStrings.SERVER, required = true)
    protected String sonarServer;
    @Parameter(property = SonarStrings.PROJECT_KEY, required = true)
    protected String sonarKey;
    @Parameter(property = SonarStrings.BRANCH)
    protected String branchName;
    @Parameter(property = SonarStrings.LOGIN, required = true)
    protected String sonarUser;
    @Parameter(property = SonarStrings.PASSWORD, required = true)
    protected String sonarPassword;
    @Parameter(property = SonarStrings.EXECUTION_START)
    protected String sonarExecutionStart;

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();

    /**
     * Validate a project against a given quality gate.
     *
     * @throws MojoExecutionException will not be thrown.
     * @throws MojoFailureException whll be thrown when the quality gate is not met by the given project.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( String.format( "%s start execution of '%s'", SonarStrings.LOG_PREFIX, SonarStrings.MOJO_NAME_VALIDATE_QUALITY_GATE ) );
        getLog().info( String.format( "%s use sonar server '%s' and log in with user '%s'", SonarStrings.LOG_PREFIX, sonarServer, sonarUser ) );

        try {
            final SonarClient client = SonarClient.builder()
                                                  .url( sonarServer )
                                                  .login( sonarUser )
                                                  .password( sonarPassword )
                                                  .build();

            getLog().info(String.format("validate quality gate for projectKey[%s] and branch [%s]", sonarKey, branchName));
            String computedProjectKey = qualityGateService.composeSonarProjectKey( sonarKey, branchName );
            getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.PROJECT_KEY, sonarKey ) );
            getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.BRANCH, branchName ) );
            getLog().info( String.format( "%s computed project key: %s", SonarStrings.LOG_PREFIX, computedProjectKey ) );
            qualityGateService.validateQualityGate( client, computedProjectKey );
        }
        catch ( final SonarQualityException e ) {
            getLog().error( String.format( "%s %s", SonarStrings.LOG_PREFIX, e.getLocalizedMessage() ) );
            throw new MojoFailureException( String.format( "%s %s", SonarStrings.LOG_PREFIX, e.getLocalizedMessage() ), e );
        }
    }
}
