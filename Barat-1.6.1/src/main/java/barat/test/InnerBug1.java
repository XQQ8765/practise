package barat.test;

public class InnerBug1 {
  public static void main(String[] args) {
    class blubb {};

    blubb c1 = new blubb(); // Works

    for(int i=0; i < 2; i++) {
      blubb c2 = new blubb(); // Doesn't work
    }
  }
}

