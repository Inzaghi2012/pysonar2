package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class Yield extends Node {

    public Node value;


    public Yield(Node n, String file, int start, int end) {
        super(file, start, end);
        this.value = n;
        addChildren(n);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return transformExpr(value, s);
    }


    @NotNull
    @Override
    public String toString() {
        return "(yield:" + value + ")";
    }

}
