package org.yinwang.pysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.Analyzer;
import org.yinwang.pysonar.State;
import org.yinwang.pysonar._;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A Node is a junction in the program.
 * Since there is no way to put different things in the same segment of the same file,
 * a node is uniquely identified by a file, a start and end point.
 */
public abstract class Node implements java.io.Serializable, Comparable<Object> {

    public String file;
    public int start;
    public int end;

    public String name;
    public Node parent = null;


    public Node() {
    }


    public Node(String file, int start, int end) {
        this.file = file;
        this.start = start;
        this.end = end;
    }


    public String getFullPath() {
        if (!file.startsWith("/")) {
            return _.makePathString(Analyzer.self.projectDir, file);
        } else {
            return file;
        }
    }


    public void setParent(Node parent) {
        this.parent = parent;
    }


    @NotNull
    public Node getAstRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getAstRoot();
    }


    public int length() {
        return end - start;
    }


    public void addChildren(@Nullable Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) {
                    n.setParent(this);
                }
            }
        }
    }


    public void addChildren(@Nullable Collection<? extends Node> nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) {
                    n.setParent(this);
                }
            }
        }
    }


    @Nullable
    public Str getDocString() {
        Node body = null;
        if (this instanceof Function) {
            body = ((Function) this).body;
        } else if (this instanceof Class) {
            body = ((Class) this).body;
        } else if (this instanceof Module) {
            body = ((Module) this).body;
        }

        if (body instanceof Block && ((Block) body).seq.size() >= 1) {
            Node firstExpr = ((Block) body).seq.get(0);
            if (firstExpr instanceof Expr) {
                Node docstrNode = ((Expr) firstExpr).value;
                if (docstrNode != null && docstrNode instanceof Str) {
                    return (Str) docstrNode;
                }
            }
        }
        return null;
    }


    // ---------------------- state transformers ---------------------
    @NotNull
    protected abstract List<State> transform(State s);


    @NotNull
    public static List<State> transformExpr(@NotNull Node n, State s) {
        return n.transform(s);
    }


    @NotNull
    public static List<State> transformExpr(@NotNull Node n, List<State> ss) {
        List<State> ret = new ArrayList<>();
        for (State s : ss) {
            ret.addAll(n.transform(s));
        }
        return ret;
    }


    @NotNull
    static protected List<State> transformList(@NotNull Collection<? extends Node> nodes, State s) {
        List<State> ret = new ArrayList<>();
        ret.add(s);
        for (Node n : nodes) {
            List<State> ret2 = new ArrayList<>();
            for (State s1 : ret) {
                List<State> ss = transformExpr(n, s1);
                ret2.addAll(ss);
            }
            ret = ret2;
        }
        return ret;
    }


    @Nullable
    static protected List<State> transformList(@Nullable Collection<? extends Node> nodes, List<State> ss) {
        List<State> ret = new ArrayList<>();
        for (State s : ss) {
            ret.addAll(transformList(nodes, s));
        }
        return ret;
    }


    public boolean isCall() {
        return this instanceof Call;
    }


    public boolean isModule() {
        return this instanceof Module;
    }


    public boolean isClassDef() {
        return false;
    }


    public boolean isFunctionDef() {
        return false;
    }


    public boolean isLambda() {
        return false;
    }


    public boolean isName() {
        return this instanceof Name;
    }


    public boolean isAssign() {
        return this instanceof Assign;
    }


    public boolean isGlobal() {
        return this instanceof Global;
    }


    public boolean isBinOp() {
        return this instanceof BinOp;
    }


    @NotNull
    public BinOp asBinOp() {
        return (BinOp) this;
    }


    @NotNull
    public Call asCall() {
        return (Call) this;
    }


    @NotNull
    public Module asModule() {
        return (Module) this;
    }


    @NotNull
    public Class asClassDef() {
        return (Class) this;
    }


    @NotNull
    public Function asFunctionDef() {
        return (Function) this;
    }


    @NotNull
    public Name asName() {
        return (Name) this;
    }


    @NotNull
    public Assign asAssign() {
        return (Assign) this;
    }


    @NotNull
    public Global asGlobal() {
        return (Global) this;
    }


    protected void addWarning(String msg) {
        Analyzer.self.putProblem(this, msg);
    }


    protected void addError(String msg) {
        Analyzer.self.putProblem(this, msg);
    }


    // nodes are equal if they are from the same file and same starting point
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        } else {
            Node node = (Node) obj;
            String file = this.file;
            return (start == node.start &&
                    end == node.end &&
                    _.same(file, node.file));
        }
    }


    @Override
    public int hashCode() {
        return (file + ":" + start + ":" + end).hashCode();
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Node) {
            return start - ((Node) o).start;
        } else {
            return -1;
        }
    }


    public String toDisplay() {
        return "";
    }


    @NotNull
    @Override
    public String toString() {
        return "(node:" + file + ":" + name + ":" + start + ")";
    }

}
