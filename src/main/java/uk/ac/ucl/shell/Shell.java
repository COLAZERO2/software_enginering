package uk.ac.ucl.shell;

//import java.io.BufferedReader;
//import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
//import java.io.OutputStreamWriter;
/*import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;*/
import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.ac.ucl.shell.ShellGrammarParser.CommandContext;

public class Shell {

    public static String currentDirectory = System.getProperty("user.dir");

    public static void eval(String cmdline, OutputStream output) throws IOException {

        CharStream parserInput = CharStreams.fromString(cmdline);
        ShellGrammarLexer lexer = new ShellGrammarLexer(parserInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ShellGrammarParser Parser = new ShellGrammarParser(tokenStream);
        CommandContext commandctx = Parser.command();
        commandconverter visitor = new commandconverter();
        command Command = visitor.visit(commandctx);

        Command.eval("", output);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("COMP0010 shell: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.out.println("COMP0010 shell: " + args[0] + ": unexpected argument");
            }
            try {
                eval(args[1], System.out);
            } catch (Exception e) {
                System.out.println("COMP0010 shell: " + e.getMessage());
            }
        } else {
            Scanner input = new Scanner(System.in);
            try {
                while (true) {
                    String prompt = currentDirectory + "> ";
                    System.out.print(prompt);
                    try {
                        String cmdline = input.nextLine();
                        eval(cmdline, System.out);
                    } catch (Exception e) {
                        System.out.println("COMP0010 shell: " + e.getMessage());
                    }
                }
            } finally {
                input.close();
            }
        }
    }

}
