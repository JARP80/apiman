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
package io.apiman.manager.test;

import io.apiman.manager.test.util.AbstractTestPlanTest;

import org.junit.Test;

/**
 * Runs the "plugins" test plan.
 *
 * @author eric.wittmann@redhat.com
 */
@SuppressWarnings("nls")
public class PluginsTest extends AbstractTestPlanTest {

    @Test
    public void test() {
        System.setProperty("apiman.test.m2-path", "src/test/resources/test-plan-data/plugins/m2");
        runTestPlan("test-plans/plugins-testPlan.xml", PluginsTest.class.getClassLoader()); //$NON-NLS-1$
    }

}
