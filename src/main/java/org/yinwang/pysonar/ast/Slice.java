package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ListType;

import java.util.List;


public class Slice extends Node {

    public Node lower;
    public Node step;
    public Node upper;


    public Slice(Node lower, Node step, Node upper, String file, int start, int end) {
        super(file, start, end);
        this.lower = lower;
        this.step = step;
        this.upper = upper;
        addChildren(lower, step, upper);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        if (lower != null) {
            transformExpr(lower, s);
        }
        if (step != null) {
            transformExpr(step, s);
        }
        if (upper != null) {
            transformExpr(upper, s);
        }
        return s.put(this, new ListType());
    }


    @NotNull
    @Override
    public String toString() {
        return "(slice:" + lower + ":" + step + ":" + upper + ")";
    }

}
