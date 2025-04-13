package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link TimeDuration}
 */
public class TimeDurationTest {

  @Test
  public void testTimeDurationWithValidInputs() {
    // Test milliseconds
    TimeDuration duration = new TimeDuration("100ms");
    Assert.assertEquals(100 * 1_000_000L, duration.getNanos());

    // Test seconds
    duration = new TimeDuration("5s");
    Assert.assertEquals(5 * 1_000_000_000L, duration.getNanos());

    // Test minutes
    duration = new TimeDuration("2m");
    Assert.assertEquals(2 * 60 * 1_000_000_000L, duration.getNanos());

    // Test hours
    duration = new TimeDuration("1h");
    Assert.assertEquals(1 * 60 * 60 * 1_000_000_000L, duration.getNanos());

    // Test days
    duration = new TimeDuration("1d");
    Assert.assertEquals(1 * 24 * 60 * 60 * 1_000_000_000L, duration.getNanos());

    // Test decimal values
    duration = new TimeDuration("1.5s");
    Assert.assertEquals((long)(1.5 * 1_000_000_000), duration.getNanos());

    duration = new TimeDuration("0.5m");
    Assert.assertEquals((long)(0.5 * 60 * 1_000_000_000), duration.getNanos());

    // Test without unit (default to milliseconds)
    duration = new TimeDuration("100");
    Assert.assertEquals(100 * 1_000_000L, duration.getNanos());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimeDurationWithInvalidFormat() {
    new TimeDuration("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimeDurationWithInvalidUnit() {
    new TimeDuration("10x");
  }

  @Test
  public void testTimeDurationToString() {
    TimeDuration duration = new TimeDuration("5s");
    Assert.assertEquals("5s", duration.value());
  }

  // Add the getNanos() test
  @Test
  public void testTimeDurationGetNanos() {
    // Test milliseconds
    TimeDuration duration = new TimeDuration("100ms");
    Assert.assertEquals(100 * 1_000_000L, duration.getNanos());

    // Test seconds
    duration = new TimeDuration("5s");
    Assert.assertEquals(5 * 1_000_000_000L, duration.getNanos());

    // Test minutes
    duration = new TimeDuration("2m");
    Assert.assertEquals(2 * 60 * 1_000_000_000L, duration.getNanos());

    // Test hours
    duration = new TimeDuration("1h");
    Assert.assertEquals(1 * 60 * 60 * 1_000_000_000L, duration.getNanos());

    // Test days
    duration = new TimeDuration("1d");
    Assert.assertEquals(1 * 24 * 60 * 60 * 1_000_000_000L, duration.getNanos());

    // Test decimal values
    duration = new TimeDuration("1.5s");
    Assert.assertEquals((long)(1.5 * 1_000_000_000), duration.getNanos());

    duration = new TimeDuration("0.5m");
    Assert.assertEquals((long)(0.5 * 60 * 1_000_000_000), duration.getNanos());

    // Test without unit (default to milliseconds)
    duration = new TimeDuration("100");
    Assert.assertEquals(100 * 1_000_000L, duration.getNanos());
  }
}
