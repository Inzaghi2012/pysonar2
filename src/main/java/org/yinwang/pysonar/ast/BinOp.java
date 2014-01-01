package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.NumType;
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
                    ltype instanceof NumType && rtype instanceof NumType)
            {
                NumType leftNum = (NumType) ltype;
                NumType rightNum = (NumType) rtype;

                if (op == Op.Add) {
                    s1.put(this, NumType.add(leftNum, rightNum));
                    ret.add(s1);
                }

                if (op == Op.Sub) {
                    s1.put(this, NumType.sub(leftNum, rightNum));
                    ret.add(s1);
                }

                if (op == Op.Mul) {
                    s1.put(this, NumType.mul(leftNum, rightNum));
                    ret.add(s1);
                }

                if (op == Op.Div) {
                    s1.put(this, NumType.div(leftNum, rightNum));
                    ret.add(s1);
                }

                // comparison
                if (op == Op.Lt || op == Op.LtE || op == Op.Gt || op == Op.GtE || op == Op.Equal) {
                    Node leftNode = left;
                    Op op1 = op;

                    if (!left.isName()) {
                        leftNode = right;

                        NumType tmpNum = rightNum;
                        rightNum = leftNum;
                        leftNum = tmpNum;

                        op1 = Op.invert(op1);
                    }

                    if (op1 == Op.Lt) {
                        if (NumType.lt(leftNum, rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (NumType.gte(leftNum, rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l < r, then l's upper bound is r's upper bound (exclusive)
                                NumType trueType = NumType.copy(leftNum);
                                NumType.setUpperExclusive(trueType, rightNum);

                                // false branch: if l >= r, then l's lower bound is r's lower bound (inclusive)
                                NumType falseType = NumType.copy(leftNum);
                                NumType.setLowerInclusive(falseType, rightNum);

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
                        if (NumType.lte(leftNum, rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (NumType.gt(leftNum, rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l <= r, then l's upper bound is r's upper bound (inclusive)
                                NumType trueType = NumType.copy(leftNum);
                                NumType.setUpperInclusive(trueType, rightNum);

                                // false branch: if l > r, then l's lower bound is r's lower bound (exclusive)
                                NumType falseType = NumType.copy(leftNum);
                                NumType.setLowerExclusive(falseType, rightNum);

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
                        if (NumType.gt(leftNum, rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (NumType.lte(leftNum, rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            if (leftNode.isName()) {
                                // true branch: if l > r, then l's lower bound is r's lower bound
                                NumType trueType = NumType.copy(leftNum);
                                NumType.setLowerExclusive(trueType, rightNum);

                                // false branch: if l <= r, then l's upper bound is r's upper bound
                                NumType falseType = NumType.copy(leftNum);
                                NumType.setUpperInclusive(falseType, rightNum);

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
                        if (NumType.gte(leftNum, rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (NumType.lt(leftNum, rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            if (leftNode.isName()) {
                                // true branch: if l >= r, then l's lower bound is r's lower bound
                                NumType trueType = NumType.copy(leftNum);
                                NumType.setLowerInclusive(trueType, rightNum);

                                // false branch: if l < r, then l's upper bound is r's upper bound
                                NumType falseType = NumType.copy(leftNum);
                                NumType.setUpperExclusive(falseType, rightNum);

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
                        if (NumType.eq(leftNum, rightNum)) {
                            s1.put(this, Type.TRUE);
                            ret.add(s1);
                        } else if (NumType.lt(leftNum, rightNum) || NumType.gt(leftNum, rightNum)) {
                            s1.put(this, Type.FALSE);
                            ret.add(s1);
                        } else {
                            // transfer bound information
                            if (leftNode.isName()) {
                                // true branch: if l == r, then l is r
                                NumType trueType = NumType.copy(rightNum);

                                // false branch: if l != r, then l < r or l > r
                                NumType falseType1 = NumType.copy(leftNum);
                                NumType.setLowerExclusive(falseType1, rightNum);

                                NumType falseType2 = NumType.copy(leftNum);
                                NumType.setUpperExclusive(falseType2, rightNum);

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
