package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.DictType;
import org.yinwang.pysonar.types.Type;

import java.util.List;


public class Subscript extends Node {

    @NotNull
    public Node value;
    @Nullable
    public Node slice;  // an NIndex or NSlice


    public Subscript(@NotNull Node value, @Nullable Node slice, String file, int start, int end) {
        super(file, start, end);
        this.value = value;
        this.slice = slice;
        addChildren(value, slice);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = transformExpr(value, s);
        ss = slice == null ? ss : transformExpr(slice, ss);
        for (State s1 : ss) {
            Type vt = s1.lookupType(value);
            Type st = s1.lookupType(slice);
            getSubscript(vt, st, s1);
        }
        return ss;
    }


    @NotNull
    private void getSubscript(@NotNull Type vt, @Nullable Type st, State s) {
        if (vt.isUnknownType()) {
            s.put(this, Type.UNKNOWN);
        } else if (vt.isListType()) {
            getListSubscript(vt, st, s);
        } else if (vt.isTupleType()) {
            getListSubscript(vt.asTupleType().toListType(), st, s);
        } else if (vt.isDictType()) {
            DictType dt = vt.asDictType();
            if (!dt.keyType.equals(st)) {
                addWarning("Possible KeyError (wrong type for subscript)");
            }
            s.put(this, vt.asDictType().valueType);
        } else if (vt.isStrType()) {
            if (st != null && (st.isListType() || st.isNumType())) {
                s.put(this, vt);
            } else {
                addWarning("Possible KeyError (wrong type for subscript)");
                s.put(this, Type.UNKNOWN);
            }
        } else {
            s.put(this, Type.UNKNOWN);
        }
    }


    @NotNull
    private void getListSubscript(@NotNull Type vt, @Nullable Type st, State s) {
        if (vt.isListType()) {
            if (st != null && st.isListType()) {
                s.put(this, vt);
            } else if (st == null || st.isNumType()) {
                s.put(this, vt.asListType().eltType);
            } else {
                Type sliceFunc = vt.table.lookupAttrType("__getslice__");
                if (sliceFunc == null) {
                    addError("The type can't be sliced: " + vt);
                    s.put(this, Type.UNKNOWN);
                } else if (sliceFunc.isFuncType()) {
                    Call.apply(sliceFunc.asFuncType(), null, null, null, null, this, s);
                } else {
                    addError("The type's __getslice__ method is not a function: " + sliceFunc);
                    s.put(this, Type.UNKNOWN);
                }
            }
        } else {
            s.put(this, Type.UNKNOWN);
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(subscript:" + value + ":" + slice + ")";
    }

}
