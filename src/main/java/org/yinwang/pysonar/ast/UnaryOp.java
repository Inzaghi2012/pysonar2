package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class UnaryOp extends Node {

    public Op op;
    public Node operand;


    public UnaryOp(Op op, Node operand, String file, int start, int end) {
        super(file, start, end);
        this.op = op;
        this.operand = operand;
        addChildren(operand);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = transformExpr(operand, s);

        for (State s1 : ss) {
            Type valueType = s1.lookupType(operand);

            if (op == Op.Add) {
                if (valueType != null && valueType.isNumType()) {
                    s1.put(this, valueType);
                } else {
                    Analyzer.self.putProblem(this, "+ can't be applied to type: " + valueType);
                    s1.put(this, Type.INT);
                }
            }

            if (op == Op.Sub) {
                if (valueType != null && valueType.isIntType()) {
                    s1.put(this, valueType.asIntType().negate());
                } else {
                    Analyzer.self.putProblem(this, "- can't be applied to type: " + valueType);
                    s1.put(this, Type.INT);
                }
            }

            if (op == Op.Not) {
                if (valueType != null && valueType.isTrue()) {
                    s1.put(this, Type.FALSE);
                } else {
                    s1.put(this, Type.TRUE);
                }
            }
        }

//        Analyzer.self.putProblem(this, "operator " + op + " cannot be applied to type: " + valueType);
        return ss;
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + op + " " + operand + ")";
    }

}
