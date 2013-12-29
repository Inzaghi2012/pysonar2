package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class Try extends Node {

    public List<Handler> handlers;
    public Block body;
    public Block orelse;
    public Block finalbody;


    public Try(List<Handler> handlers, Block body, Block orelse, Block finalbody,
               String file, int start, int end)
    {
        super(file, start, end);
        this.handlers = handlers;
        this.body = body;
        this.orelse = orelse;
        this.finalbody = finalbody;
        addChildren(handlers);
        addChildren(body, orelse);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {

        List<State> ss = s.single();
        if (handlers != null) {
            for (Handler h : handlers) {
                ss = transformExpr(h, ss);
            }
        }

        if (body != null) {
            ss = transformExpr(body, ss);
        }

        if (orelse != null) {
            ss = transformExpr(orelse, ss);
        }

        if (finalbody != null) {
            ss = transformExpr(finalbody, ss);
        }

        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(try:" + handlers + ":" + body + ":" + orelse + ")";
    }

}
