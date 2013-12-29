package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.List;


public class Raise extends Node {

    public Node exceptionType;
    public Node inst;
    public Node traceback;


    public Raise(Node exceptionType, Node inst, Node traceback, String file, int start, int end) {
        super(file, start, end);
        this.exceptionType = exceptionType;
        this.inst = inst;
        this.traceback = traceback;
        addChildren(exceptionType, inst, traceback);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = s.single();

        if (exceptionType != null) {
            ss = transformExpr(exceptionType, ss);
        }
        if (inst != null) {
            ss = transformExpr(inst, ss);
        }
        if (traceback != null) {
            ss = transformExpr(traceback, ss);
        }
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(raise:" + traceback + ":" + exceptionType + ")";
    }

}
