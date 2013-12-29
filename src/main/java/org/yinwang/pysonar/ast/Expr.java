package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


/**
 * Expression statement.
 */
public class Expr extends Node {

    public Node value;


    public Expr(Node n, String file, int start, int end) {
        super(file, start, end);
        this.value = n;
        addChildren(n);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = s.single();
        if (value != null) {
            ss = transformExpr(value, ss);
        }
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(expr:" + value + ")";
    }

}
