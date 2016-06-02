/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarPropertyNames;
import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonar.wsclient.SonarClient;

import java.time.LocalDateTime;

/**
 * Mojo to set the sonar.execution.start property to the last run timestamp.
 * <p>
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = "init-sonar-execution-start", aggregator = true)
public class SonarMavenInitSonarExecutionStartMojo extends AbstractMojo {

	@Component
	protected MavenProject project;

	@Parameter(property = SonarPropertyNames.SERVER, required = true)
	protected String sonarServer;
	@Parameter(property = SonarPropertyNames.PROJECT_KEY, required = true)
	protected String sonarKey;
	@Parameter(property = SonarPropertyNames.LOGIN, required = true)
	protected String sonarUser;
	@Parameter(property = SonarPropertyNames.PASSWORD, required = true)
	protected String sonarPassword;
	@Parameter(property = SonarPropertyNames.BRANCH)
	protected String branchName;

	private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();

	/**
	 * Set the sonar.execution.start property to the last run timestamp (if the property is not defined).
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( StringUtils.isBlank( project.getProperties().getProperty( SonarPropertyNames.EXECUTION_START ) ) ) {
			try {
				final SonarClient client = SonarClient.builder()
				                                      .url( sonarServer )
				                                      .login( sonarUser )
				                                      .password( sonarPassword )
				                                      .build();
				final LocalDateTime lastRunTimeStamp =
						qualityGateService.getLastRunTimeStamp( client, qualityGateService.composeSonarProjectKey( sonarKey, branchName ) );
				final LocalDateTime executionStart = lastRunTimeStamp != null ? lastRunTimeStamp : LocalDateTime.now();
				project.getProperties().setProperty( SonarPropertyNames.EXECUTION_START, String.valueOf( executionStart.getNano() ) );
			}
			catch ( final SonarQualityException e ) {
				throw new MojoFailureException( e.getLocalizedMessage(), e );
			}
		}
	}
}
