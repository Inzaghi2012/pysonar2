package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


/**
 * dummy node for locating purposes only
 * rarely used
 */
public class Dummy extends Node {

    public Dummy(String file, int start, int end) {
        super(file, start, end);
    }


    @NotNull
    @Override
    protected List<State> transform(State s) {
        return s.put(this, Type.UNKNOWN);
    }

}
