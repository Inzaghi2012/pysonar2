package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.Binding;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar.types.ListType;
import org.yinwang.pysonar.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


public class ImportFrom extends Node {

    public List<Name> module;
    public List<Alias> names;
    public int level;


    public ImportFrom(List<Name> module, List<Alias> names, int level, String file, int start, int end) {
        super(file, start, end);
        this.module = module;
        this.level = level;
        this.names = names;
        addChildren(names);
    }


    @NotNull
    @Override
    public List<State> transform(@NotNull State s) {
        if (module == null) {
            return s.put(this, Type.CONT);
        }

        Type mod = Analyzer.self.loadModule(module, s);

        if (mod == null) {
            Analyzer.self.putProblem(this, "Cannot load module");
        } else if (isImportStar()) {
            importStar(s, mod);
        } else {
            for (Alias a : names) {
                Name first = a.name.get(0);
                Binding b = mod.table.lookup(first.id);
                if (b != null) {
                    if (a.asname != null) {
                        s.put(a.asname.id, b);
                        Analyzer.self.putRef(a.asname, b);
                    } else {
                        s.put(first.id, b);
                        Analyzer.self.putRef(first, b);
                    }
                } else {
                    List<Name> ext = new ArrayList<>(module);
                    ext.add(first);
                    Type mod2 = Analyzer.self.loadModule(ext, s);
                    if (mod2 != null) {
                        if (a.asname != null) {
                            s.put(a.asname.id, a.asname, mod2, Binding.Kind.VARIABLE);
                        } else {
                            s.put(first.id, first, mod2, Binding.Kind.VARIABLE);
                        }
                    }
                }
            }
        }

        return s.put(this, Type.CONT);
    }


    public boolean isImportStar() {
        return names.size() == 1 && "*".equals(names.get(0).name.get(0).id);
    }


    private void importStar(@NotNull State s, @Nullable Type mt) {
        if (mt == null || mt.file == null) {
            return;
        }

        Node node = Analyzer.self.getAstForFile(mt.file);
        if (node == null) {
            return;
        }

        List<String> names = new ArrayList<>();
        Type allType = mt.table.lookupType("__all__");

        if (allType != null && allType.isListType()) {
            ListType lt = allType.asListType();

            for (Object o : lt.values) {
                if (o instanceof String) {
                    names.add((String) o);
                }
            }
        }

        if (!names.isEmpty()) {
            for (String name : names) {
                Binding b = mt.table.lookupLocal(name);
                if (b != null) {
                    s.put(name, b);
                } else {
                    List<Name> m2 = new ArrayList<>(module);
                    m2.add(new Name(name));
                    Type type = Analyzer.self.loadModule(m2, s);
                    if (type != null) {
                        s.put(name, null, type, Binding.Kind.VARIABLE);
                    }
                }
            }
        } else {
            // Fall back to importing all names not starting with "_".
            for (Entry<Object, Binding> e : mt.table.entrySet()) {
                if (!(e.getKey() instanceof String) && ((String) e.getKey()).startsWith("_")) {
                    s.put(e.getKey(), e.getValue());
                }
            }
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(FromImport:" + module + ":" + names + ")";
    }

}
