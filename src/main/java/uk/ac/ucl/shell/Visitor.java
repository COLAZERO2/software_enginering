package uk.ac.ucl.shell;

public interface Visitor {
    void visit(seq sequece);

    void visit(call Call);

    void visit(Pipe pipe);
}
