/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.GitException;

/**
 * Interface to interact with the GIT system.
 *
 * Created by Vandeperre Maarten on 05/05/2016.
 */
public interface GitService {
    String getBranchName(Runtime runtime) throws GitException;
}
