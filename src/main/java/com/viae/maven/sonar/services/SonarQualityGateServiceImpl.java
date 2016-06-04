/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.maven.project.MavenProject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sonar.wsclient.SonarClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public class SonarQualityGateServiceImpl implements SonarQualityGateService {
    private static final int SLEEP_INTERVAL = 100;

    private static final String LEVEL_ERROR = "ERROR";
    private static final String FIELD_LEVEL = "level";
    private static final String FIELD_CONDITIONS = "conditions";
    public static final String QUALITY_GATE_QUERY_URL = "/api/resources/index?metrics=quality_gate_details&format=json&resource=%s";

    @Override
    public void validateQualityGate( final SonarClient client,
                                     final String projectKey,
                                     final LocalDateTime executionStart,
                                     final int secondsToWait ) throws SonarQualityException {
        waitForNewPublishingOfSonarResults( client, projectKey, executionStart, secondsToWait );
        handleQualityGateState( client, projectKey );
    }

    public void handleQualityGateState( final SonarClient client, final String projectKey ) throws SonarQualityException {
        Validate.notNull( client, "The given sonar client can't be null" );
        Validate.notBlank( projectKey, "The given project key can't be blank" );

        try {
            final String qualityGateDetailsData = client.get( String.format( QUALITY_GATE_QUERY_URL, projectKey ) );
            if ( StringUtils.isNotBlank( qualityGateDetailsData ) ) {
                final JSONParser jsonParser = new JSONParser();
                final JSONObject jsonObject = (JSONObject) jsonParser.parse( qualityGateDetailsData );
                if (LEVEL_ERROR.equals(((String) jsonObject.get(FIELD_LEVEL)).toUpperCase())) {
                    final StringJoiner joiner = new StringJoiner( "\n" );
                    joiner.add("");
                    joiner.add("############################");
                    joiner.add("############################");
                    joiner.add("### quality gate not met ###");
                    joiner.add("############################");
                    joiner.add("############################");
                    final Object conditionsResponse = jsonObject.get( FIELD_CONDITIONS );
                    if ( conditionsResponse != null && conditionsResponse instanceof JSONArray ) {
                        joiner.add("Conditions:");
                        ((JSONArray) conditionsResponse).forEach(condition -> joiner.add(condition.toString()));
                    }
                    throw new SonarQualityException(joiner.toString());
                }
            }
        } catch (final ParseException e) {
            throw new SonarQualityException(e);
        }
    }

    protected final void waitForNewPublishingOfSonarResults( final SonarClient client,
                                                             final String projectKey,
                                                             final LocalDateTime executionStart,
                                                             final int secondsToWait ) throws SonarQualityException {
        Validate.notNull( client, "The given sonar client can't be null" );
        Validate.notBlank( projectKey, "The given project key can't be blank" );

        final LocalDateTime start = LocalDateTime.now();
        if ( executionStart != null ) {
            LocalDateTime lastRunTimeStamp = getLastRunTimeStamp( client, projectKey );
            while ( !lastRunTimeStamp.isAfter( executionStart ) ) {
                final long duration = Duration.between( start, LocalDateTime.now() ).getSeconds();
                if ( duration > secondsToWait ) {
                    throw new SonarQualityException(
                            String.format( "We waited for %s seconds, but no update on last run (i.e. date field) occurred.", duration ) );
                }
                sleep();
                lastRunTimeStamp = getLastRunTimeStamp( client, projectKey );
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep( SLEEP_INTERVAL );
        }
        catch ( final InterruptedException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void linkQualityGateToProject( final SonarClient client, final String projectKey, final String qualityGateName ) throws SonarQualityException {
        Validate.notNull(client, "The given Sonar client can't be null");
        Validate.notBlank(projectKey, "The given project key can't be null");
        Validate.notBlank(qualityGateName, "The given quality gate name can't be null");

        final String resourceDataJson = client.get( String.format( "/api/resources?format=json&resource=%s", projectKey ) );
        final String projectId = JsonUtil.getIdOnMainLevel( resourceDataJson );
        final String qualityGateJson = client.get( String.format( "/api/qualitygates/show?name=%s", qualityGateName ) );
        final String qualityGateId = JsonUtil.getIdOnMainLevel( qualityGateJson );
        if (StringUtils.isNotBlank(projectId) && StringUtils.isNotBlank(qualityGateId)) {
            final Map<String, Object> map = new ConcurrentHashMap<>();
            map.put( "gateId", qualityGateId );
            map.put( "projectId", projectId );
            client.post( "/api/qualitygates/select", map );
        }
    }

    @Override
    public LocalDateTime getLastRunTimeStamp( final SonarClient client, final String projectKey ) throws SonarQualityException {
        Validate.notNull( client, "The given Sonar client can't be null" );
        Validate.notBlank( projectKey, "The given project key can't be null" );

        final String resourceDataJson = client.get( String.format( "/api/resources?format=json&resource=%s", projectKey ) );
        final String dateStringValue = JsonUtil.getOnMainLevel( resourceDataJson, "date" );
        LocalDateTime result = null;
        if ( StringUtils.isNotBlank( dateStringValue ) ) {
            result = LocalDateTime.parse( dateStringValue, DATE_TIME_FORMATTER );
        }
        return result;
    }

    @Override
    public String composeSonarProjectKey( final MavenProject project, final String projectKey, final String branchName ) {
        String resultingKey = String.format( "%s:%s", project.getGroupId(), project.getArtifactId() );
        if (projectKey != null) {
            resultingKey = projectKey;
            if (StringUtils.isNotBlank(branchName)) {
                resultingKey += ":" + branchName;
            }
        }
        return resultingKey;
    }
}
