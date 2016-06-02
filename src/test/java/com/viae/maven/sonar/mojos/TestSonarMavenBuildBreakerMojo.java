package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Maarten on 13/05/2016.
 */
public class TestSonarMavenBuildBreakerMojo {

	private final SonarMavenBuildBreakerMojo mojo = new SonarMavenBuildBreakerMojo();

	private final SonarQualityGateService service = spy( new SonarQualityGateServiceImpl() );
	private final ArgumentCaptor<String> projectKeyCaptor = ArgumentCaptor.forClass( String.class );

	@Before
	public void setupFreshFixture() {
		reset( service );
	}

	@Test
	public void breakBuild() throws Throwable {
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.branchName = "branchName";

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doThrow( new SonarQualityException( "quality gate not met" ) ).when( service ).validateQualityGate( any( SonarClient.class ), projectKeyCaptor.capture() );
		try {
			mojo.execute();
			fail( "no error" );
		}
		catch ( final MojoFailureException e ) {
			assertThat( e.getLocalizedMessage(), containsString( "quality gate not met" ) );
		}
		assertThat(projectKeyCaptor.getValue(), equalTo( "sonarKey:branchName" ));
	}

	@Test
	public void doNotreakBuild() throws Throwable {
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.branchName = "branchName";

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doNothing().when( service ).validateQualityGate( any( SonarClient.class ), projectKeyCaptor.capture() );
		mojo.execute();

		verify( service ).validateQualityGate( any( SonarClient.class ), anyString() );
	}

}
