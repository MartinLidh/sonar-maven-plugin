/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.utils;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
public class JsonUtil {
    private static final JSONParser jsonParser = new JSONParser();

    private JsonUtil() {
    }

    public static final String getIdOnMainLevel(final String qualityGateDetailJson) throws SonarQualityException {
        String id = null;
        if (StringUtils.isNotBlank(qualityGateDetailJson)) {
            final JSONObject json = parse( qualityGateDetailJson );
            if (json.containsKey("id")) {
                id = json.get("id").toString();
            }
        }
        return id;
    }

    public static final String getOnMainLevel( final String qualityGateDetailJson, final String fieldName ) throws SonarQualityException {
        String id = null;
        if ( StringUtils.isNotBlank( qualityGateDetailJson ) ) {
            final JSONObject json = parse( qualityGateDetailJson );
            if ( json.containsKey( fieldName ) ) {
                id = json.get( fieldName ).toString();
            }
        }
        return id;
    }

    private static JSONObject parse( final String json ) throws SonarQualityException {
        try {
            final Object jsonObject = jsonParser.parse( json );
            return jsonObject instanceof JSONObject ? (JSONObject) jsonObject : (JSONObject) ((JSONArray) jsonObject).get(0);
        }
        catch ( final ParseException e ) {
            throw new SonarQualityException(e);
        }
    }
}
