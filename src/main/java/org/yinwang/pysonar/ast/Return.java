package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Return extends Node {

    public Node value;


    public Return(Node n, String file, int start, int end) {
        super(file, start, end);
        this.value = n;
        addChildren(n);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        if (value == null) {
            return s.put(this, Type.NONE);
        } else {
            return transformExpr(value, s);
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(return:" + value + ")";
    }

}
