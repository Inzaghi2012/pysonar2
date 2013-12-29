package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Binder;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;

import java.util.ArrayList;
import java.util.List;


public class For extends Node {

    public Node target;
    public Node iter;
    public Block body;
    public Block orelse;


    public For(Node target, Node iter, Block body, Block orelse,
               String file, int start, int end)
    {
        super(file, start, end);
        this.target = target;
        this.iter = iter;
        this.body = body;
        this.orelse = orelse;
        addChildren(target, iter, body, orelse);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        Binder.bindIter(s, target, iter, Binding.Kind.SCOPE);

        List<State> ss = new ArrayList<>();
        ss.addAll(transformExpr(body, s));

        if (orelse != null) {
            ss.addAll(transformExpr(orelse, s));
        }

        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(for:" + target + ":" + iter + ":" + body + ":" + orelse + ")";
    }

}
