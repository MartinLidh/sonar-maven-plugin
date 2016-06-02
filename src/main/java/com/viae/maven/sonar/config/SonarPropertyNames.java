package com.viae.maven.sonar.config;

/**
 * Class to store and reuse SONAR property names
 * <p>
 * Created by Maarten on 28/05/2016.
 */
public class SonarPropertyNames {

	private SonarPropertyNames() {
	}

	public static final String EXECUTION_START = "sonar.execution.start";
	public static final String SERVER = "sonarServer";
	public static final String PROJECT_KEY = "sonar.projectKey";
	public static final String BRANCH = "sonar.branch";
	public static final String LOGIN = "sonar.login";
	public static final String PASSWORD = "sonar.password";
	public static final String QUALITY_GATE = "sonar.qualitygate";
}
