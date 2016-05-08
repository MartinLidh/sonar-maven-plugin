/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by Vandeperre Maarten on 01/05/2016.
 */
public class TestSonarQualityGateServiceImpl {
    private static final String DUMMY_PROJECT_KEY = "DUMMY_PROJECT_KEY";
    private static final String DUMMY_BRANCH_NAME = "DUMMY_BRANCH_NAME";
    private static final String METRIC_KEY_QUALITY_GATE = "quality_gate_details";
    ArgumentCaptor<ResourceQuery> RESOURCE_QUERY_CAPTOR = ArgumentCaptor.forClass( ResourceQuery.class );

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();
    private final Sonar sonar = spy( Sonar.create( "DUMMY_SERVER", "DUMMY_USER", "DUMMY_PASSWD" ) );

    @Test
    public void qualityGateMet() throws Throwable {
        doReturn( resource( SonarQualityGateResponses.OK ) ).when( sonar ).find( RESOURCE_QUERY_CAPTOR.capture() );
        qualityGateService.validateQualityGate( sonar, DUMMY_PROJECT_KEY );
        assertEquals( DUMMY_PROJECT_KEY, RESOURCE_QUERY_CAPTOR.getValue().getResourceKeyOrId() );
    }

    @Test
    public void tooManyViolations() throws Throwable {
        doReturn( resource( SonarQualityGateResponses.CRITICAL_VIOLATIONS_TOO_HIGH ) ).when( sonar ).find( RESOURCE_QUERY_CAPTOR.capture() );
        try {
            qualityGateService.validateQualityGate( sonar, DUMMY_PROJECT_KEY );
            fail( "no error" );
        }
        catch ( final SonarQualityException e ) {
            System.out.print( e.getLocalizedMessage() );
            assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
            assertTrue( e.getLocalizedMessage().contains( "critical_violations" ) );
            assertTrue( e.getLocalizedMessage().contains( "\"op\":\"LT\"" ) );
            assertTrue( e.getLocalizedMessage().contains( "\"period\":3" ) );
            assertTrue( e.getLocalizedMessage().contains( "\"error\":\"1\"" ) );
        }
    }

    @Test
    public void noConditionsInServerResponseOnError() throws Throwable {
        doReturn( resource( SonarQualityGateResponses.ERROR_WITHOUT_CONDITIONS ) ).when( sonar ).find( RESOURCE_QUERY_CAPTOR.capture() );
        try {
            qualityGateService.validateQualityGate( sonar, DUMMY_PROJECT_KEY );
            fail( "no error" );
        }
        catch ( final SonarQualityException e ) {
            System.out.print( e.getLocalizedMessage() );
            assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
            assertFalse( e.getLocalizedMessage().toLowerCase().contains( "conditions".toLowerCase() ) );
        }
    }

    @Test
    public void noConditionsArrayInServerResponseOnError() throws Throwable {
        doReturn( resource( SonarQualityGateResponses.ERROR_WITH_CONDITIONS_AS_NON_ARRAY ) ).when( sonar ).find( RESOURCE_QUERY_CAPTOR.capture() );
        try {
            qualityGateService.validateQualityGate( sonar, DUMMY_PROJECT_KEY );
            fail( "no error" );
        }
        catch ( final SonarQualityException e ) {
            System.out.print( e.getLocalizedMessage() );
            assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
            assertFalse( e.getLocalizedMessage().toLowerCase().contains( "conditions".toLowerCase() ) );
        }
    }

    @Test
    public void invalidJson() throws Throwable {
        doReturn( resource( "{tsetdit = invalid json}" ) ).when( sonar ).find( RESOURCE_QUERY_CAPTOR.capture() );
        try {
            qualityGateService.validateQualityGate( sonar, DUMMY_PROJECT_KEY );
            fail( "no error" );
        }
        catch ( final SonarQualityException e ) {
            assertTrue( e.getLocalizedMessage().contains( "Unexpected character" ) );
        }
    }

    @Test
    public void composeProjectKey() throws Throwable {
        assertThat( qualityGateService.composeSonarProjectKey( null, DUMMY_BRANCH_NAME ), equalTo( null ) );
        assertThat( qualityGateService.composeSonarProjectKey( DUMMY_PROJECT_KEY, null ), equalTo( DUMMY_PROJECT_KEY ) );
        assertThat( qualityGateService.composeSonarProjectKey( DUMMY_PROJECT_KEY, DUMMY_BRANCH_NAME ), equalTo( DUMMY_PROJECT_KEY + ":" + DUMMY_BRANCH_NAME ) );
    }

    private Resource resource( final String qualityGateResult ) {
        final Measure measure = new Measure();
        measure.setMetricKey( METRIC_KEY_QUALITY_GATE ).getValue();
        measure.setData( qualityGateResult );
        final Resource resource = new Resource();
        resource.setMeasures( Collections.singletonList( measure ) );
        return resource;
    }
}
