/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.GlobalSettings;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by Vandeperre Maarten on 08/05/2016.
 */
public class TestSonarMavenGitBranchMojo {

    private final SonarMavenSetGitBranchMojo mojo = new SonarMavenSetGitBranchMojo();
    private final MavenProject project = mock(MavenProject.class);
    private Properties properties;

    @Before
    public void setupFreshFixture() {
        reset(project);
        mojo.project = project;
        properties = new Properties();
        doReturn(properties).when(project).getProperties();
    }

    @Test
    public void happyPath() throws Throwable {
        mojo.execute();
        assertThat(properties.getProperty(GlobalSettings.SONAR_BRANCH_PROPERTY_NAME), equalTo(GlobalSettings.BRANCH_NAME));
    }

    @Test
    public void doNotOverrideSonarBranchProperty() throws Throwable {
        properties.setProperty(GlobalSettings.SONAR_BRANCH_PROPERTY_NAME, "test-property");
        mojo.execute();
        assertThat(properties.getProperty(GlobalSettings.SONAR_BRANCH_PROPERTY_NAME), equalTo("test-property"));
    }
}
