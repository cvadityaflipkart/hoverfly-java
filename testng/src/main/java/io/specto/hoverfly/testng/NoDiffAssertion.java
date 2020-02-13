/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this classpath except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * <p>
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.testng;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;

/**
 * Asserts that there was no diff between any of the expected responses set by simulations and the actual responses
 * returned from the real service.
 */
public class NoDiffAssertion {

    private Hoverfly hoverfly;
    private HoverflyExtension hoverflyExtension;

    /**
     * Creates a rule with the given instance of {@link Hoverfly} that asserts that there was no diff between any of the
     * expected responses set by simulations and the actual responses returned from the real service.
     * The rule also removes (before and after the test execution) all possible diffs that are stored in Hoverfly.
     * This ensures that all test runs are executed in isolated and clean environment.
     *
     * @param hoverfly An instance of {@link Hoverfly} to be used for retrieving the diffs
     */
    public NoDiffAssertion(Hoverfly hoverfly) {
        if (hoverfly == null) {
            throw new IllegalArgumentException("Hoverfly cannot be null");
        }
        this.hoverfly = hoverfly;
    }

    /**
     * Creates a rule with the given instance of {@link HoverflyExtension} that asserts that there was no diff between any of the
     * expected responses set by simulations and the actual responses returned from the real service.
     * The rule also removes (before and after the test execution) all possible diffs that are stored in Hoverfly.
     * This ensures that all test runs are executed in isolated and clean environment.
     *
     * @param hoverflyExtension An instance of {@link HoverflyExtension} to be used for retrieving the diffs
     */
    public NoDiffAssertion(HoverflyExtension hoverflyExtension) {
        if (hoverflyExtension == null) {
            throw new IllegalArgumentException("HoverflyRule cannot be null");
        }
        this.hoverflyExtension = hoverflyExtension;
    }

    /**
     * Creates a rule with the given instance of {@link HoverflyConfig} that asserts that there was no diff between any of the
     * expected responses set by simulations and the actual responses returned from the real service.
     * The rule also removes (before and after the test execution) all possible diffs that are stored in Hoverfly.
     * This ensures that all test runs are executed in isolated and clean environment.
     *
     * @param hoverflyConfig An instance of {@link HoverflyConfig} to be used for retrieving the diffs
     */
    public NoDiffAssertion(HoverflyConfig hoverflyConfig) {
        if (hoverflyConfig == null) {
            throw new IllegalArgumentException("HoverflyConfig cannot be null");
        }
        hoverfly = new Hoverfly(hoverflyConfig, HoverflyMode.DIFF);
    }

    public void cleanDiffs() {
        if (hoverfly != null) {
            hoverfly.resetDiffs();
        } else {
            hoverflyExtension.resetDiffs();
        }
    }

    public void performAssertion() {
        if (hoverfly != null) {
            hoverfly.assertThatNoDiffIsReported(true);
        } else {
            hoverflyExtension.assertThatNoDiffIsReported();
        }
    }
}
