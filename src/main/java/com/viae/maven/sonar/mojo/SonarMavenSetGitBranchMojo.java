/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojo;

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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final Process p = Runtime.getRuntime().exec( "git rev-parse --abbrev-ref HEAD" );
			p.waitFor();

			final BufferedReader reader = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

			String sonarBranchName = reader.readLine().trim();
			getLog().info( String.format( "set sonar.branch [%s]", sonarBranchName ) );
			project.getProperties().setProperty( "sonar.branch", sonarBranchName );
		}
		catch ( IOException | InterruptedException e ) {
			throw new MojoExecutionException( e.getLocalizedMessage(), e );
		}
	}
}
