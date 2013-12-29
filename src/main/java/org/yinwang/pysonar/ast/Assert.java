package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.Collections;
import java.util.List;


public class Assert extends Node {

    public Node test;
    public Node msg;


    public Assert(Node test, Node msg, String file, int start, int end) {
        super(file, start, end);
        this.test = test;
        this.msg = msg;
        addChildren(test, msg);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        if (msg != null) {
            transformExpr(msg, s);
        }
        if (test != null) {
            return transformExpr(test, s);
        } else {
            return Collections.emptyList();
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(assert:" + test + ":" + msg + ")";
    }

}
