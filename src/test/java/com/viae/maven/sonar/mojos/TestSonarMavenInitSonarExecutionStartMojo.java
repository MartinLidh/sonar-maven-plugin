package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.services.SonarQualityGateService;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.wsclient.SonarClient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

/**
 * Tests for {@link SonarMavenInitSonarExecutionStartMojo}
 * <p>
 * Created by Maarten on 23/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSonarMavenInitSonarExecutionStartMojo {

	private static final String KEY = "sonar.execution.start";

	@Mock
	private MavenProject project;
	@Mock
	private SonarQualityGateService service;
	private Properties properties;

	private final SonarMavenInitSonarExecutionStartMojo mojo = new SonarMavenInitSonarExecutionStartMojo();

	@Before
	public void setupFreshFixture() throws Throwable {
		mojo.project = project;
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "user";
		mojo.sonarPassword = "password";
		mojo.sonarKey = KEY;
		properties = new Properties();
		doReturn( properties ).when( project ).getProperties();
		Field serviceField = SonarMavenInitSonarExecutionStartMojo.class.getDeclaredField( "qualityGateService" );
		serviceField.setAccessible( true );
		serviceField.set(mojo, service);
	}

	@Test
	public void setTimeStampWithoutLastRunTimestamp() throws Throwable {
		assertEquals( "guard assertion", null, project.getProperties().get( KEY ) );
		final int before = LocalDateTime.now().getNano();
		doReturn( null ).when( service ).getLastRunTimeStamp( any( SonarClient.class), anyString() );
		mojo.execute();
		final int after = LocalDateTime.now().getNano();
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertTrue( before + " <= " + startTimeString, before <= Integer.parseInt( startTimeString ) );
		assertTrue( after + " >= " + startTimeString, after >= Integer.parseInt( startTimeString ) );
	}

	@Test
	public void setTimeStampWithLastRunTimestamp() throws Throwable {
		assertEquals( "guard assertion", null, project.getProperties().get( KEY ) );
		final LocalDateTime first = LocalDateTime.MIN;
		doReturn( first ).when( service ).getLastRunTimeStamp( any( SonarClient.class), anyString() );
		mojo.execute();
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertEquals( first.getNano(), Integer.parseInt( startTimeString ) );
	}

	@Test
	public void keepTimeStamp() throws Throwable {
		final int original = 50;
		project.getProperties().setProperty( "sonar.execution.start", String.valueOf( original ) );
		assertEquals( "guard assertion", String.valueOf( original ), project.getProperties().get( KEY ) );
		mojo.execute();
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertEquals( original, Integer.parseInt( startTimeString ) );
	}
}
