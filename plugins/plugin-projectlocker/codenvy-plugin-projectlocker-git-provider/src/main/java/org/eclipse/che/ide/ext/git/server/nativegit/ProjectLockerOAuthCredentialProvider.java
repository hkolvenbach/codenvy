/*
 * Copyright (c) [2012] - [2017] Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.ext.git.server.nativegit;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.api.git.CredentialsProvider;
import org.eclipse.che.api.git.UserCredential;
import org.eclipse.che.api.git.exception.GitException;
import org.eclipse.che.api.git.shared.ProviderInfo;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.security.oauth.shared.OAuthTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Credentials provider for ProjectLocker
 *
 * @author Max Shaposhnik
 */
@Singleton
public class ProjectLockerOAuthCredentialProvider implements CredentialsProvider {

  private static final Logger LOG =
      LoggerFactory.getLogger(ProjectLockerOAuthCredentialProvider.class);

  private static String OAUTH_PROVIDER_NAME = "projectlocker";
  public final Pattern PROJECTLOCKER_2_URL_PATTERN;

  private final OAuthTokenProvider oAuthTokenProvider;

  @Inject
  public ProjectLockerOAuthCredentialProvider(
      OAuthTokenProvider oAuthTokenProvider,
      @Named("oauth.projectlocker.git.pattern") String gitPattern) {
    this.oAuthTokenProvider = oAuthTokenProvider;
    this.PROJECTLOCKER_2_URL_PATTERN = Pattern.compile(gitPattern);
  }

  @Override
  public UserCredential getUserCredential() throws GitException {
    try {
      OAuthToken token =
          oAuthTokenProvider.getToken(
              OAUTH_PROVIDER_NAME, EnvironmentContext.getCurrent().getSubject().getUserId());
      if (token != null) {
        return new UserCredential(token.getToken(), token.getToken(), OAUTH_PROVIDER_NAME);
      }
    } catch (IOException e) {
      LOG.warn(e.getLocalizedMessage());
    }
    return null;
  }

  @Override
  public String getId() {
    return OAUTH_PROVIDER_NAME;
  }

  @Override
  public boolean canProvideCredentials(String url) {
    return PROJECTLOCKER_2_URL_PATTERN.matcher(url).matches();
  }

  @Override
  public ProviderInfo getProviderInfo() {
    return null;
  }
}
