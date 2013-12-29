package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Control extends Node {

    public String command;


    public Control(String command, String file, int start, int end) {
        super(file, start, end);
        this.command = command;
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + command + ")";
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.put(this, Type.NONE);
    }
}
