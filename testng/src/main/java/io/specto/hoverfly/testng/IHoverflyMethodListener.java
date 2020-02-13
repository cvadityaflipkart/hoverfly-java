package io.specto.hoverfly.testng;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;

import static io.specto.hoverfly.testng.HoverflyExtensionUtils.isAnnotatedWithHoverflyExtension;
import static io.specto.hoverfly.testng.HoverflyExtensionUtils.isAnnotatedWithNoDiffAssertion;

public interface IHoverflyMethodListener extends IInvokedMethodListener2 {

    @Override
    default void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        try {
            HoverflyExtension hoverflyExtension;
            if ((hoverflyExtension = isAnnotatedWithHoverflyExtension(method)) != null) {
                hoverflyExtension.before();
            }
            NoDiffAssertion noDiffAssertion;
            if ((noDiffAssertion = isAnnotatedWithNoDiffAssertion(method)) != null) {
                noDiffAssertion.cleanDiffs();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IHoverflyMethodListener: beforeInvocation, ", e);
        }
    }

    @Override
    default void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        try {
            NoDiffAssertion noDiffAssertion;
            if ((noDiffAssertion = isAnnotatedWithNoDiffAssertion(method)) != null) {
                noDiffAssertion.performAssertion();
            }
            HoverflyExtension hoverflyExtension;
            if ((hoverflyExtension = isAnnotatedWithHoverflyExtension(method)) != null) {
                hoverflyExtension.after();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IHoverflyMethodListener: afterInvocation, ", e);
        }
    }
}
