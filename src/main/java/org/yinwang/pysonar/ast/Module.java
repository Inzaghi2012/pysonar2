package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar._;
import org.yinwang.pysonar.types.ModuleType;

import java.util.List;


public class Module extends Node {

    public Block body;


    public Module(Block body, String file, int start, int end) {
        super(file, start, end);
        this.name = _.moduleName(file);
        this.body = body;
        addChildren(this.body);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        ModuleType mt = new ModuleType(name, file, Analyzer.self.globaltable);
        s.put(_.moduleQname(file), this, mt, Binding.Kind.MODULE);
        return transformExpr(body, mt.table);
    }


    @NotNull
    @Override
    public String toString() {
        return "(module:" + file + ")";
    }

}
