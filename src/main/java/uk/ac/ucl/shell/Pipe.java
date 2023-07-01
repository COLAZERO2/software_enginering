package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Pipe implements command {
    ArrayList<command> pipectx;

    public Pipe(ArrayList<command> pipectx) {
        this.pipectx = pipectx;
    }

    @Override

    public void eval(String input, OutputStream output) throws IOException {
        application.exec_pipe = true;
        // go in the pipe
        for (int i = 0; i < pipectx.size(); i++) {
            if (i == pipectx.size() - 1) {
                application.exec_pipe = false;
                // quit the pipe
            }
            pipectx.get(i).eval(application.currentOutput, output);
        }

    }

    @Override
    public void accept(Visitor visitor) {
    }

    @Override
    public String evalsub(String input, OutputStream output) throws IOException {
        application.exec_pipe = true;
        for (int i = 0; i < pipectx.size(); i++) {
            if (i == pipectx.size() - 1) {
                application.exec_pipe = false;
                return pipectx.get(i).evalsub(application.currentOutput, output);
            }
            pipectx.get(i).evalsub(application.currentOutput, output);
        }

        return null;
    }

}
