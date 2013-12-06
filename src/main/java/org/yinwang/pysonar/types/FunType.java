package org.yinwang.pysonar.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Scope;
import org.yinwang.pysonar.TypeStack;
import org.yinwang.pysonar.ast.FunctionDef;

import java.util.*;


public class FunType extends Type {

    @NotNull
    public Map<Type, Type> arrows = new HashMap<>();
    public FunctionDef func;
    @Nullable
    public ClassType cls = null;
    private Scope env;
    @Nullable
    public Type selfType;                 // self's type for calls
    public List<Type> defaultTypes;       // types for default parameters (evaluated at def time)


    public FunType() {
    }


    public FunType(FunctionDef func, Scope env) {
        this.func = func;
        this.env = env;
    }


    public FunType(Type from, Type to) {
        addMapping(from, to);
        getTable().addSuper(Analyzer.self.builtins.BaseFunction.getTable());
        getTable().setPath(Analyzer.self.builtins.BaseFunction.getTable().getPath());
    }


    public void addMapping(Type from, Type to) {
        if (arrows.size() < 5) {
            arrows.put(from, to);
            Map<Type, Type> oldArrows = arrows;
            arrows = compressArrows(arrows);

            if (toString().length() > 900) {
                arrows = oldArrows;
            }
        }
    }


    @Nullable
    public Type getMapping(@NotNull Type from) {
        return arrows.get(from);
    }


    public Type getReturnType() {
        if (!arrows.isEmpty()) {
            return arrows.values().iterator().next();
        } else {
            return Analyzer.self.builtins.unknown;
        }
    }


    public FunctionDef getFunc() {
        return func;
    }


    public Scope getEnv() {
        return env;
    }


    @Nullable
    public ClassType getCls() {
        return cls;
    }


    public void setCls(ClassType cls) {
        this.cls = cls;
    }


    @Nullable
    public Type getSelfType() {
        return selfType;
    }


    public void setSelfType(Type selfType) {
        this.selfType = selfType;
    }


    public void clearSelfType() {
        this.selfType = null;
    }


    public List<Type> getDefaultTypes() {
        return defaultTypes;
    }


    public void setDefaultTypes(List<Type> defaultTypes) {
        this.defaultTypes = defaultTypes;
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof FunType) {
            FunType fo = (FunType) other;
            return fo.getTable().getPath().equals(getTable().getPath()) || this == other;
        } else {
            return false;
        }
    }


    static Type removeNoneReturn(@NotNull Type toType) {
        if (toType.isUnionType()) {
            Set<Type> types = new HashSet<>(toType.asUnionType().getTypes());
            types.remove(Analyzer.self.builtins.Cont);
            return UnionType.newUnion(types);
        } else {
            return toType;
        }
    }


    @Override
    public int hashCode() {
        return "FunType".hashCode();
    }


    private Type subsumed(Type type1, Type type2) {
        return subsumedInner(type1, type2, new TypeStack());
    }


    private Type subsumedInner(Type type1, Type type2, TypeStack typeStack) {
        if (typeStack.contains(type1, type2)) {
            return null;
        }

        if (type1.isUnknownType() || type1 == Analyzer.self.builtins.None || type1.equals(type2)) {
            return type2;
        }

        if (type1.isInstanceType() && type2.isInstanceType()) {
            InstanceType it1 = type1.asInstanceType();
            InstanceType it2 = type2.asInstanceType();
            Type cls1 = it1.getClassType();
            Type cls2 = it2.getClassType();
            if (cls1.isClassType() && cls2.isClassType()) {
                ClassType c1 = cls1.asClassType();
                ClassType c2 = cls2.asClassType();
                if (c1.superclass != null && c1.superclass.equals(c2)) {
                    return it2;
                } else if (c2.superclass != null && c2.superclass.equals(c1)) {
                    return it1;
                } else if (!c1.equals(c2) &&
                        c1.superclass != null &&
                        c2.superclass != null &&
                        c1.superclass.equals(c2.superclass)) {
                    return new InstanceType(c1.superclass);
                }
            }

        }

        if (type1 instanceof TupleType && type2 instanceof TupleType) {
            List<Type> elems1 = ((TupleType) type1).getElementTypes();
            List<Type> elems2 = ((TupleType) type2).getElementTypes();
            TupleType ret = new TupleType();

            if (elems1.size() == elems2.size()) {
                typeStack.push(type1, type2);
                for (int i = 0; i < elems1.size(); i++) {
                    Type t = subsumedInner(elems1.get(i), elems2.get(i), typeStack);
                    if (t == null) {
                        typeStack.pop(type1, type2);
                        return null;
                    } else {
                        ret.add(t);
                    }
                }
            }

            return ret;
        }

        if (type1 instanceof ListType && type2 instanceof ListType) {
            return subsumedInner(type1.asListType().toTupleType(),
                    type2.asListType().toTupleType(),
                    typeStack);
        }

        return null;
    }


    private Map<Type, Type> compressArrows(Map<Type, Type> arrows) {
        Map<Type, Type> ret = new HashMap<>();

        for (Map.Entry<Type, Type> e1 : arrows.entrySet()) {
            Type subsumedType = null;

            for (Map.Entry<Type, Type> e2 : arrows.entrySet()) {

                if (e1 != e2) {
                    subsumedType = subsumed(e1.getKey(), e2.getKey());
                    if (subsumedType != null) {
                        break;
                    }
                }
            }

            if (subsumedType == null) {
                ret.put(e1.getKey(), e1.getValue());
            } else {
                ret.put(subsumedType, e1.getValue());
            }
        }

        return ret;
    }


    @Override
    protected String printType(@NotNull CyclicTypeRecorder ctr) {

        if (arrows.isEmpty()) {
            return "? -> ?";
        }

        StringBuilder sb = new StringBuilder();

        Integer num = ctr.visit(this);
        if (num != null) {
            sb.append("#").append(num);
        } else {
            int newNum = ctr.push(this);

            int i = 0;
            Set<String> seen = new HashSet<>();

            for (Map.Entry<Type, Type> e : arrows.entrySet()) {
                String as = e.getKey().printType(ctr) + " -> " + e.getValue().printType(ctr);

                if (!seen.contains(as)) {
                    if (i != 0) {
                        if (Analyzer.self.multilineFunType) {
                            sb.append("\n| ");
                        } else {
                            sb.append(" | ");
                        }
                    }

                    sb.append(as);
                    seen.add(as);
                }

                i++;
            }

            if (ctr.isUsed(this)) {
                sb.append("=#").append(newNum).append(": ");
            }
            ctr.pop(this);
        }
        return sb.toString();
    }
}
