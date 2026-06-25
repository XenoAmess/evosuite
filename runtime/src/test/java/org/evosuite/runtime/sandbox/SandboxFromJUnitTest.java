/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.runtime.sandbox;

import org.junit.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SandboxFromJUnitTest {

    private static ExecutorService executor;

    @BeforeClass
    public static void initEvoSuiteFramework() {
        // The EvoSuite sandbox is built on top of java.lang.SecurityManager,
        // which is deprecated for removal since JDK 17 and can no longer be
        // installed on JDK 17+. Skip the whole class in that case.
        Assume.assumeTrue(
                "SecurityManager-based sandbox is not supported on this JDK",
                securityManagerIsUsable());

        Assert.assertNull(System.getSecurityManager());

        Sandbox.initializeSecurityManagerForSUT();
        executor = Executors.newCachedThreadPool();

    }

    /**
     * Returns true if a custom {@link SecurityManager} can still be installed
     * on the current runtime. Since JDK 17 {@code System.setSecurityManager}
     * has been deprecated and starting with JDK 17 it throws
     * {@link UnsupportedOperationException} when an attempt is made to install
     * a non-null manager.
     */
    private static boolean securityManagerIsUsable() {
        SecurityManager current = System.getSecurityManager();
        SecurityManager probe = new SecurityManager() {
            @Override
            public void checkPermission(java.security.Permission perm) {
                // no-op; we just want to know whether installing one is allowed
            }
        };
        try {
            System.setSecurityManager(probe);
            return true;
        } catch (SecurityException | UnsupportedOperationException e) {
            return false;
        } finally {
            try {
                System.setSecurityManager(current);
            } catch (SecurityException | UnsupportedOperationException ignored) {
                // best effort restoration
            }
        }
    }

    @AfterClass
    public static void clearEvoSuiteFramework() {
        if (executor == null) {
            // initEvoSuiteFramework was skipped via Assume.assumeTrue()
            return;
        }
        Assert.assertNotNull(System.getSecurityManager());

        executor.shutdownNow();
        Sandbox.resetDefaultSecurityManager();

        Assert.assertNull(System.getSecurityManager());
    }

    @Before
    public void initTest() {
        Sandbox.goingToExecuteSUTCode();
        //TestGenerationContext.getInstance().goingToExecuteSUTCode();
    }

    @After
    public void doneWithTestCase() {
        Sandbox.doneWithExecutingSUTCode();
    }


    @Test
    public void testExit() throws Exception {

        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                //-------
                Foo foo = new Foo();
                try {
                    foo.tryToExit();
                    Assert.fail();
                } catch (SecurityException e) {
                    //expected
                }
                //-------
            }
        });
        future.get(5000, TimeUnit.MILLISECONDS);

    }

}


class Foo {

    public void tryToExit() {
        System.exit(0);
    }
}


