package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class call implements command {
    String appName;
    ArrayList<String> appArgs;
    String inputFileName;
    String outputFileName;

    public call(String appname, ArrayList<String> appArgs, String inputFileName, String outputFileName) {
        this.appArgs = appArgs;
        this.appName = appname;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public void eval(String input, OutputStream output) throws IOException {
        if (input.length() > 0) {
            appArgs.add(input);
        }

        new getspecificapp(appName, appArgs, output, inputFileName, outputFileName);

    }

    @Override
    public String evalsub(String input, OutputStream output) throws IOException {
        if (input.length() > 0) {
            appArgs.add(input);
        }
        return new getspecificapp(appName, appArgs, output, inputFileName, outputFileName).usewhensub();

    }

}
