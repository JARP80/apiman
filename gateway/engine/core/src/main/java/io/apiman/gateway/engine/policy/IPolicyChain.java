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
package io.apiman.gateway.engine.policy;

import io.apiman.gateway.engine.beans.PolicyFailure;

/**
 * The interface given to policies that allows them to asynchronously
 * signal when events occur.
 *
 * @author eric.wittmann@redhat.com
 */
public interface IPolicyChain<T> {

    /**
     * Called by a policy when it has successfully completed applying itself.  This 
     * triggers the next policy in the chain.
     * @param serviceObject
     */
    public void doApply(T serviceObject);

    /**
     * Called by a policy when it has detected a violation or failure in the policy.  This
     * will stop the processing of policies and immediately return an failure response to
     * the originating client.
     * @param failure
     */
    public void doFailure(PolicyFailure failure);

    /**
     * Called by a policy when an unexpected and unrecoverable error is encountered.
     * @param error
     */
    public void throwError(Throwable error);

}
