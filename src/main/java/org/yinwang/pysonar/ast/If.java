package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

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
        List<State> ss = transformExpr(test, s);
        List<State> ret = new ArrayList<>();

        for (State s1 : ss) {
            Type testType = s1.lookupType(test);
            State s2 = s1.copy();
            List<State> trueStates = transformExpr(body, s1);
            List<State> falseStates = transformExpr(orelse, s2);

            if (testType != null && !testType.isFalse() && body != null) {
                ret.addAll(trueStates);
            }

            if (testType != null && !testType.isTrue() && orelse != null) {
                ret.addAll(falseStates);
            }
        }
        return ret;
    }


    @NotNull
    @Override
    public String toString() {
        return "(if:" + test + ":" + body + ":" + orelse + ")";
    }

}
