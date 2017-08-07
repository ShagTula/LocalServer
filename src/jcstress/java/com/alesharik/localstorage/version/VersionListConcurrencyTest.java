package com.alesharik.localstorage.version;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class VersionListConcurrencyTest {

    @SuppressWarnings("WeakerAccess")
    @State
    public static class AddingConsistencyState {
        private final VersionList versionList = new VersionList();
        private final AtomicLong count = new AtomicLong();
        private final AtomicInteger counter2 = new AtomicInteger();
        private final CopyOnWriteArraySet<Version> versions = new CopyOnWriteArraySet<>();
    }

    @JCStressTest
    @Description("This test represents real usage case with 1 producer and many consumers")
    @Outcome(id = "0, 0, true, true", expect = Expect.ACCEPTABLE, desc = "Normal state")
    @Outcome(id = "1, 1, true, false", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Some desynchronization")
    @Outcome(id = "1, 1, false, false", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Some desynchronization and values are not all consistent")
    @Outcome(expect = Expect.FORBIDDEN, desc = "Inconsistent state!")
    public static final class AddingConsistencyTest {
        private static final File DUDE = new File("") {
            @Override
            public boolean delete() {
                return true;//Do nothing and don't break everything
            }
        };

        @Actor
        public void producer(AddingConsistencyState state) {
            state.count.updateAndGet(operand -> {
                Version version = new Version(1, 1, state.counter2.getAndIncrement(), Version.Prefix.UNSTABLE, DUDE);
                state.versionList.addVersion(version);
                state.versions.add(version);
                return operand + 1;
            });
        }

        @Actor
        public void consumer1(AddingConsistencyState state, JJZZ_Result result) {
            result.r1 = state.count.get() - state.versionList.size();
        }

        @Actor
        public void consumer2(AddingConsistencyState state, JJZZ_Result result) {
            result.r2 = state.count.get() - state.versionList.getVersions().size();
        }

        @Actor
        public void consumer3(AddingConsistencyState state, JJZZ_Result result) {
            for(Version version : state.versions) {
                result.r3 = result.r3 && state.versionList.contains(version);
            }
        }

        @Arbiter
        public void arbiter(AddingConsistencyState state, JJZZ_Result result) {
            result.r4 = (state.count.get() == 0) == state.versionList.isEmpty();
            for(Version version : state.versions) {
                state.count.updateAndGet(operand -> {
                    state.versionList.deleteVersion(version);
                    state.versions.remove(version);
                    return operand - 1;
                });
            }
            state.versions.clear();
        }
    }
}
