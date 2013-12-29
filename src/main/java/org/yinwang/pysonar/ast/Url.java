package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


/**
 * virtual-AST node used to represent virtual source locations for builtins
 * as external urls.
 */
public class Url extends Node {

    public String url;


    public Url(String url) {
        this.url = url;
    }


    // will not be called
    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.put(this, Type.STR);
    }


    @NotNull
    @Override
    public String toString() {
        return "(url:\"" + url + "\")";
    }

}
