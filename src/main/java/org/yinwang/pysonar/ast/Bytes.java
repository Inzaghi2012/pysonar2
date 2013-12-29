package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Bytes extends Node {

    public Object value;


    public Bytes(@NotNull Object value, String file, int start, int end) {
        super(file, start, end);
        this.value = value.toString();
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        s.put(this, Type.STR);
        return s.single();
    }


    @NotNull
    @Override
    public String toString() {
        return "(bytes: " + value + ")";
    }

}
