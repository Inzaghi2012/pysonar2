package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
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
                if (op == Op.Lt || op == Op.LtE || op == Op.Gt || op == Op.GtE || op == Op.Equal) {
                    Node leftNode = left;
                    Op op1 = op;

                    if (!left.isName()) {
                        leftNode = right;

                        IntType tmpNum = rightNum;
                        rightNum = leftNum;
                        leftNum = tmpNum;

                        op1 = Op.invert(op1);
                    }

                    if (op1 == Op.Lt) {
                        if (leftNum.lt(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.gte(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l < r, then l's upper bound is r's upper bound (exclusive)
                                IntType trueType = new IntType(leftNum);
                                trueType.setUpperExclusive(rightNum.upper);

                                // false branch: if l >= r, then l's lower bound is r's lower bound (inclusive)
                                IntType falseType = new IntType(leftNum);
                                falseType.setLowerInclusive(rightNum.lower);

                                State s1true = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.updateType(leftNode, trueType);
                                ret.add(s1true);

                                State s1false = s1.copy();
                                s1false.put(this, Type.FALSE);
                                s1false.updateType(leftNode, falseType);
                                ret.add(s1false);
                            }
                        }
                    }

                    if (op1 == Op.LtE) {
                        if (leftNum.lte(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.gt(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l <= r, then l's upper bound is r's upper bound (inclusive)
                                IntType trueType = new IntType(leftNum);
                                trueType.setUpperInclusive(rightNum.upper);

                                // false branch: if l >= r, then l's lower bound is r's lower bound (exclusive)
                                IntType falseType = new IntType(leftNum);
                                falseType.setLowerExclusive(rightNum.lower);

                                State s1true = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.updateType(leftNode, trueType);
                                ret.add(s1true);

                                State s1false = s1.copy();
                                s1false.put(this, Type.FALSE);
                                s1false.updateType(leftNode, falseType);
                                ret.add(s1false);
                            }
                        }
                    }

                    if (op1 == Op.Gt) {
                        if (leftNum.gt(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.lte(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            if (leftNode.isName()) {
                                // true branch: if l > r, then l's lower bound is r's lower bound
                                IntType trueType = new IntType(leftNum);
                                trueType.setLowerExclusive(rightNum.lower);

                                // false branch: if l < r, then l's upper bound is r's upper bound
                                IntType falseType = new IntType(leftNum);
                                falseType.setUpperInclusive(rightNum.upper);

                                State s1true = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.updateType(leftNode, trueType);
                                ret.add(s1true);

                                State s1false = s1.copy();
                                s1false.put(this, Type.FALSE);
                                s1false.updateType(leftNode, falseType);
                                ret.add(s1false);
                            }
                        }
                    }

                    if (op1 == Op.GtE) {
                        if (leftNum.gte(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.lt(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            if (leftNode.isName()) {
                                // true branch: if l >= r, then l's lower bound is r's lower bound
                                IntType trueType = new IntType(leftNum);
                                trueType.setLowerInclusive(rightNum.lower);

                                // false branch: if l < r, then l's upper bound is r's upper bound
                                IntType falseType = new IntType(leftNum);
                                falseType.setUpperExclusive(rightNum.upper);

                                State s1true = s1.copy();
                                s1true.put(this, Type.TRUE);
                                s1true.updateType(leftNode, trueType);
                                ret.add(s1true);

                                State s1false = s1.copy();
                                s1false.put(this, Type.FALSE);
                                s1false.updateType(leftNode, falseType);
                                ret.add(s1false);
                            }
                        }
                    }

                    if (op1 == Op.Equal) {
                        if (leftNum.eq(rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (leftNum.lt(rightNum) || leftNum.gt(rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l == r, then l is r
                                IntType trueType = new IntType(rightNum);

                                // false branch: if l != r, then l < r or l > r
                                IntType falseType1 = new IntType(leftNum);
                                falseType1.setLowerExclusive(rightNum.lower);

                                IntType falseType2 = new IntType(leftNum);
                                falseType2.setUpperExclusive(rightNum.upper);

                                State s1true = s1.copy();
                                State s1false1 = s1.copy();
                                State s1false2 = s1.copy();

                                s1true.put(this, Type.TRUE);
                                s1true.updateType(leftNode, trueType);
                                ret.add(s1true);

                                s1false1.put(this, Type.FALSE);
                                s1false1.updateType(leftNode, falseType1);
                                ret.add(s1false1);

                                s1false2.put(this, Type.FALSE);
                                s1false2.updateType(leftNode, falseType2);
                                ret.add(s1false2);
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
