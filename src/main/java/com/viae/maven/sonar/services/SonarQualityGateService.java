/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import org.sonar.wsclient.Sonar;

/**
 * Service to validate (a) SONAR quality gate(s).
 * @param sonar, the SONAR configuration.
 * @param projectKey, the identifier of the project (e.g. groupId:ArtifactId:branchId).
 *
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public interface SonarQualityGateService {
    void validateQualityGate(Sonar sonar, String projectKey) throws SonarQualityException;
}
