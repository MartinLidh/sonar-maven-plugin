/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

/**
 * Exception that will be thrown when a SONAR quality check fails.
 *
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public class SonarQualityException extends Exception {
    public SonarQualityException(final String errorMessage){
        super(errorMessage);
    }
    public SonarQualityException(final Exception cause){
        super(cause);
    }
}
