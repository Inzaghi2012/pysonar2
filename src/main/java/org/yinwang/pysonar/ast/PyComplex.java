package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ComplexType;

import java.util.List;


public class PyComplex extends Node {

    public double real;
    public double imag;


    public PyComplex(double real, double imag, String file, int start, int end) {
        super(file, start, end);
        this.real = real;
        this.imag = imag;
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.put(this, new ComplexType(real, imag));
    }


    @NotNull
    @Override
    public String toString() {
        return "(complex:" + real + "+" + imag + "j)";
    }

}
