package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.BoolType;
import org.yinwang.pysonar.types.IntType;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class BinOp extends Node {

    @NotNull
    public Node left;
    @NotNull
    public Node right;
    @NotNull
    public Op op;


    public BinOp(@NotNull Op op, @NotNull Node left, @NotNull Node right, String file, int start, int end) {
        super(file, start, end);
        this.left = left;
        this.right = right;
        this.op = op;
        addChildren(left, right);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        return s.single();
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }

}
