package io.specto.hoverfly.testng;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;

import static io.specto.hoverfly.testng.HoverflyTestNGUtils.isAnnotatedWithHoverflyExtension;
import static io.specto.hoverfly.testng.HoverflyTestNGUtils.isAnnotatedWithNoDiffAssertion;

public interface IHoverflyMethodListener extends IInvokedMethodListener2 {

    @Override
    default void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        try {
            HoverflyTestNG hoverflyTestNG;
            if ((hoverflyTestNG = isAnnotatedWithHoverflyExtension(method)) != null) {
                hoverflyTestNG.before();
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
            HoverflyTestNG hoverflyTestNG;
            if ((hoverflyTestNG = isAnnotatedWithHoverflyExtension(method)) != null) {
                hoverflyTestNG.after();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IHoverflyMethodListener: afterInvocation, ", e);
        }
    }
}
