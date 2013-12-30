package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.IntType;
import org.yinwang.pysonar.types.Type;

import java.util.ArrayList;
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

        // or has a special flow
        if (op == Op.Or) {

            // evaluate left and separate the states into true and false
            List<State> ss1 = transformExpr(left, s);
            List<State> trueS = new ArrayList<>();
            List<State> falseS = new ArrayList<>();

            for (State s1 : ss1) {
                Type ltype = s1.lookupType(left);
                if (ltype != null && ltype.isTrue()) {
                    trueS.add(s1);
                } else {
                    falseS.add(s1);
                }
            }

            List<State> ret = new ArrayList<>();
            // take all true states
            ret.addAll(trueS);
            // only the false states from left go into right
            ret.addAll(transformExpr(right, falseS));

            for (State s1 : ret) {
                Type ltype = s1.lookupType(left);
                Type rtype = s1.lookupType(right);

                if (ltype != null && ltype.isFalse() &&
                        rtype != null && rtype.isFalse())
                {
                    s1.put(this, Type.FALSE);
                } else {
                    s1.put(this, Type.TRUE);
                }
            }
            return ret;
        }

        // all the rest op's has sequential flow
        List<State> ss = transformExpr(left, s);
        ss = transformExpr(right, ss);
        List<State> ret = new ArrayList<>();

        if (op == Op.And) {
            for (State s1 : ss) {
                Type ltype = s1.lookupType(left);
                Type rtype = s1.lookupType(right);

                if (ltype != null && ltype.isTrue() &&
                        rtype != null && rtype.isTrue())
                {
                    s1.put(this, Type.TRUE);
                } else {
                    s1.put(this, Type.FALSE);
                }
            }

            return ss;
        }

        // try to figure out actual result
        for (State s1 : ss) {
            Type ltype = s1.lookupType(left);
            Type rtype = s1.lookupType(right);

            if (ltype != null && rtype != null &&
                    ltype.isIntType() && rtype.isIntType())
            {
                IntType leftNum = ltype.asIntType();
                IntType rightNum = rtype.asIntType();

                if (op == Op.Add) {
                    s1.put(this, IntType.add(leftNum, rightNum));
                }

                if (op == Op.Sub) {
                    s1.put(this, IntType.sub(leftNum, rightNum));
                }

                if (op == Op.Mul) {
                    s1.put(this, IntType.mul(leftNum, rightNum));
                }

                if (op == Op.Div) {
                    s1.put(this, IntType.div(leftNum, rightNum));
                }

                // comparison
                if (op == Op.Lt || op == Op.LtE || op == Op.Gt || op == Op.GtE) {
                    Node leftNode = left;
                    IntType trueType, falseType;
                    Op op1 = op;

                    if (!left.isName()) {
                        leftNode = right;

                        IntType tmpNum = rightNum;
                        rightNum = leftNum;
                        leftNum = tmpNum;

                        op1 = Op.invert(op1);
                    }

                    if (op1 == Op.Lt || op1 == Op.LtE) {
                        if (leftNum.lt(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.gt(rightNum) || leftNum.eq(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l < r, then l's upper bound is r's upper bound
                                trueType = new IntType(leftNum);

                                if (op1 == Op.Lt) { // l < r
                                    trueType.setUpperExclusive(rightNum.upper);
                                } else { // l <= r
                                    trueType.setUpperInclusive(rightNum.upper);
                                }

                                // false branch: if l >= r, then l's lower bound is r's lower bound
                                falseType = new IntType(leftNum);

                                if (op1 == Op.Lt) { // l >= r
                                    falseType.setLowerInclusive(rightNum.lower);
                                } else if (op1 == Op.LtE) { // l > r
                                    falseType.setLowerExclusive(rightNum.lower);
                                }

                                String id = leftNode.asName().id;

                                Binding b = s1.lookup(id);
                                State s1true = s1.copy();
                                State s1false = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.put(id, new Binding(b.node, trueType, b.kind));
                                ret.add(s1true);

                                s1false.put(this, Type.FALSE);
                                s1false.put(id, new Binding(b.node, falseType, b.kind));
                                ret.add(s1false);
                            }
                        }
                    }

                    if (op1 == Op.Gt || op1 == Op.GtE) {
                        if (leftNum.gt(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.lt(rightNum) || leftNum.eq(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            if (leftNode.isName()) {
                                // true branch: if l > r, then l's lower bound is r's lower bound
                                trueType = new IntType(leftNum);

                                if (op1 == Op.Gt) {  // l > r
                                    trueType.setLowerExclusive(rightNum.lower);
                                } else {  // l >= r
                                    trueType.setLowerInclusive(rightNum.lower);
                                }

                                // false branch: if l < r, then l's upper bound is r's upper bound
                                falseType = new IntType(leftNum);

                                if (op1 == Op.Gt) {  // l <= r
                                    falseType.setUpperInclusive(rightNum.upper);
                                } else { // l < r
                                    falseType.setUpperExclusive(rightNum.upper);
                                }

                                String id = leftNode.asName().id;
                                Binding b = s1.lookup(id);
                                State s1true = s1.copy();
                                State s1false = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.put(id, new Binding(b.node, trueType, b.kind));
                                ret.add(s1true);

                                s1false.put(this, Type.FALSE);
                                s1false.put(id, new Binding(b.node, falseType, b.kind));
                                ret.add(s1false);
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }

}
