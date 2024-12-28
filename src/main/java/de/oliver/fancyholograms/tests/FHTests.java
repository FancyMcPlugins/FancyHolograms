package de.oliver.fancyholograms.tests;

import de.oliver.fancyholograms.tests.api.HologramBuilderTest;
import de.oliver.fancyholograms.tests.api.HologramRegistryTest;
import de.oliver.fancylib.tests.FPTestClass;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FHTests {

    private final List<FPTestClass> tests = new ArrayList<>();

    public FHTests() {
        addTest(HologramRegistryTest.class);
        addTest(HologramBuilderTest.class);
    }

    /**
     * Adds a test class to the list of test classes to be run.
     *
     * @param testClass the test class to be added
     * @return this instance, allowing for method chaining
     */
    public FHTests addTest(Class<?> testClass) {
        tests.add(FPTestClass.fromClass(testClass));
        return this;
    }

    /**
     * Runs all registered test classes using the provided player context.
     *
     * @param player The player context to pass to the test methods.
     * @return true if all tests completed successfully, false if any test failed or an unexpected exception occurred.
     */
    public boolean runAllTests(Player player) {
        for (FPTestClass test : tests) {
            try {
                if (!test.runTests(player)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the current count of test classes registered to be run.
     *
     * @return the number of test classes in the list
     */
    public int getTestCount() {
        int count = 0;

        for (FPTestClass test : tests) {
            count += test.testMethods().size();
        }

        return count;
    }

}
