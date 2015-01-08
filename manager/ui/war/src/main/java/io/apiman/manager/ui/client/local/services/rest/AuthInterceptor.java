/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.manager.ui.client.local.services.rest;

import io.apiman.manager.api.rest.contract.IActionResource;
import io.apiman.manager.api.rest.contract.ICurrentUserResource;
import io.apiman.manager.api.rest.contract.IGatewayResource;
import io.apiman.manager.api.rest.contract.IOrganizationResource;
import io.apiman.manager.api.rest.contract.IPluginResource;
import io.apiman.manager.api.rest.contract.IPolicyDefinitionResource;
import io.apiman.manager.api.rest.contract.IRoleResource;
import io.apiman.manager.api.rest.contract.ISearchResource;
import io.apiman.manager.api.rest.contract.ISystemResource;
import io.apiman.manager.api.rest.contract.IUserResource;
import io.apiman.manager.ui.client.local.AppMessages;
import io.apiman.manager.ui.client.local.services.ConfigurationService;
import io.apiman.manager.ui.client.local.util.Base64Util;
import io.apiman.manager.ui.client.shared.beans.ApiAuthConfigurationBean;
import io.apiman.manager.ui.client.shared.beans.ApiAuthType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.interceptor.InterceptsRemoteCall;
import org.jboss.errai.enterprise.client.jaxrs.api.interceptor.RestCallContext;
import org.jboss.errai.enterprise.client.jaxrs.api.interceptor.RestClientInterceptor;
import org.jboss.errai.ui.client.local.spi.TranslationService;

/**
 * A REST interceptor that adds authentication to the REST invokation
 * pipeline.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@InterceptsRemoteCall({ ISystemResource.class, ISearchResource.class, ICurrentUserResource.class,
        IUserResource.class, IRoleResource.class, IOrganizationResource.class, IActionResource.class,
        IPolicyDefinitionResource.class, IGatewayResource.class, IPluginResource.class })
public class AuthInterceptor implements RestClientInterceptor {
    
    @Inject
    ConfigurationService config;
    @Inject
    TranslationService i18n;
    
    /**
     * Constructor.
     */
    public AuthInterceptor() {
    }

    /**
     * @see org.jboss.errai.common.client.api.interceptor.RemoteCallInterceptor#aroundInvoke(org.jboss.errai.common.client.api.interceptor.RemoteCallContext)
     */
    @Override
    public void aroundInvoke(RestCallContext context) {
        if (config == null) {
            throw new RuntimeException(i18n.format(AppMessages.CONFIG_SERVICE_NOT_AVAILABLE));
        }
        ApiAuthConfigurationBean auth = config.getCurrentConfig().getApi().getAuth();
        if (auth.getType() == ApiAuthType.basic) {
            doBasicAuth(context, auth);
        } else if (auth.getType() == ApiAuthType.samlBearerToken) {
            doSAMLBearerTokenAuth(context, auth);
        } else if (auth.getType() == ApiAuthType.bearerToken) {
            doBearerTokenAuth(context, auth);
        } else if (auth.getType() == ApiAuthType.authToken) {
            doAuthTokenAuth(context, auth);
        }
        context.proceed();
    }

    /**
     * Implementation of basic auth.
     * @param context
     * @param auth
     */
    private void doBasicAuth(RestCallContext context, ApiAuthConfigurationBean auth) {
        String encoded = Base64Util.b64encode(auth.getBasic().getUsername() + ":" + auth.getBasic().getPassword()); //$NON-NLS-1$
        context.getRequestBuilder().setIncludeCredentials(true);
        context.getRequestBuilder().setHeader("Authorization", "Basic " + encoded); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Implementation of SAML bearer token auth.
     * @param context
     * @param auth
     */
    private void doSAMLBearerTokenAuth(RestCallContext context, ApiAuthConfigurationBean auth) {
        String b64Token = auth.getBearerToken().getToken();
        String token = Base64Util.b64decode(b64Token);
        String encoded = Base64Util.b64encode("SAML-BEARER-TOKEN:" + token); //$NON-NLS-1$
        context.getRequestBuilder().setIncludeCredentials(true);
        context.getRequestBuilder().setHeader("Authorization", "Basic " + encoded); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Implementation of APIMan AuthToken auth.
     * @param context
     * @param auth
     */
    private void doAuthTokenAuth(RestCallContext context, ApiAuthConfigurationBean auth) {
        String b64Token = auth.getBearerToken().getToken();
        context.getRequestBuilder().setIncludeCredentials(true);
        context.getRequestBuilder().setHeader("Authorization", "AUTH-TOKEN " + b64Token); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Implementation of bearer token auth.
     * @param context
     * @param auth
     */
    private void doBearerTokenAuth(RestCallContext context, ApiAuthConfigurationBean auth) {
        String bearerToken = auth.getBearerToken().getToken();
        context.getRequestBuilder().setIncludeCredentials(true);
        context.getRequestBuilder().setHeader("Authorization", "Bearer " + bearerToken); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
