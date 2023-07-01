package uk.ac.ucl.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

//class implement pathmatcher to support globbing and filevisitor to traverse the directory want to find
public class filevisitor implements FileVisitor<Path> {
    private PathMatcher globbingmatcher;
    private Path startDir;
    public ArrayList<String> args;

    public filevisitor(String fileglobbing, Path startdir, ArrayList<String> args) {
        FileSystem filesys = FileSystems.getDefault();
        String globbing;
        if (application.infind == true) {
            globbing = "glob:**/" + fileglobbing;
        } else {
            globbing = "glob:" + fileglobbing;
        }
        globbingmatcher = filesys.getPathMatcher(globbing);

        this.startDir = startdir;
        this.args = args;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String path = "." + File.separator;
        if (globbingmatcher.matches(file)) {
            if (startDir.toString().equals(application.currentDirectory)) {
                if (application.infind == true) {
                    path += file.toString().substring(startDir.toString().length() + 1, file.toString().length());
                    // the prefix of file retuen in ./ if it is in finding application to indicate
                    // absolute path
                    args.add(path);
                } else {
                    args.add(file.toString().substring(startDir.toString().length() + 1, file.toString().length()));
                }
            } else {
                String withdir = file.toString().substring(startDir.toString().lastIndexOf(File.separator) + 1,
                        file.toString().length());

                args.add(withdir);// only return the dir after specifed start dir
            }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

        boolean finishedSearch = Files.isSameFile(dir, startDir);
        if (finishedSearch) {

            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    public static void main(String[] args) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        ArrayList<String> aargs = new ArrayList<>();
        Path startingDir = Paths.get(currentDirectory);
        String fileglobbing = "C:/Users/47883/Documents/GitHub/comp0010-shell-java-j8/*.md";
        filevisitor visitor = new filevisitor(fileglobbing, startingDir, aargs);
        Files.walkFileTree(startingDir, visitor);
        System.out.println(currentDirectory);
        for (String arg : aargs) {
            System.out.println(arg);
        }
    }
}