package org.mobicents.diameter.stack.parser;

import java.io.IOException;
import java.util.Date;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.client.impl.parser.ElementParser;
import org.junit.Assert;
import org.junit.Test;

public class AvpSetTest {

  private ElementParser elementParser = new ElementParser();
  private static final int _CODE = 600;
  private static final long _VENDOR = 1000;

  @Test
  public void remove1Test() throws IOException, AvpDataException {
    AvpSet set = elementParser.decodeAvpSet(new byte[0], 1);
    set.addAvp(_CODE, new Date());
    set.addAvp(_CODE, new Date());
    set.addAvp(_CODE , new Date(), _VENDOR, true, true);
    Assert.assertEquals("Wrong set size", 3, set.size());
    set.removeAvp(_CODE);
    Assert.assertEquals("Wrong set size", 1, set.size());
    Avp avp = set.getAvpByIndex(0);
    Assert.assertEquals("Wrong avp code", _CODE, avp.getCode());
    Assert.assertEquals("Wrong avp vendor", _VENDOR, avp.getVendorId());
    //check again :)
    set.removeAvp(_CODE);
    Assert.assertEquals("Wrong set size", 1, set.size());
    avp = set.getAvpByIndex(0);
    Assert.assertEquals("Wrong avp code", _CODE, avp.getCode());
    Assert.assertEquals("Wrong avp vendor", _VENDOR, avp.getVendorId());
    set.removeAvp(_CODE, _VENDOR);
    Assert.assertEquals("Wrong set size", 0, set.size());
  }

  @Test
  public void remove2Test() throws IOException, AvpDataException {
    AvpSet set = elementParser.decodeAvpSet(new byte[0], 1);
    set.addAvp(_CODE, new Date());
    set.addAvp(_CODE, new Date());
    set.addAvp(_CODE , new Date(), _VENDOR, true, true);
    Assert.assertEquals("Wrong set size", 3, set.size());
    set.removeAvp(_CODE, _VENDOR);
    Assert.assertEquals("Wrong set size", 2, set.size());
    Avp avp = set.getAvpByIndex(0);
    Assert.assertEquals("Wrong avp code", _CODE, avp.getCode());
    Assert.assertEquals("Wrong avp vendor", 0L, avp.getVendorId());
    avp = set.getAvpByIndex(1);
    Assert.assertEquals("Wrong avp code", _CODE, avp.getCode());
    Assert.assertEquals("Wrong avp vendor", 0L, avp.getVendorId());

    set.removeAvp(_CODE);
    Assert.assertEquals("Wrong set size", 0, set.size());

  }

  @Test(timeout = 500)
  public void decodeWithVendorIdZero() throws IOException, AvpDataException {
    byte avpCode = 123;
    byte vendorId = 1;
    byte[] avpSetBytes = new byte[] {
        0, 0, 1, 3, 64, 0, 0, 12, 0, 0, 0, 3, 0, 0, 1, 41, 0, 0, 0, 20, 0, 0, 1, 42, 0, 0, 0, 12, 0, 0, 7, -47, 0, 0, 0,
        1, 0, 0, 0, 26, 109, 111, 98, 105, 99, 101, 110, 116, 115, 45, 100, 105, 97, 109, 101, 116, 101, 114, 0, 0, 0,
        0, 0, avpCode, -64, 0, 0, 15, 0, 0, 0, vendorId, 88, 88, 88, 0
    };
    AvpSet avpVendor1 = elementParser.decodeAvpSet(avpSetBytes);
    Assert.assertNotNull(avpVendor1.getAvp(123, 1));
    
    vendorId = 0;
    avpSetBytes = new byte[] {
        0, 0, 1, 3, 64, 0, 0, 12, 0, 0, 0, 3, 0, 0, 1, 41, 0, 0, 0, 20, 0, 0, 1, 42, 0, 0, 0, 12, 0, 0, 7, -47, 0, 0, 0,
        1, 0, 0, 0, 26, 109, 111, 98, 105, 99, 101, 110, 116, 115, 45, 100, 105, 97, 109, 101, 116, 101, 114, 0, 0, 0,
        0, 0, avpCode, -64, 0, 0, 15, 0, 0, 0, vendorId, 88, 88, 88, 0
    };
    AvpSet avpVendor0 = elementParser.decodeAvpSet(avpSetBytes);
    Assert.assertNotNull(avpVendor0.getAvp(123, 0));
  }


}
