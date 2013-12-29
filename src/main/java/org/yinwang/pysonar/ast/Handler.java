package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class Handler extends Node {

    public List<Node> exceptions;
    public Node binder;
    public Block body;


    public Handler(List<Node> exceptions, Node binder, Block body, String file, int start, int end) {
        super(file, start, end);
        this.binder = binder;
        this.exceptions = exceptions;
        this.body = body;
        addChildren(binder, body);
        addChildren(exceptions);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        return transformExpr(body, s);
    }


    @NotNull
    @Override
    public String toString() {
        return "(handler:" + exceptions + ":" + binder + ")";
    }

}
