package org.mobicents.diameter.framework.scenarios;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mobicents.diameter.framework.TestingFramework;

@RunWith(Parameterized.class)
public class DuplicateDetectionTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"scenarios/duplicate-detection-test.xml"},
        {"scenarios/duplicate-detection-test-diff-e2e.xml"},
        {"scenarios/duplicate-detection-test-diff-origin-host.xml"},
        {"scenarios/duplicate-detection-test-diff-origin-host-e2e.xml"},
        {"scenarios/duplicate-detection-test-rtr-flag-unset.xml"},
        {"scenarios/duplicate-detection-test-rtr-sent-first.xml"},
        {"scenarios/duplicate-detection-test-three-times.xml"}
    });
  }

  private String scenario;
  
  public DuplicateDetectionTest(String scenario) {
    this.scenario = scenario;
  }
  
  @Test
  public void runTest() throws FileNotFoundException {
    TestingFramework tF = new TestingFramework();
    tF.executeTest(this.getClass().getClassLoader().getResourceAsStream(scenario));
  }
}
