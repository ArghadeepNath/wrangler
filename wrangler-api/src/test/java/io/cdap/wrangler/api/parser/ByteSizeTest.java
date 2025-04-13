package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link ByteSize}
 */
public class ByteSizeTest {

  @Test
  public void testByteSizeWithValidInputs() {
    // Test bytes
    ByteSize byteSize = new ByteSize("100B");
    Assert.assertEquals(100L, byteSize.getBytes());
    
    byteSize = new ByteSize("100");
    Assert.assertEquals(100L, byteSize.getBytes());
    
    // Test kilobytes
    byteSize = new ByteSize("10KB");
    Assert.assertEquals(10 * 1024L, byteSize.getBytes());
    
    byteSize = new ByteSize("10K");
    Assert.assertEquals(10 * 1024L, byteSize.getBytes());
    
    // Test megabytes
    byteSize = new ByteSize("1.5MB");
    Assert.assertEquals((long)(1.5 * 1024 * 1024), byteSize.getBytes());
    
    byteSize = new ByteSize("1.5M");
    Assert.assertEquals((long)(1.5 * 1024 * 1024), byteSize.getBytes());
    
    // Test gigabytes
    byteSize = new ByteSize("2GB");
    Assert.assertEquals(2 * 1024 * 1024 * 1024L, byteSize.getBytes());
    
    byteSize = new ByteSize("2G");
    Assert.assertEquals(2 * 1024 * 1024 * 1024L, byteSize.getBytes());
    
    // Test terabytes
    byteSize = new ByteSize("1TB");
    Assert.assertEquals(1 * 1024 * 1024 * 1024 * 1024L, byteSize.getBytes());
    
    byteSize = new ByteSize("1T");
    Assert.assertEquals(1 * 1024 * 1024 * 1024 * 1024L, byteSize.getBytes());
    
    // Test case insensitivity
    byteSize = new ByteSize("5kb");
    Assert.assertEquals(5 * 1024L, byteSize.getBytes());
    
    byteSize = new ByteSize("5Kb");
    Assert.assertEquals(5 * 1024L, byteSize.getBytes());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testByteSizeWithInvalidFormat() {
    new ByteSize("invalid");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testByteSizeWithInvalidUnit() {
    new ByteSize("10XB");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testByteSizeWithNegativeValue() {
    new ByteSize("-10KB");
  }
  
  @Test
  public void testByteSizeToString() {
    ByteSize byteSize = new ByteSize("10KB");
    Assert.assertEquals("10KB", byteSize.value());
  }
}