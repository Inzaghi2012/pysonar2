package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Str extends Node {

    public String value;


    public Str(@NotNull Object value, String file, int start, int end) {
        super(file, start, end);
        this.value = value.toString();
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.put(this, Type.STR);
    }


    @NotNull
    @Override
    public String toString() {
        String summary;
        if (value.length() > 10) {
            summary = value.substring(0, 10);
        } else {
            summary = value;
        }
        return "'" + summary + "'";
    }

}
