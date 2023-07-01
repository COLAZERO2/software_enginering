package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class seq implements command {
    ArrayList<command> seq = new ArrayList<>();

    public seq(ArrayList<command> seq) {
        this.seq = seq;
    }

    @Override
    public void eval(String input, OutputStream output) throws IOException {
        int i = 0;
        application.inseq = true;
        // go in the sequence
        for (command command : seq) {
            if (application.seqErr) {
                application.seqErr = false;
                return;
                // stop the sequence while encountering an error
            }
            if (i == seq.size() - 1) {
                application.inseq = false;
                // quit the sequence
            }
            command.eval(input, output);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);

    }

    @Override
    public String evalsub(String input, OutputStream output) throws IOException {
        String subseq = "";
        application.inseq = true;
        for (int i = 0; i < seq.size(); i++) {
            if (application.seqErr) {
                application.seqErr = false;
            }
            if (i == seq.size() - 1) {
                subseq += seq.get(i).evalsub(input, output);
                application.inseq = false;
            } else {
                subseq += seq.get(i).evalsub(input, output) + " ";
            }
        }
        return subseq;
    }

}
