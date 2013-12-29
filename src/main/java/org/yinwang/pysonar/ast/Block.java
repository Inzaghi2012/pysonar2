package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;

import java.util.List;


public class Block extends Node {

    @NotNull
    public List<Node> seq;


    public Block(@NotNull List<Node> seq, String file, int start, int end) {
        super(file, start, end);
        this.seq = seq;
        addChildren(seq);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        // find global names and mark them
        for (Node n : seq) {
            if (n.isGlobal()) {
                for (Name name : n.asGlobal().names) {
                    s.addGlobalName(name.id);
                    Binding b = s.lookup(name.id);
                    if (b != null) {
                        Analyzer.self.putRef(name, b);
                    }
                }
            }
        }

        List<State> ss = s.single();

        for (Node n : seq) {
            ss = transformExpr(n, ss);
        }

        return ss;
    }


    public boolean isEmpty() {
        return seq.isEmpty();
    }


    @NotNull
    @Override
    public String toString() {
        return "(block:" + seq + ")";
    }

}
