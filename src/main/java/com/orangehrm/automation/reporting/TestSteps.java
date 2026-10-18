package com.orangehrm.automation.reporting;

import java.util.ArrayList;
import java.util.List;

/** Collects human-readable actions for the current TestNG test invocation. */
public final class TestSteps {
    private static final ThreadLocal<List<String>> STEPS = ThreadLocal.withInitial(ArrayList::new);

    private TestSteps() { }

    public static void clear() { STEPS.get().clear(); }

    public static void add(String step) { STEPS.get().add(step); }

    public static List<String> snapshot() { return List.copyOf(STEPS.get()); }
}
