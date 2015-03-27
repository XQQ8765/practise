package barat.test;

class A_A
{
  void a()
  {
    System.out.println("A_A.a()");
  }
  class B
  {
    void b()
    {
      System.out.println("B.b()");
    }
    class C
    {
      void c()
      {
          A_A.X x = new X();
        B.Y y = new B.Y();
      }
    }
    class Y
    {
    }
  }
  class X
  {
  }
}

public class QualifiedAnonymousAlloc
{
  public static void main(String[] args)
  {
      A_A a = new A_A();
      A_A.B b = a.new B();
      A_A.B.C c = b.new C();
    c.c();
  }
}
