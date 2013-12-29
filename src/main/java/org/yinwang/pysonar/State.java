package org.yinwang.pysonar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.ast.Name;
import org.yinwang.pysonar.ast.Node;
import org.yinwang.pysonar.types.Type;

import java.util.*;
import java.util.Map.Entry;


public class State {
    public enum StateType {
        CLASS,
        INSTANCE,
        FUNCTION,
        MODULE,
        GLOBAL,
        SCOPE
    }


    @NotNull
    public Map<Object, Binding> table = new HashMap<>();
    @Nullable
    public State parent;      // all are non-null except global table
    @Nullable
    public State forwarding; // link to the closest non-class scope, for lifting functions out
    @Nullable
    public List<State> supers;
    @Nullable
    public Set<String> globalNames;
    public StateType stateType;
    public Type type;
    @NotNull
    public String path = "";


    public State(@Nullable State parent, StateType type) {
        this.parent = parent;
        this.stateType = type;

        if (type == StateType.CLASS) {
            this.forwarding = parent == null ? null : parent.getForwarding();
        } else {
            this.forwarding = this;
        }
    }


    public State(@NotNull State s) {
        this.table = new HashMap<>();
        this.table.putAll(s.table);
        this.parent = s.parent;
        this.stateType = s.stateType;
        this.forwarding = s.forwarding;
        this.supers = s.supers;
        this.globalNames = s.globalNames;
        this.type = s.type;
        this.path = s.path;
    }


    // erase and overwrite this to s's contents
    public void overwrite(@NotNull State s) {
        this.table = s.table;
        this.parent = s.parent;
        this.stateType = s.stateType;
        this.forwarding = s.forwarding;
        this.supers = s.supers;
        this.globalNames = s.globalNames;
        this.type = s.type;
        this.path = s.path;
    }


    @NotNull
    public State copy() {
        return new State(this);
    }


    public static List<State> copy(List<State> ss) {
        List<State> ret = new ArrayList<>();
        for (State s : ss) {
            ret.add(s.copy());
        }
        return ret;
    }


    public void setParent(@Nullable State parent) {
        this.parent = parent;
    }


    public State getForwarding() {
        if (forwarding != null) {
            return forwarding;
        } else {
            return this;
        }
    }


    public void addSuper(State sup) {
        if (supers == null) {
            supers = new ArrayList<>();
        }
        supers.add(sup);
    }


    public void setStateType(StateType type) {
        this.stateType = type;
    }


    public void addGlobalName(@NotNull String name) {
        if (globalNames == null) {
            globalNames = new HashSet<>();
        }
        globalNames.add(name);
    }


    public boolean isGlobalName(@NotNull String name) {
        if (globalNames != null) {
            return globalNames.contains(name);
        } else if (parent != null) {
            return parent.isGlobalName(name);
        } else {
            return false;
        }
    }


    public void remove(String id) {
        table.remove(id);
    }


    public void put(@NotNull Object o, @NotNull Binding b) {
        if (o instanceof Name) {
            table.put(((Name) o).id, b);
        } else {
            table.put(o, b);
        }
    }


    public List<State> put(@NotNull Node node, Type type) {
        put(node, new Binding(node, type, Binding.Kind.SCOPE));
        return single();
    }


    public void put(String id, @NotNull Node node, Type type, Binding.Kind kind) {
        Binding b = new Binding(node, type, kind);
        if (type.isModuleType()) {
            b.setQname(type.asModuleType().qname);
        } else {
            b.setQname(extendPath(id));
        }
        put(id, b);
    }


    public void setPath(@NotNull String path) {
        this.path = path;
    }


    public void setType(Type type) {
        this.type = type;
    }


    /**
     * Look up a name in the current symbol table only. Don't recurse on the
     * parent table.
     */
    @Nullable
    public Binding lookupLocal(Object name) {
        if (name instanceof Name) {
            return table.get(((Name) name).id);
        } else {
            return table.get(name);
        }
    }


    /**
     * Look up a name (String) in the current symbol table.  If not found,
     * recurse on the parent table.
     */
    @Nullable
    public Binding lookup(@NotNull Object node) {
        Binding b = getModuleBindingIfGlobal(node);
        if (b != null) {
            return b;
        } else {
            Binding ent = lookupLocal(node);
            if (ent != null) {
                return ent;
            } else {
                if (parent != null) {
                    return parent.lookup(node);
                } else {
                    return null;
                }
            }
        }
    }


    /**
     * Look up a name in the module if it is declared as global, otherwise look
     * it up locally.
     */
    @Nullable
    public Binding lookupScope(String name) {
        Binding b = getModuleBindingIfGlobal(name);
        if (b != null) {
            return b;
        } else {
            return lookupLocal(name);
        }
    }


    /**
     * Look up an attribute in the type hierarchy.  Don't look at parent link,
     * because the enclosing scope may not be a super class. The search is
     * "depth first, left to right" as in Python's (old) multiple inheritance
     * rule. The new MRO can be implemented, but will probably not introduce
     * much difference.
     */
    @NotNull
    private static Set<State> looked = new HashSet<>();    // circularity prevention


    @Nullable
    public Binding lookupAttr(String attr) {
        if (looked.contains(this)) {
            return null;
        } else {
            Binding b = lookupLocal(attr);
            if (b != null) {
                return b;
            } else {
                if (supers != null && !supers.isEmpty()) {
                    looked.add(this);
                    for (State p : supers) {
                        b = p.lookupAttr(attr);
                        if (b != null) {
                            looked.remove(this);
                            return b;
                        }
                    }
                    looked.remove(this);
                    return null;
                } else {
                    return null;
                }
            }
        }
    }


    /**
     * Look for a binding named {@code name} and if found, return its type.
     */
    @Nullable
    public Type lookupType(Object node) {
        Binding b = lookup(node);
        if (b == null) {
            return null;
        } else {
            return b.type;
        }
    }


    /**
     * Look for a attribute named {@code attr} and if found, return its type.
     */
    @Nullable
    public Type lookupAttrType(String attr) {
        Binding b = lookupAttr(attr);
        if (b == null) {
            return null;
        } else {
            return b.type;
        }
    }


    /**
     * Find a symbol table of a certain type in the enclosing scopes.
     */
    @Nullable
    public State getStateOfType(StateType type) {
        if (stateType == type) {
            return this;
        } else if (parent == null) {
            return null;
        } else {
            return parent.getStateOfType(type);
        }
    }


    @NotNull
    public State getGlobalTable() {
        State result = getStateOfType(StateType.MODULE);
        if (result != null) {
            return result;
        } else {
            _.die("Couldn't find global table. Shouldn't happen");
            return this;
        }
    }


    @Nullable
    private Binding getModuleBindingIfGlobal(@NotNull Object name) {
        if (name instanceof Name) {
            String id = ((Name) name).id;
            if (isGlobalName(id)) {
                State module = getGlobalTable();
                if (module != this) {
                    return module.lookupLocal(id);
                }
            }
        }
        return null;
    }


    public void putAll(@NotNull State other) {
        table.putAll(other.table);
    }


    public List<State> single() {
        List<State> sl = new ArrayList<>();
        sl.add(this);
        return sl;
    }


    public Type getReturnType() {
        return lookupType(Constants.RETURN_NAME);
    }


    public Binding getReturn() {
        return lookup(Constants.RETURN_NAME);
    }


    @NotNull
    public Set<Object> keySet() {
        return table.keySet();
    }


    @NotNull
    public Collection<Binding> values() {
        return table.values();
    }


    @NotNull
    public Set<Entry<Object, Binding>> entrySet() {
        return table.entrySet();
    }


    public boolean isEmpty() {
        return table.isEmpty();
    }


    @NotNull
    public String extendPath(@NotNull String name) {
        name = _.moduleName(name);
        if (path.equals("")) {
            return name;
        }
        return path + "." + name;
    }


    @NotNull
    @Override
    public String toString() {
        return "(state:" + stateType + ":" + table.keySet() + ")";
    }

}
