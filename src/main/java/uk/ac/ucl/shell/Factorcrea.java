package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class Factorcrea {
    application specific;
    ArrayList<String> arg;
    String inputFileName;
    String outputFileName;
    public static Boolean subornot = false;

    public Factorcrea(String app, ArrayList<String> arg, OutputStream output, String inputFileName,
            String outputFileName) throws IOException {
        specific = getspecific(app, arg, output, inputFileName, outputFileName);
        this.arg = arg;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        if (subornot == false) {
            specific.exec(arg, output, inputFileName, outputFileName);// but application get executed when command
                                                                      // substitution nor occur
        }

    }

    public String usewhensub() throws IOException {
        return specific.execsub(arg, inputFileName, outputFileName);
    }

    public abstract application getspecific(String app, ArrayList<String> arg, OutputStream output,
            String inputFileName, String outputFileName);
    // speicified application get choosen in this method
}
