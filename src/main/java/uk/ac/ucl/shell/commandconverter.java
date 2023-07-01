package uk.ac.ucl.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import uk.ac.ucl.shell.ShellGrammarParser.ArgumentContext;
import uk.ac.ucl.shell.ShellGrammarParser.CommandContext;
import uk.ac.ucl.shell.ShellGrammarParser.RedirectionContext;

//in AST it is the encapusulating appied here
public class commandconverter extends ShellGrammarBaseVisitor<command> {

    public command visitCommand(ShellGrammarParser.CommandContext ctx) {
        ArrayList<command> seq = new ArrayList<>();
        if (ctx.call() != null) {
            seq.add(visitCall(ctx.call()));
        }
        if (ctx.pipe() != null) {
            seq.add(visitPipe(ctx.pipe()));
        }

        if (ctx.seq().size() != 0) {

            int i = 0;
            while (i < ctx.seq().size()) {
                seq.add(visitSeq(ctx.seq(i)));
                i++;
            }

        }

        return new seq(seq);
    }

    public command visitSeq(ShellGrammarParser.SeqContext ctx) {
        if (ctx.call() != null) {

            return visitCall(ctx.call());
        }
        if (ctx.pipe() != null) {
            return visitPipe(ctx.pipe());
        }
        return null;
    }

    public command visitPipe(ShellGrammarParser.PipeContext ctx) {
        ArrayList<command> pipectx = new ArrayList<>();
        for (int i = 0; i < ctx.call().size(); i++) {
            pipectx.add(visitCall(ctx.call(i)));
        }
        return new Pipe(pipectx);
    }

    public command visitCall(ShellGrammarParser.CallContext ctx) {
        boolean inputFileExist = false;
        boolean outputFileExist = false;
        String inputFileName = "";
        String outputFileName = "";
        String appname = "";
        ArrayList<String> args = new ArrayList<>();
        for (int i = 0; i < ctx.redirection().size(); i++) {
            // evaluate the redirections in the command
            RedirectionContext redirection = ctx.redirection().get(i);
            if (redirection.getChild(0).getText().equals("<")) {
                if (!inputFileExist) {
                    ArgumentContext actx = redirection.argument();
                    String argStr = "";
                    for (int j = 0; j < actx.getChildCount(); j++) {
                        String content = actx.getChild(j).getText();
                        char quoted = actx.getChild(j).getText().charAt(0);
                        String whole = actx.getText();

                        if (content.contains("`")) {
                            String subcommandstring = whole.substring(whole.indexOf("`") + 1,
                                    whole.indexOf("`", whole.indexOf("`") + 1));
                            Factorcrea.subornot = true;
                            CharStream parserInput = CharStreams.fromString(subcommandstring);
                            ShellGrammarLexer lexer = new ShellGrammarLexer(parserInput);
                            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                            ShellGrammarParser Parser = new ShellGrammarParser(tokenStream);
                            CommandContext commandctx = Parser.command();
                            commandconverter visitor = new commandconverter();
                            command Command = visitor.visit(commandctx);
                            String subcommandoutput = "";
                            try {
                                subcommandoutput = Command.evalsub("", null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String completeform = ctx.getText();
                            StringBuilder SB = new StringBuilder(completeform);
                            SB.replace(SB.indexOf("`"), SB.lastIndexOf("`") + 1, subcommandoutput);
                            completeform = SB.toString();
                            Factorcrea.subornot = false;

                            parserInput = CharStreams.fromString(completeform);
                            lexer = new ShellGrammarLexer(parserInput);
                            tokenStream = new CommonTokenStream(lexer);
                            Parser = new ShellGrammarParser(tokenStream);
                            commandctx = Parser.command();
                            Command = visitor.visit(commandctx);

                            return Command;
                        } else if (quoted == '\'' || quoted == '\"') {
                            String noEsc;
                            if (content.contains("\\")) {
                                noEsc = Escape.ignore(content);
                            } else {
                                noEsc = content;
                            }
                            argStr += noEsc.substring(1, noEsc.length() - 1);
                        } else {
                            String noEsc;
                            if (content.contains("\\")) {
                                noEsc = Escape.ignore(content);
                            } else {
                                noEsc = content;
                            }
                            argStr += noEsc;
                        }
                    }
                    inputFileName = argStr;
                    inputFileExist = true;
                } else {
                    throw new RuntimeException("Error: several files are specified for input redirection");
                }
            } else if (redirection.getChild(0).getText().equals(">")) {
                if (!outputFileExist) {
                    ArgumentContext actx = redirection.argument();
                    String argStr = "";
                    for (int j = 0; j < actx.getChildCount(); j++) {
                        String content = actx.getChild(j).getText();
                        char quoted = actx.getChild(j).getText().charAt(0);
                        String whole = actx.getText();
                        if (content.contains("`")) {
                            // evaluate command substitution
                            String subcommandstring = whole.substring(whole.indexOf("`") + 1,
                                    whole.indexOf("`", whole.indexOf("`") + 1));
                            Factorcrea.subornot = true;
                            CharStream parserInput = CharStreams.fromString(subcommandstring);
                            ShellGrammarLexer lexer = new ShellGrammarLexer(parserInput);
                            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                            ShellGrammarParser Parser = new ShellGrammarParser(tokenStream);
                            CommandContext commandctx = Parser.command();
                            commandconverter visitor = new commandconverter();
                            command Command = visitor.visit(commandctx);
                            String subcommandoutput = "";
                            try {
                                subcommandoutput = Command.evalsub("", null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String completeform = ctx.getText();
                            StringBuilder SB = new StringBuilder(completeform);
                            SB.replace(SB.indexOf("`"), SB.lastIndexOf("`") + 1, subcommandoutput);
                            completeform = SB.toString();
                            Factorcrea.subornot = false;

                            parserInput = CharStreams.fromString(completeform);
                            lexer = new ShellGrammarLexer(parserInput);
                            tokenStream = new CommonTokenStream(lexer);
                            Parser = new ShellGrammarParser(tokenStream);
                            commandctx = Parser.command();
                            Command = visitor.visit(commandctx);

                            return Command;
                        } else if (quoted == '\'' || quoted == '\"') { 
                            // evaluate quoted content
                            String noEsc;
                            if (content.contains("\\")) {
                                noEsc = Escape.ignore(content);
                            } else {
                                noEsc = content;
                            }
                            argStr += noEsc.substring(1, noEsc.length() - 1);

                        } else {
                            String noEsc;
                            if (content.contains("\\")) {
                                noEsc = Escape.ignore(content);
                            } else {
                                noEsc = content;
                            }
                            argStr += noEsc;
                        }
                    }
                    outputFileName = argStr;
                    outputFileExist = true;
                } else {
                    throw new RuntimeException("Error: several files are specified for output redirection");
                }
            }
        }
        for (int j = 0; j < ctx.argument().size(); j++) {
            ArgumentContext actx = ctx.argument().get(j);
            String argStr = "";
            for (int k = 0; k < actx.getChildCount(); k++) {
                String content = actx.getChild(k).getText();
                char quoted = actx.getChild(k).getText().charAt(0);
                String whole = actx.getText();
                if (content.contains("`")) {
                    // evaluate command substitution
                    String subcommandstring = whole.substring(whole.indexOf("`") + 1,
                            whole.indexOf("`", whole.indexOf("`") + 1));

                    Factorcrea.subornot = true;

                    CharStream parserInput = CharStreams.fromString(subcommandstring);
                    ShellGrammarLexer lexer = new ShellGrammarLexer(parserInput);
                    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                    ShellGrammarParser Parser = new ShellGrammarParser(tokenStream);
                    CommandContext commandctx = Parser.command();
                    commandconverter visitor = new commandconverter();
                    command Command = visitor.visit(commandctx);
                    String subcommandoutput = "";
                    try {
                        subcommandoutput = Command.evalsub("", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String completeform = ctx.getText();
                    StringBuilder SB = new StringBuilder(completeform);
                    SB.replace(SB.indexOf("`"), SB.lastIndexOf("`") + 1, subcommandoutput);
                    completeform = SB.toString();
                    Factorcrea.subornot = false;

                    parserInput = CharStreams.fromString(completeform);
                    lexer = new ShellGrammarLexer(parserInput);
                    tokenStream = new CommonTokenStream(lexer);
                    Parser = new ShellGrammarParser(tokenStream);
                    commandctx = Parser.command();
                    Command = visitor.visit(commandctx);

                    return Command;
                } else if (content.contains("*")
                        || appname.equals("find") && j == ctx.argument().size() - 1) {
                            // evaluate file globbing
                    String currentDirectory =application.currentDirectory;
                    if (appname.equals("find") && ctx.argument().size() == 4) {
                        currentDirectory = args.get(0);
                        args.remove(0);
                    }

                    if (appname.equals("find")) {
                        args.remove(0);
                        application.infind = true;
                    }

                    Path startingDir = Paths.get(currentDirectory);
                    filevisitor visitor;
                    String sb = "";
                    if (quoted == '\'' || quoted == '\"') {
                        // evaluate quoted content
                        if (application.infind == false) {

                            String fullgob = currentDirectory + File.separator
                                    + content.substring(1, content.length() - 1);

                            sb = fullgob.replaceAll("\\\\", "/");
                        } else {
                            sb = content.substring(1, content.length() - 1);
                        }

                        visitor = new filevisitor(sb, startingDir,
                                args);
                    } else {

                        if (application.infind == false) {

                            String fullgob = currentDirectory + File.separator + content.substring(0, content.length());

                            sb = fullgob.replaceAll("\\\\", "/");
                        } else {
                            sb = content.substring(0, content.length());
                        }

                        visitor = new filevisitor(sb, startingDir,
                                args);

                    }
                    try {
                        Files.walkFileTree(startingDir, visitor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    application.infind=false;
                } else if (quoted == '\'' || quoted == '\"') {
                    String noEsc;
                    if (content.contains("\\")) {
                        noEsc = Escape.ignore(content);
                    } else {
                        noEsc = content;
                    }
                    argStr += noEsc.substring(1, noEsc.length() - 1);
                } else {
                    String noEsc;
                    if (content.contains("\\")) {
                        noEsc = Escape.ignore(content);
                    } else {
                        noEsc = content;
                    }
                    argStr += noEsc;
                }
            }

            if (j == 0) {
                appname = argStr;
            } else {
                args.add(argStr);
            }
        }

        return new call(appname, args, inputFileName, outputFileName);

    }
}