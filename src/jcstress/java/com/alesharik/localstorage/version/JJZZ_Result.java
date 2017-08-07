package com.alesharik.localstorage.version;

import lombok.EqualsAndHashCode;
import org.openjdk.jcstress.annotations.Result;

@EqualsAndHashCode
@SuppressWarnings("WeakerAccess")
@Result
public class JJZZ_Result {
    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public long r1;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public long r2;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public boolean r3;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public boolean r4;

    @Override
    public String toString() {
        return r1 + ", " + r2 + ", " + r3 + ", " + r4;
    }
}
