package barat.test;

/**
 * @tag1
 * @tag2
 */
public class Test581888 {
  public static void runTest() {
    barat.reflect.AUserType testClass = barat.Barat.getUserType("barat.test.Test581888");
    if(!testClass.new_hasTag("tag1"))
      throw new RuntimeException("there should be a tag1");
    if(!testClass.new_hasTag("tag2"))
      throw new RuntimeException("there should be a tag2");
  }
}
