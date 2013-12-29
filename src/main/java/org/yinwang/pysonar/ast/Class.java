package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.*;
import org.yinwang.pysonar.types.ClassType;
import org.yinwang.pysonar.types.DictType;
import org.yinwang.pysonar.types.TupleType;
import org.yinwang.pysonar.types.Type;

import java.util.ArrayList;
import java.util.List;


public class Class extends Node {

    @NotNull
    public Name name;
    public List<Node> bases;
    public Node body;


    public Class(@NotNull Name name, List<Node> bases, Node body, String file, int start, int end) {
        super(file, start, end);
        this.name = name;
        this.bases = bases;
        this.body = body;
        addChildren(name, this.body);
        addChildren(bases);
    }


    @Override
    public boolean isClassDef() {
        return true;
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        ClassType classType = new ClassType(name.id, s);
        List<Type> baseTypes = new ArrayList<>();
        for (Node base : bases) {
            List<State> ss = transformExpr(base, s);
            for (State s1 : ss) {
                Type baseType = s1.lookupType(base);
                if (baseType.isClassType()) {
                    classType.addSuper(baseType);
                } else if (baseType.isUnionType()) {
                    for (Type b : baseType.asUnionType().types) {
                        classType.addSuper(b);
                        break;
                    }
                } else {
                    Analyzer.self.putProblem(base, base + " is not a class");
                }
                baseTypes.add(baseType);
            }
        }

        // XXX: Not sure if we should add "bases", "name" and "dict" here. They
        // must be added _somewhere_ but I'm just not sure if it should be HERE.
        addSpecialAttribute(classType.table, "__bases__", new TupleType(baseTypes));
        addSpecialAttribute(classType.table, "__name__", Type.STR);
        addSpecialAttribute(classType.table, "__dict__",
                new DictType(Type.STR, Type.UNKNOWN));
        addSpecialAttribute(classType.table, "__module__", Type.STR);
        addSpecialAttribute(classType.table, "__doc__", Type.STR);

        // Bind ClassType to name here before resolving the body because the
        // methods need this type as self.
        Binder.bind(s, name, classType, Binding.Kind.CLASS);
        return transformExpr(body, classType.table);
    }


    private void addSpecialAttribute(@NotNull State s, String name, Type proptype) {
        Binding b = new Binding(Builtins.newTutUrl("classes.html"), proptype, Binding.Kind.ATTRIBUTE);
        s.put(name, b);
        b.markSynthetic();
        b.markStatic();

    }


    @NotNull
    @Override
    public String toString() {
        return "(class:" + name.id + ":" + start + ")";
    }

}
