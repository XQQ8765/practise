package barat.test;

public class AssertTest {
  public static void main(String[] args) {
    assert 2>1;
    assert new Object()!=null : "object should exist";
  }
}
