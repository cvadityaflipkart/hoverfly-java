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

import org.testng.IClassListener;
import org.testng.ITestClass;

import static io.specto.hoverfly.testng.HoverflyExtensionUtils.isAnnotatedWithHoverflyExtension;

public interface IHoverflyClassListener extends IClassListener {
    @Override
    default void onBeforeClass(ITestClass testClass) {
        try {
            HoverflyExtension hoverflyExtension;
            if ((hoverflyExtension = isAnnotatedWithHoverflyExtension(testClass)) != null) {
                hoverflyExtension.before();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IHoverflyClassListener:", e);
        }
    }

    @Override
    default void onAfterClass(ITestClass testClass) {
        try {
            HoverflyExtension hoverflyExtension;
            if ((hoverflyExtension = isAnnotatedWithHoverflyExtension(testClass)) != null) {
                hoverflyExtension.after();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IHoverflyClassListener:", e);
        }
    }
}
