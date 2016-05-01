/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

/**
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public class SonarQualityGateServiceImpl implements SonarQualityGateService {

    @Override
    public void validateQualityGate(final Sonar sonar, final String projectKey) throws SonarQualityException {
        try {
            final Resource result = sonar.find( ResourceQuery.createForMetrics( projectKey, "quality_gate_details" ) );
            final String data = result.getMeasure( "quality_gate_details" ).getData();
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse( data );
            System.out.println( data );
            System.out.println( jsonObject.get( "level" ) );
            if ( "ERROR".equals( ( (String) jsonObject.get( "level" ) ).toUpperCase() ) ) {
                throw new SonarQualityException( "quality gate not met" );
            }
        }
        catch ( final ParseException e ) {
            throw new SonarQualityException( e.getLocalizedMessage(), e );
        }
    }
}
