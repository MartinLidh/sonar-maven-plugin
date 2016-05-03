/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.utils;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
public class JsonUtil {
    private static final JSONParser jsonParser = new JSONParser();

    public static final String getIdOnMainLevel(final String qualityGateDetailJson) throws SonarQualityException {
        String id = null;
        if (StringUtils.isNotBlank(qualityGateDetailJson)) {
            JSONObject json = parse(qualityGateDetailJson);
            if (json.containsKey("id")) {
                id = json.get("id").toString();
            }
        }
        return id;
    }

    private static final JSONObject parse(final String json) throws SonarQualityException {
        try {
            return (JSONObject) jsonParser.parse(json);
        } catch (ParseException e) {
            throw new SonarQualityException(e);
        }
    }
}
