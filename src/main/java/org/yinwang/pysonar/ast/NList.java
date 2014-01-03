package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ListType;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class NList extends Sequence {

    public NList(@NotNull List<Node> elts, String file, int start, int end) {
        super(elts, file, start, end);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        if (elts.size() == 0) {
            return s.put(this, new ListType());
        }

        List<State> ss = s.single();
        for (Node elt : elts) {
            ss = transformExpr(elt, ss);
        }

        for (State s1 : ss) {
            ListType listType = new ListType();
            for (Node elt : elts) {
                Type t = s1.lookupType(elt);
                if (t != null) {
                    listType.add(t);
                    if (elt instanceof Str) {
                        listType.addValue(((Str) elt).value);
                    }
                }
            }
            s1.put(this, listType);
        }

        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(list:" + start + ":" + elts + ")";
    }

}
