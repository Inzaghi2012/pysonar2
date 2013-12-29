package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.ArrayList;
import java.util.List;


public class If extends Node {

    @NotNull
    public Node test;
    public Node body;
    public Node orelse;


    public If(@NotNull Node test, Node body, Node orelse, String file, int start, int end) {
        super(file, start, end);
        this.test = test;
        this.body = body;
        this.orelse = orelse;
        addChildren(test, body, orelse);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        State s1 = s.copy();
        State s2 = s.copy();
        List<State> ss = new ArrayList<>();

        if (body != null) {
            ss = transformExpr(body, s1);
        }

        if (orelse != null) {
            ss.addAll(transformExpr(orelse, s2));
        }

        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(if:" + test + ":" + body + ":" + orelse + ">";
    }

}
