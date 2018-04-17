package indi.noclay.cloudhealth.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import static indi.noclay.cloudhealth.util.UtilClass.*;

/**
 * Created by clay on 2018/4/17.
 */

public class TabSpec {
    public final String name;
    public final Class<? extends Fragment> cls;
    public final Bundle args;
    public final int position;
    public final Fragment mParent;

    public TabSpec(final String name, final Class<? extends Fragment> cls, final Bundle args, final int position, Fragment parent) {
        if (cls == null) throw new IllegalArgumentException("Fragment cannot be null!");
        if (name == null)
            throw new IllegalArgumentException("You must specify a name for this tab!");
        this.name = name;
        this.cls = cls;
        this.args = args;
        this.position = position;
        this.mParent = parent;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TabSpec)) return false;
        final TabSpec spec = (TabSpec) o;
        return objectEquals(name, spec.name) && classEquals(cls, spec.cls)
                && bundleEquals(args, spec.args) && position == spec.position;
    }

    @Override
    public String toString() {
        return "TabSpec [name=" + name + ", cls=" + cls + ", args=" + args + ", position=" + position + "]";
    }

}
