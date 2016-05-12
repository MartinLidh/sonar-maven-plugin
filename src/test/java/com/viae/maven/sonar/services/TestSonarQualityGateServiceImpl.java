/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.Map;

import static com.viae.maven.sonar.services.SonarQualityGateResponses.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Vandeperre Maarten on 01/05/2016.
 */
public class TestSonarQualityGateServiceImpl {
    private static final String DUMMY_PROJECT_KEY = "DUMMY_PROJECT_KEY";
    private static final String DUMMY_BRANCH_NAME = "DUMMY_BRANCH_NAME";
    private static final String METRIC_KEY_QUALITY_GATE = "quality_gate_details";
    private final ArgumentCaptor<ResourceQuery> RESOURCE_QUERY_CAPTOR = ArgumentCaptor.forClass(ResourceQuery.class);
    private final ArgumentCaptor<Map> MAP_CAPTOR = ArgumentCaptor.forClass(Map.class);

    private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl();
    private final Sonar sonar = spy(Sonar.create("DUMMY_SERVER", "DUMMY_USER", "DUMMY_PASSWD"));
    private SonarClient client = mock(SonarClient.class);

    @Before
    public void setup() {
        reset(client);
    }

    @Test
    public void qualityGateMet() throws Throwable {
        doReturn(resource(SonarQualityGateResponses.OK)).when(sonar).find(RESOURCE_QUERY_CAPTOR.capture());
        qualityGateService.validateQualityGate(sonar, DUMMY_PROJECT_KEY);
        assertEquals(DUMMY_PROJECT_KEY, RESOURCE_QUERY_CAPTOR.getValue().getResourceKeyOrId());
    }

    @Test
    public void tooManyViolations() throws Throwable {
        doReturn(resource(SonarQualityGateResponses.CRITICAL_VIOLATIONS_TOO_HIGH)).when(sonar).find(RESOURCE_QUERY_CAPTOR.capture());
        try {
            qualityGateService.validateQualityGate(sonar, DUMMY_PROJECT_KEY);
            fail("no error");
        } catch (final SonarQualityException e) {
            System.out.print(e.getLocalizedMessage());
            assertTrue(e.getLocalizedMessage().contains("quality gate not met"));
            assertTrue(e.getLocalizedMessage().contains("critical_violations"));
            assertTrue(e.getLocalizedMessage().contains("\"op\":\"LT\""));
            assertTrue(e.getLocalizedMessage().contains("\"period\":3"));
            assertTrue(e.getLocalizedMessage().contains("\"error\":\"1\""));
        }
    }

    @Test
    public void noConditionsInServerResponseOnError() throws Throwable {
        doReturn(resource(SonarQualityGateResponses.ERROR_WITHOUT_CONDITIONS)).when(sonar).find(RESOURCE_QUERY_CAPTOR.capture());
        try {
            qualityGateService.validateQualityGate(sonar, DUMMY_PROJECT_KEY);
            fail("no error");
        } catch (final SonarQualityException e) {
            System.out.print(e.getLocalizedMessage());
            assertTrue(e.getLocalizedMessage().contains("quality gate not met"));
            assertFalse(e.getLocalizedMessage().toLowerCase().contains("conditions".toLowerCase()));
        }
    }

    @Test
    public void noConditionsArrayInServerResponseOnError() throws Throwable {
        doReturn(resource(SonarQualityGateResponses.ERROR_WITH_CONDITIONS_AS_NON_ARRAY)).when(sonar).find(RESOURCE_QUERY_CAPTOR.capture());
        try {
            qualityGateService.validateQualityGate(sonar, DUMMY_PROJECT_KEY);
            fail("no error");
        } catch (final SonarQualityException e) {
            System.out.print(e.getLocalizedMessage());
            assertTrue(e.getLocalizedMessage().contains("quality gate not met"));
            assertFalse(e.getLocalizedMessage().toLowerCase().contains("conditions".toLowerCase()));
        }
    }

    @Test
    public void invalidJson() throws Throwable {
        doReturn(resource("{tsetdit = invalid json}")).when(sonar).find(RESOURCE_QUERY_CAPTOR.capture());
        try {
            qualityGateService.validateQualityGate(sonar, DUMMY_PROJECT_KEY);
            fail("no error");
        } catch (final SonarQualityException e) {
            assertTrue(e.getLocalizedMessage().contains("Unexpected character"));
        }
    }

    @Test
    public void composeProjectKey() throws Throwable {
        assertThat(qualityGateService.composeSonarProjectKey(null, DUMMY_BRANCH_NAME), equalTo(null));
        assertThat(qualityGateService.composeSonarProjectKey(DUMMY_PROJECT_KEY, null), equalTo(DUMMY_PROJECT_KEY));
        assertThat(qualityGateService.composeSonarProjectKey(DUMMY_PROJECT_KEY, DUMMY_BRANCH_NAME), equalTo(DUMMY_PROJECT_KEY + ":" + DUMMY_BRANCH_NAME));
    }

    @Test
    public void linkQualityGateToProjectWithNullSonarClient() throws Throwable {
        try {
            qualityGateService.linkQualityGateToProject(null, RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
            fail("no error");
        } catch (NullPointerException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains("Sonar client"));
        }
    }

    @Test
    public void linkQualityGateToProjectWithNullProjectKey() throws Throwable {
        try {
            qualityGateService.linkQualityGateToProject(client, null, RandomStringUtils.randomAlphabetic(5));
            fail("no error");
        } catch (NullPointerException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains("project key"));
        }
    }

    @Test
    public void linkQualityGateToProjectWithEmptyProjectKey() throws Throwable {
        try {
            qualityGateService.linkQualityGateToProject(client, "", RandomStringUtils.randomAlphabetic(5));
            fail("no error");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains("project key"));
        }
    }

    @Test
    public void linkQualityGateToProjectWithNullQualityGateName() throws Throwable {
        try {
            qualityGateService.linkQualityGateToProject(client, RandomStringUtils.randomAlphabetic(5), null);
            fail("no error");
        } catch (NullPointerException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains("quality gate name"));
        }
    }

    @Test
    public void linkQualityGateToProjectWithEmptyQualityGateName() throws Throwable {
        try {
            qualityGateService.linkQualityGateToProject(client, RandomStringUtils.randomAlphabetic(5), "");
            fail("no error");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains("quality gate name"));
        }
    }

    @Test
    public void linkQualityGateToProjectWithDetailsAsList() throws Throwable {
        String qualityGateName = "qualityGateName";
        String projectKey = "projectKey";

        doReturn(PROJECT_DETAIL_AS_LIST).when(client).get("/api/resources?format=json&resource=projectKey");
        doReturn(QUALITY_GATE_DETAIL_AS_LIST).when(client).get("/api/qualitygates/show?name=qualityGateName");

        qualityGateService.linkQualityGateToProject(client, projectKey, qualityGateName);

        verify(client, times(1)).get("/api/resources?format=json&resource=projectKey");
        verify(client, times(1)).get("/api/qualitygates/show?name=qualityGateName");
        verify(client, times(1)).post(eq("/api/qualitygates/select"), MAP_CAPTOR.capture());

        Map postedMap = MAP_CAPTOR.getValue();
        assertThat(postedMap.get("gateId"), equalTo("2"));
        assertThat(postedMap.get("projectId"), equalTo("22295"));
    }

    @Test
    public void linkQualityGateToProject() throws Throwable {
        String qualityGateName = "qualityGateName";
        String projectKey = "projectKey";

        doReturn(PROJECT_DETAIL).when(client).get("/api/resources?format=json&resource=projectKey");
        doReturn(QUALITY_GATE_DETAIL).when(client).get("/api/qualitygates/show?name=qualityGateName");

        qualityGateService.linkQualityGateToProject(client, projectKey, qualityGateName);

        verify(client, times(1)).get("/api/resources?format=json&resource=projectKey");
        verify(client, times(1)).get("/api/qualitygates/show?name=qualityGateName");
        verify(client, times(1)).post(eq("/api/qualitygates/select"), MAP_CAPTOR.capture());

        Map postedMap = MAP_CAPTOR.getValue();
        assertThat(postedMap.get("gateId"), equalTo("2"));
        assertThat(postedMap.get("projectId"), equalTo("22295"));
    }

    private Resource resource(final String qualityGateResult) {
        final Measure measure = new Measure();
        measure.setMetricKey(METRIC_KEY_QUALITY_GATE).getValue();
        measure.setData(qualityGateResult);
        final Resource resource = new Resource();
        resource.setMeasures(Collections.singletonList(measure));
        return resource;
    }
}
