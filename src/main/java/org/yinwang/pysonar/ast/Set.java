package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ListType;

import java.util.List;


public class Set extends Sequence {

    public Set(List<Node> elts, String file, int start, int end) {
        super(elts, file, start, end);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.put(this, new ListType());
    }


    @NotNull
    @Override
    public String toString() {
        return "(list:" + elts + ")";
    }

}
