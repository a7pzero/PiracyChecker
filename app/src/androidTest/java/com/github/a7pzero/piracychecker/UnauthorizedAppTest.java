package com.github.a7pzero.piracychecker;

import android.content.Context;

import com.github.a7pzero.piracychecker.callbacks.PiracyCheckerCallback;
import com.github.a7pzero.piracychecker.demo.MainActivity;
import com.github.a7pzero.piracychecker.enums.PiracyCheckerError;
import com.github.a7pzero.piracychecker.enums.PirateApp;

import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * 2. Specific test cases for unauthorized apps. Requires to install an unauthorized app before
 * running this tests.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class UnauthorizedAppTest {

    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule =
        new ActivityTestRule<>(MainActivity.class);

    @Test
    public void verifyUnauthorizedCustomApp_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .addAppToCheck(new PirateApp("Demo",
                                                 uiThreadTestRule.getActivity().getPackageName() +
                                                     ".other"))
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                     error.toString());
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyUnauthorizedCustomApp_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .addAppToCheck(
                        new PirateApp("Demo", uiThreadTestRule.getActivity().getPackageName()))
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: There is a custom unauthorized app " +
                                     "installed.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.PIRATE_APP_INSTALLED)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyUnauthorizedApps_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableUnauthorizedAppsCheck()
                    .blockIfUnauthorizedAppUninstalled("piracychecker_preferences",
                                                       "app_unauthorized")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: There is an unauthorized app installed.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.PIRATE_APP_INSTALLED)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }
}
