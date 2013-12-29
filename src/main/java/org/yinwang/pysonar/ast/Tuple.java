package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.TupleType;

import java.util.List;


public class Tuple extends Sequence {

    public Tuple(List<Node> elts, String file, int start, int end) {
        super(elts, file, start, end);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = s.single();
        for (Node e : elts) {
            ss = transformExpr(e, ss);
        }
        for (State s1 : ss) {
            TupleType t = new TupleType();
            for (Node e : elts) {
                t.add(s1.lookupType(e));
            }
            s1.put(this, t);
        }
        return ss;
    }


    @NotNull
    @Override
    public String toDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        int idx = 0;
        for (Node n : elts) {
            if (idx != 0) {
                sb.append(", ");
            }
            idx++;
            sb.append(n.toDisplay());
        }

        sb.append(")");
        return sb.toString();
    }


    @NotNull
    @Override
    public String toString() {
        return "(tuple:" + elts + ")";
    }

}
