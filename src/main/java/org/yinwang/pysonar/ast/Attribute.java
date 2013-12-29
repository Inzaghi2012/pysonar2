package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;

import static org.yinwang.pysonar.Binding.Kind.ATTRIBUTE;


public class Attribute extends Node {

    @Nullable
    public Node target;
    @NotNull
    public Name attr;


    public Attribute(@Nullable Node target, @NotNull Name attr, String file, int start, int end) {
        super(file, start, end);
        this.target = target;
        this.attr = attr;
        addChildren(target, attr);
    }


    public void setAttr(State s, @NotNull Type type) {
        List<State> states = transformExpr(target, s);
        for (State s1 : states) {
            s1.put(attr, type);
        }
    }


    private void setAttrType(@NotNull Type targetType, @NotNull Type v) {
        if (targetType.isUnknownType()) {
            Analyzer.self.putProblem(this, "Can't set attribute for UnknownType");
            return;
        }
        // new attr, mark the type as "mutated"
        if (targetType.table.lookupAttr(attr.id) == null ||
                !targetType.table.lookupAttrType(attr.id).equals(v))
        {
            targetType.setMutated(true);
        }
        targetType.table.put(attr.id, attr, v, ATTRIBUTE);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = transformExpr(target, s);
        for (State s1 : ss) {
            Type targetType = s1.lookupType(target);
            Binding b = targetType.table.lookupAttr(attr.id);
            if (b == null) {
                Analyzer.self.putProblem(attr, "attribute not found in type: " + targetType);
                s1.put(this, Type.UNKNOWN);
            } else {
                Analyzer.self.putRef(attr, b);
                if (parent != null && parent.isCall() &&
                        b.type.isFuncType() && targetType.isInstanceType())
                {  // method call
                    b.type.asFuncType().setSelfType(targetType);
                }
                s1.put(this, b);
            }
        }
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(attr:" + start + ":" + target + "." + attr.id + ")";
    }

}
