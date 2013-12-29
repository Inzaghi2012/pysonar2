package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Binder;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Assign extends Node {

    @NotNull
    public Node target;
    @NotNull
    public Node value;


    public Assign(@NotNull Node target, @NotNull Node value, String file, int start, int end) {
        super(file, start, end);
        this.target = target;
        this.value = value;
        addChildren(target);
        addChildren(value);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        List<State> states = transformExpr(value, s);

        for (State s1 : states) {
            Type t1 = s1.lookupType("#return");
            Binder.bind(s1, target, t1);
        }

        return states;
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + target + " = " + value + ")";
    }

}
