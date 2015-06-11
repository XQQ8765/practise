package templatecode;

public class ExampleClass {
    
    public static void exampleMethod() {
        String name = ExampleClass.class.getName();
    }

    public static Class returnClass() {
        return ExampleClass.class;
    }

    public static Class returnClass2() {
        Class clazz = ExampleClass.class;
        return clazz;
    }
}