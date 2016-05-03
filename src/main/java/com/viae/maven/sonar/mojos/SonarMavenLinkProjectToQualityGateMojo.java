/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonar.wsclient.SonarClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
@Mojo(name = "link-project-to-qualitygate", aggregator = true)
public class SonarMavenLinkProjectToQualityGateMojo extends AbstractMojo {
    @Parameter(property = "sonarServer", required = true)
    protected String sonarServer;
    @Parameter(property = "sonar.projectKey", required = true)
    protected String sonarKey;
    @Parameter(property = "sonar.branch")
    protected String branchName;
    @Parameter(property = "sonar.login", required = true)
    protected String sonarUser;
    @Parameter(property = "sonar.password", required = true)
    protected String sonarPassword;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final SonarClient client = SonarClient.builder()
                .url( "http://sonar.projects.foreach.be" )
                .login( "admin" )
                .password( "tmp1022!" )
                .build();
        final Map<String, Object> map = new ConcurrentHashMap();
        map.put( "gateId", "2" );
        map.put( "projectId", "22295" );
        String result = client.get( "/api/resources?format=json&resource=be.resto:user-module-project:master" );
        //{"id":22295,"key":"be.resto:user-module-project:master","name":"be.resto:user-module-project master","scope":"PRJ","qualifier":"TRK","date":"2016-05-03T14:04:45+0200","creationDate":"2016-05-02T16:32:23+0200","lname":"be.resto:user-module-project master","version":"0.0.1-SNAPSHOT","branch":"master","description":""}]
        client.post( "/api/qualitygates/select", map );
        result = client.get( "/api/qualitygates/show?name=NgQualityGate" );
        //{"id":2,"name":"NgQualityGate","conditions":[{"id":9,"metric":"new_coverage","op":"LT","warning":"","error":"90","period":3}]}
        System.out.println( "..." + result ); // no result
    }
}
