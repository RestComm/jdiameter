package org.mobicents.slee.resources.diameter.tests;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.mobicents.slee.resources.diameter.tests.framework.TestingFramework;

public class KeepLargeHopByHopTest
{

  @Test
  public void runTest() throws FileNotFoundException
  {
    TestingFramework tF = new TestingFramework();
    
    tF.executeTest( this.getClass().getClassLoader().getResourceAsStream( "scenarios/keep-largehopbyhop-test.xml" ) );
  }
  
}
