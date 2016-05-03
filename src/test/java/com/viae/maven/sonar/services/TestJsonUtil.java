/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.utils.JsonUtil;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
public class TestJsonUtil {
    private static final String INVALID_JSON = "{test = not a valid json";
    private static final String MEANINGLESS_JSON = "{\"name\" : \"Vandeperre\", \"lastName\" : \"Maarten\", \"company\" : \"VIAE\"}";

    @Test
    public void getIdOnMainLevel() throws Throwable {
        final String happyPath = "{\"id\":22295,\"key\":\"be.resto:user-module-project:master\",\"name\":\"be.resto:user-module-project master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"be.resto:user-module-project master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}";
        assertThat(JsonUtil.getIdOnMainLevel(happyPath), equalTo("22295"));
        assertThat(JsonUtil.getIdOnMainLevel(null), nullValue());
        assertThat(JsonUtil.getIdOnMainLevel(MEANINGLESS_JSON), nullValue());
        try{
            JsonUtil.getIdOnMainLevel(INVALID_JSON);
            fail("no error");
        } catch(SonarQualityException e){
            assertThat(e.getLocalizedMessage().contains("Unexpected character"), is(true));
        }
    }

}
