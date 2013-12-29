package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class ExtSlice extends Node {

    public List<Node> dims;


    public ExtSlice(List<Node> dims, String file, int start, int end) {
        super(file, start, end);
        this.dims = dims;
        addChildren(dims);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = s.single();
        for (Node d : dims) {
            ss = transformExpr(d, ss);
        }
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(extslice:" + dims + ")";
    }

}
