/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public class SonarQualityGateServiceImpl implements SonarQualityGateService {

    private static final String METRIC_QUALITY_GATE_DETAILS = "quality_gate_details";
    private static final String LEVEL_ERROR = "ERROR";
    private static final String FIELD_LEVEL = "level";
    private static final String FIELD_CONDITIONS = "conditions";

    @Override
    public void validateQualityGate(final Sonar sonar, final String projectKey) throws SonarQualityException {
        try {
            final ResourceQuery qualityGateDetails = ResourceQuery.createForMetrics(projectKey, METRIC_QUALITY_GATE_DETAILS);
            final Resource result = sonar.find(qualityGateDetails);
            if (result != null) {
                final String data = result.getMeasure(METRIC_QUALITY_GATE_DETAILS).getData();
                final JSONParser jsonParser = new JSONParser();
                final JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
                if (LEVEL_ERROR.equals(((String) jsonObject.get(FIELD_LEVEL)).toUpperCase())) {
                    final StringJoiner joiner = new StringJoiner("\n");
                    joiner.add("");
                    joiner.add("############################");
                    joiner.add("############################");
                    joiner.add("### quality gate not met ###");
                    joiner.add("############################");
                    joiner.add("############################");
                    Object conditionsResponse = jsonObject.get(FIELD_CONDITIONS);
                    if(conditionsResponse != null && conditionsResponse instanceof JSONArray) {
                        joiner.add("Conditions:");
                        ((JSONArray) conditionsResponse).stream().forEach(condition -> joiner.add(condition.toString()));
                    }
                    throw new SonarQualityException(joiner.toString());
                }
            }
        } catch (final ParseException e) {
            throw new SonarQualityException(e);
        }
    }

    @Override
    public void linkQualityGateToProject(SonarClient client, String projectKey, String qualityGateName) throws SonarQualityException {
        String projectIdJson = client.get(String.format("/api/resources?format=json&resource=%s", projectKey));
        String projectId = JsonUtil.getIdOnMainLevel(projectIdJson);
        String qualityGateJson = client.get(String.format("/api/qualitygates/show?name=%s", qualityGateName));
        String qualityGateId = JsonUtil.getIdOnMainLevel(qualityGateJson);
        if (StringUtils.isNotBlank(projectId) && StringUtils.isNotBlank(qualityGateId)) {
            final Map<String, Object> map = new ConcurrentHashMap<>();
            map.put( "gateId", qualityGateId );
            map.put( "projectId", projectId );
            client.post( "/api/qualitygates/select", map );
        }
    }

    @Override
    public String composeSonarProjectKey(final String projectKey, final String branchName) {
        String resultingKey = null;
        if (projectKey != null) {
            resultingKey = projectKey;
            if (StringUtils.isNotBlank(branchName)) {
                resultingKey += ":" + branchName;
            }
        }
        return resultingKey;
    }
}
