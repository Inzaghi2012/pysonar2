package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class Exec extends Node {

    public Node body;
    public Node globals;
    public Node locals;


    public Exec(Node body, Node globals, Node locals, String file, int start, int end) {
        super(file, start, end);
        this.body = body;
        this.globals = globals;
        this.locals = locals;
        addChildren(body, globals, locals);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = s.single();
        if (body != null) {
            ss = transformExpr(body, ss);
        }
        if (globals != null) {
            ss = transformExpr(globals, ss);
        }
        if (locals != null) {
            ss = transformExpr(locals, ss);
        }
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(exec:" + start + ":" + end + ")";
    }

}
