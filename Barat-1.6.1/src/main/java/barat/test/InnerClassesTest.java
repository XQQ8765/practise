package barat.test;

class A
{
  protected int id;
  A(int i)
  {
    id = i;
  }
  protected void m()
  {
    System.out.println("m() auf Objekt: " + id);
  }
  private void mm()
  {
    System.out.println("mm() auf Objekt: " + id);
  }
  class B extends A
  {
    B(int j)
    {
      super(j);
    }
    void n()
    {
      System.out.println("Hallo, hier ist n auf " + id +
                         " innerhalb von " + A.this.id);
      A.this.m();
      A.this.mm();
    }
    protected void m()
    {
      System.out.println("aetsch!");
    }
  }
}

public class InnerClassesTest
{
  public static void main(String[] args)
  {
    A a = new A(1);
    a.m();
    A.B b = a.new B(2);
    b.n();
    //b.m();
  }
}
