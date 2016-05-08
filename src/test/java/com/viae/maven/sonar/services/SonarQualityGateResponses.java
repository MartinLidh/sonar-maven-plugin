/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */
package com.viae.maven.sonar.services;

/**
 * Created by Vandeperre Maarten on 02/05/2016.
 */
public class SonarQualityGateResponses {
	public static final String OK = "{\"level\":\"OK\",\"conditions\":[]}";

	public static final String ERROR_WITHOUT_CONDITIONS = "" +
			"{\"level\":\"ERROR\"," +
			"}" +
			"";

	public static final String ERROR_WITH_CONDITIONS_AS_NON_ARRAY = "" +
			"{\"level\":\"ERROR\"," +
			"\"conditions\":" +
			"{  " +
			"\"metric\":\"critical_violations\"," +
			"\"op\":\"LT\"," +
			"\"period\":3," +
			"\"warning\":\"1\"," +
			"\"error\":\"1\"," +
			"\"actual\":\"0.0\"," +
			"\"level\":\"ERROR\"" +
			"}" +
			"}" +
			"";

	public static final String CRITICAL_VIOLATIONS_TOO_HIGH = "" +
			"{\"level\":\"ERROR\"," +
			"\"conditions\":[" +
			"{  " +
			"\"metric\":\"critical_violations\"," +
			"\"op\":\"LT\"," +
			"\"period\":3," +
			"\"warning\":\"1\"," +
			"\"error\":\"1\"," +
			"\"actual\":\"0.0\"," +
			"\"level\":\"ERROR\"" +
			"}" +
			"]" +
			"}" +
			"";
}

/*
<build>
    <plugins>
        <plugin>
            <groupId>com.viae.maven</groupId>
            <artifactId>sonar-build-braker-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <configuration>
                <sonarServer>http://sonar.projects.foreach.be/</sonarServer>
                <!--sonarKey>be.realinvestor:investor-modules</sonarKey-->
                <!--branchName>feature/SONAR-TEST-2</branchName-->
                <sonarKey>be.realinvestor:investor-modules:feature/SONAR-TEST-2</sonarKey>
                <sonarUser>buildserver</sonarUser>
                <sonarPassword>buildserver</sonarPassword>
            </configuration>
        </plugin>
    </plugins>
</build>
 */
