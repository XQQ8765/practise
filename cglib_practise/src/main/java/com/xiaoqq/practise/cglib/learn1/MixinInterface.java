package com.xiaoqq.practise.cglib.learn1;

interface Interface1 {
    String first();
}

interface Interface2 {
    String second();
}

class Class1 implements Interface1 {
    @Override
    public String first() {
        return "first";
    }
}

class Class2 implements Interface2 {
    @Override
    public String second() {
        return "second";
    }
}

public interface MixinInterface extends Interface1, Interface2{
}
