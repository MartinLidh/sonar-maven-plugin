/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.GlobalSettings;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * Created by Vandeperre Maarten on 08/05/2016.
 */
public class TestSonarMavenGitBranchMojo {

    private final SonarMavenSetGitBranchMojo mojo = new SonarMavenSetGitBranchMojo();
    private final MavenProject project = mock(MavenProject.class);
    private final Properties properties = mock(Properties.class);

    @Before
    public void setupFreshFixture(){
        reset(project);
        reset(properties);
        mojo.project = project;
        doReturn(properties).when(project).getProperties();
    }

    @Test
    public void happyPath() throws Throwable {
        mojo.execute();
        verify(properties, times(1)).setProperty("sonar.branch", GlobalSettings.BRANCH_NAME);
    }

    @Test
    public void doNotOverrideSonarBranchProperty() throws Throwable {
        //properties.
        mojo.execute();
        verify(properties, times(1)).setProperty("sonar.branch", "test-property");
    }
}
