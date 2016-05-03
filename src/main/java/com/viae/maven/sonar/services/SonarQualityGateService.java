/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;

/**
 * Service to validate (a) SONAR quality gate(s).
 *
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public interface SonarQualityGateService {
	/**
     * Validate if the quality gate linked to the given project is passed.
     *
     * @param sonar, the SONAR configuration.
     * @param projectKey, the identifier of the project (e.g. groupId:ArtifactId:branchId).
     * @throws SonarQualityException will be thrown when the given project doesn't pass the linked quality gate.
     */
    void validateQualityGate(Sonar sonar, String projectKey) throws SonarQualityException;

	void linkQualityGateToProject(SonarClient client, String projectKey, String qualityGateName) throws SonarQualityException;

	/**
	 * Compose the project key used by SONAR based on the project key and an optional branch name.
	 *
	 * @param projectKey, the key of the project (i.e. groupId:artifactId).
	 * @param branchName, the branchName (optional)
	 * @return the project key known to SONAR
	 */
	String composeSonarProjectKey(String projectKey, String branchName);
}
