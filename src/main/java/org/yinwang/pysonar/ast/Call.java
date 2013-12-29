package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.*;
import org.yinwang.pysonar.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.yinwang.pysonar.Binding.Kind.ATTRIBUTE;
import static org.yinwang.pysonar.Binding.Kind.CLASS;


public class Call extends Node {

    public Node func;
    public List<Node> args;
    @Nullable
    public List<Keyword> keywords;
    public Node kwargs;
    public Node starargs;


    public Call(Node func, List<Node> args, @Nullable List<Keyword> keywords,
                Node kwargs, Node starargs, String file, int start, int end)
    {
        super(file, start, end);
        this.func = func;
        this.args = args;
        this.keywords = keywords;
        this.kwargs = kwargs;
        this.starargs = starargs;
        addChildren(func, kwargs, starargs);
        addChildren(args);
        addChildren(keywords);
    }


    @NotNull
    @Override
    public List<State> transform(State s) {
        List<State> ss = transformExpr(func, s);
        ss = transformList(args, ss);

        if (keywords != null) {
            for (Keyword kw : keywords) {
                ss = transformExpr(kw.value, ss);
            }
        }

        if (kwargs != null) {
            ss = transformExpr(kwargs, ss);
        }
        if (starargs != null) {
            ss = transformExpr(starargs, ss);
        }

        for (State s1 : ss) {
            Type fun = s1.lookupType(func);
            if (fun instanceof FunType) {
                List<Type> pos = new ArrayList<>();
                for (Node arg : args) {
                    pos.add(s1.lookupType(arg));
                }

                Map<String, Type> hash = new HashMap<>();
                if (keywords != null) {
                    for (Keyword kw : keywords) {
                        hash.put(kw.arg, s1.lookupType(kw.value));
                    }
                }

                Type kw = kwargs == null ? null : s1.lookupType(kwargs);
                Type star = starargs == null ? null : s1.lookupType(starargs);

                apply((FunType) fun, pos, hash, kw, star, this, s1);
            } else {
                Analyzer.self.putProblem(this, "calling non-function: " + func);
            }
        }
        return ss;
    }


    @NotNull
    public static void apply(@NotNull FunType func,
                             @Nullable List<Type> pos,
                             Map<String, Type> hash,
                             Type kw,
                             Type star,
                             @Nullable Node call,
                             State s)
    {
        Analyzer.self.removeUncalled(func);

        if (func.func != null && !func.func.called) {
            Analyzer.self.nCalled++;
            func.func.called = true;
        }

        if (func.func == null) {
            // func without definition (possibly builtins)
            s.put(call, func.getReturnType());
        } else if (call != null && Analyzer.self.inStack(call)) {
            func.setSelfType(null);
            s.put(call, Type.UNKNOWN);
        }

        if (call != null) {
            Analyzer.self.pushStack(call);
        }

        List<Type> pTypes = new ArrayList<>();

        // Python: bind first parameter to self type
        if (func.selfType != null) {
            pTypes.add(func.selfType);
        } else {
            if (func.cls != null) {
                pTypes.add(func.cls.getCanon());
            }
        }

        if (pos != null) {
            pTypes.addAll(pos);
        }

        bindMethodAttrs(func);

        State funcTable = new State(func.env, State.StateType.FUNCTION);

        if (func.table.parent != null) {
            funcTable.setPath(func.table.parent.extendPath(func.func.name.id));
        } else if (func.func != null) {
            funcTable.setPath(func.func.name.id);
        }

        Type fromType = func.func == null ? Type.UNKNOWN :
                bindParams(call, func.func, funcTable, func.func.args,
                        func.func.vararg, func.func.kwarg,
                        pTypes, func.defaultTypes, hash, kw, star);

        Type cachedTo = func.getMapping(fromType);
        if (cachedTo != null) {
            func.setSelfType(null);
            s.put(call, cachedTo);
        } else {
            List<State> outStates = transformExpr(func.func.body, funcTable);
            for (State os : outStates) {
                Binding b = os.lookup(func.func.body);
                if (b != null) {
                    Type toType = b.type;
                    func.addMapping(fromType, toType);
                    s.put(call, b);
                }
            }

            func.setSelfType(null);
        }
    }


    @NotNull
    static private Type bindParams(@Nullable Node call,
                                   @NotNull Function func,
                                   @NotNull State funcTable,
                                   @Nullable List<Node> args,
                                   @Nullable Name rest,
                                   @Nullable Name restKw,
                                   @Nullable List<Type> pTypes,
                                   @Nullable List<Type> dTypes,
                                   @Nullable Map<String, Type> hash,
                                   @Nullable Type kw,
                                   @Nullable Type star)
    {
        TupleType fromType = new TupleType();
        int pSize = args == null ? 0 : args.size();
        int aSize = pTypes == null ? 0 : pTypes.size();
        int dSize = dTypes == null ? 0 : dTypes.size();
        int nPos = pSize - dSize;

        if (star != null && star.isListType()) {
            star = star.asListType().toTupleType();
        }

        for (int i = 0, j = 0; i < pSize; i++) {
            Node arg = args.get(i);
            Type aType;
            if (i < aSize) {
                aType = pTypes.get(i);
            } else if (i - nPos >= 0 && i - nPos < dSize) {
                aType = dTypes.get(i - nPos);
            } else {
                if (hash != null && args.get(i).isName() &&
                        hash.containsKey(args.get(i).asName().id))
                {
                    aType = hash.get(args.get(i).asName().id);
                    hash.remove(args.get(i).asName().id);
                } else if (star != null && star.isTupleType() &&
                        j < star.asTupleType().eltTypes.size())
                {
                    aType = star.asTupleType().get(j++);
                } else {
                    aType = Type.UNKNOWN;
                    if (call != null) {
                        Analyzer.self.putProblem(args.get(i),
                                "unable to bind argument:" + args.get(i));
                    }
                }
            }
            Binder.bind(funcTable, arg, aType, Binding.Kind.PARAMETER);
            fromType.add(aType);
        }

        if (restKw != null) {
            if (hash != null && !hash.isEmpty()) {
                Type hashType = UnionType.newUnion(hash.values());
                Binder.bind(
                        funcTable,
                        restKw,
                        new DictType(Type.STR, hashType),
                        Binding.Kind.PARAMETER);
            } else {
                Binder.bind(funcTable,
                        restKw,
                        Type.UNKNOWN,
                        Binding.Kind.PARAMETER);
            }
        }

        if (rest != null) {
            if (pTypes.size() > pSize) {
                if (func.afterRest != null) {
                    int nAfter = func.afterRest.size();
                    for (int i = 0; i < nAfter; i++) {
                        Binder.bind(funcTable, func.afterRest.get(i),
                                pTypes.get(pTypes.size() - nAfter + i),
                                Binding.Kind.PARAMETER);
                    }
                    if (pTypes.size() - nAfter > 0) {
                        Type restType = new TupleType(pTypes.subList(pSize, pTypes.size() - nAfter));
                        Binder.bind(funcTable, rest, restType, Binding.Kind.PARAMETER);
                    }
                } else {
                    Type restType = new TupleType(pTypes.subList(pSize, pTypes.size()));
                    Binder.bind(funcTable, rest, restType, Binding.Kind.PARAMETER);
                }
            } else {
                Binder.bind(funcTable,
                        rest,
                        Type.UNKNOWN,
                        Binding.Kind.PARAMETER);
            }
        }

        return fromType;
    }


    static void bindMethodAttrs(@NotNull FunType cl) {
        if (cl.table.parent != null) {
            Type cls = cl.table.parent.type;
            if (cls != null && cls.isClassType()) {
                addReadOnlyAttr(cl, "im_class", cls, CLASS);
                addReadOnlyAttr(cl, "__class__", cls, CLASS);
                addReadOnlyAttr(cl, "im_self", cls, ATTRIBUTE);
                addReadOnlyAttr(cl, "__self__", cls, ATTRIBUTE);
            }
        }
    }


    static void addReadOnlyAttr(@NotNull FunType fun,
                                String name,
                                @NotNull Type type,
                                Binding.Kind kind)
    {
        Node loc = Builtins.newDataModelUrl("the-standard-type-hierarchy");
        Binding b = new Binding(loc, type, kind);
        fun.table.put(name, b);
        b.markSynthetic();
        b.markStatic();
    }


    static boolean missingReturn(@NotNull Type toType) {
        boolean hasNone = false;
        boolean hasOther = false;

        if (toType.isUnionType()) {
            for (Type t : toType.asUnionType().types) {
                if (t == Type.NONE || t == Type.CONT) {
                    hasNone = true;
                } else {
                    hasOther = true;
                }
            }
        }

        return hasNone && hasOther;
    }


    @NotNull
    @Override
    public String toString() {
        return "(call:" + func + ":" + args + ":" + start + ")";
    }

}
