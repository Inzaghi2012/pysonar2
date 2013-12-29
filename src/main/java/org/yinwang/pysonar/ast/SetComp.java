package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ListType;

import java.util.List;


public class SetComp extends Node {

    public Node elt;
    public List<Comprehension> generators;


    public SetComp(Node elt, List<Comprehension> generators, String file, int start, int end) {
        super(file, start, end);
        this.elt = elt;
        this.generators = generators;
        addChildren(elt);
        addChildren(generators);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        transformList(generators, s);
        return transformExpr(elt, s);
    }


    @NotNull
    @Override
    public String toString() {
        return "(SetComp:" + elt + ")";
    }

}
