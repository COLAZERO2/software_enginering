package uk.ac.ucl.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class getspecificapp extends Factorcrea {

    public getspecificapp(String app, ArrayList<String> arg, OutputStream output, String inputFileName,
            String outputFileName) throws IOException {
        super(app, arg, output, inputFileName, outputFileName);
    }

    @Override
    public application getspecific(String app, ArrayList<String> arg, OutputStream output, String inputFileName,
            String outputFileName) {
        if (app.charAt(0) == '_') {
            application.unsafe = true;
            app = app.substring(1, app.length());
        }
        switch (app) {
            case "cd":
                return new cd();
            case "pwd":
                return new pwd();
            case "ls":
                return new ls();
            case "cat":
                return new cat();
            case "echo":
                return new echo();
            case "head":
                return new head();
            case "tail":
                return new tail();
            case "grep":
                return new grep();
            case "find":
                return new find();
            case "sort":
                return new sort();
            case "uniq":
                return new uniq();
            case "cut":
                return new cut();
            default:
                throw new RuntimeException(app + ": unknown application");
        }
    }

}