/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.StringJoiner;

/**
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public class SonarQualityGateServiceImpl implements SonarQualityGateService {

    public static final String METRIC_QUALITY_GATE_DETAILS = "quality_gate_details";
    public static final String LEVEL_ERROR = "ERROR";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_CONDITIONS = "conditions";

    @Override
    public void validateQualityGate(final Sonar sonar, final String projectKey) throws SonarQualityException {
        try {
            final ResourceQuery qualityGateDetails = ResourceQuery.createForMetrics( projectKey, METRIC_QUALITY_GATE_DETAILS );
            final Resource result = sonar.find( qualityGateDetails );
            if(result != null) {
                final String data = result.getMeasure( METRIC_QUALITY_GATE_DETAILS ).getData();
                final JSONParser jsonParser = new JSONParser();
                final JSONObject jsonObject = (JSONObject) jsonParser.parse( data );
                if ( LEVEL_ERROR.equals( ( (String) jsonObject.get( FIELD_LEVEL ) ).toUpperCase() ) ) {
                    final StringJoiner joiner = new StringJoiner( "\n" );
                    joiner.add( "" );
                    joiner.add( "############################" );
                    joiner.add( "############################" );
                    joiner.add( "### quality gate not met ###" );
                    joiner.add( "############################" );
                    joiner.add( "############################" );
                    joiner.add( "Conditions:" );
                    ( (JSONArray) jsonObject.get( FIELD_CONDITIONS ) ).stream().forEach( condition -> joiner.add( condition.toString() ) );
                    throw new SonarQualityException( joiner.toString() );
                }
            }
        }
        catch ( final ParseException e ) {
            throw new SonarQualityException( e );
        }
    }

    @Override
    public String composeSonarProjectKey( final String projectKey, final String branchName ) {
        String resultingKey = null;
        if ( projectKey != null ) {
            resultingKey = projectKey;
            if ( StringUtils.isNotBlank( branchName ) ) {
                resultingKey += ":" + branchName;
            }
        }
        return resultingKey;
    }
}
