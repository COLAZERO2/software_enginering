package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;

//command interface is used to decorate pipe,call and seq to exec command or deal with backquoted substitution
public interface command {
    void eval(String input, OutputStream output) throws IOException;

    String evalsub(String input, OutputStream ouput) throws IOException; // use when the command is backquoted

    void accept(Visitor visitor);
}
