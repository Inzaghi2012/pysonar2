package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class While extends Node {

    public Node test;
    public Node body;
    public Node orelse;


    public While(Node test, Node body, Node orelse, String file, int start, int end) {
        super(file, start, end);
        this.test = test;
        this.body = body;
        this.orelse = orelse;
        addChildren(test, body, orelse);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        transformExpr(test, s);
        List<State> ss = s.single();

        if (body != null) {
            ss = transformExpr(body, ss);
        }

        if (orelse != null) {
            ss = transformExpr(orelse, ss);
        }

        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(while:" + test + ":" + body + ":" + orelse + ")";
    }

}
