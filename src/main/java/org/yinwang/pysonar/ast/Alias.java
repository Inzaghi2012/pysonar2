package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;

import java.util.Collections;
import java.util.List;


public class Alias extends Node {

    public List<Name> name;
    public Name asname;


    public Alias(List<Name> name, Name asname, String file, int start, int end) {
        super(file, start, end);
        this.name = name;
        this.asname = asname;
        addChildren(name);
        addChildren(asname);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return Collections.emptyList();
    }


    @NotNull
    @Override
    public String toString() {
        return "(alias:" + name + " as " + asname + ")";
    }

}
