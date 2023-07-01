package uk.ac.ucl.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

abstract class application {
    public static String currentDirectory = Shell.currentDirectory;
    public static String currentOutput = ""; // stores the pipe result
    public static boolean exec_pipe = false; // if it is true, the shell will not output the result,
                                             // but will store the result in ths String currentOutput instead
    public static boolean unsafe = false;
    public static boolean infind = false;
    public static boolean inseq = false;
    public static boolean seqErr = false;

    public abstract void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName,
            String outputFileName)
            throws IOException;

    public abstract String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName)
            throws IOException;
}

class cd extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0 && outputFileName.length() == 0)) {
            if (unsafe) {
                // if the application is called for an unsafe version, do not throw an
                // exception.
                return;
            } else if (inseq) {
                // if in sequence, do not throw an exception and stop the sequence.
                seqErr = true;
                return;
            }
            throw new RuntimeException("cd: not executable with file IO!");
        }
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cd: missing appArgsument");
        } else if (AppArgs.size() > 1) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cd: too many appArgsuments");
        }
        currentOutput = "";
        String dirString = AppArgs.get(0);
        File dir = new File(currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cd: " + dirString + " is not an existing directory");
        }
        Shell.currentDirectory = dir.getCanonicalPath();
        currentDirectory = Shell.currentDirectory;

    }

    @Override
    // if the application is executed in a backquoted content, run this function
    // instead
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName) throws IOException {
        if (!(inputFileName.length() == 0 && outputFileName.length() == 0)) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cd: not executable with file IO!");
        }
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cd: missing appArgsument");
        } else if (AppArgs.size() > 1) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cd: too many appArgsuments");
        }
        currentOutput = "";
        String dirString = AppArgs.get(0);
        File dir = new File(currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cd: " + dirString + " is not an existing directory");
        }
        Shell.currentDirectory = dir.getCanonicalPath();
        currentDirectory = Shell.currentDirectory;
        return "";
    }

}

class pwd extends application {

    @Override
    public void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("pwd: not executable with file input!");
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        if (!exec_pipe) {
            writer.write(currentDirectory);
            writer.write(System.getProperty("line.separator"));
        } else {
            currentOutput = currentDirectory;
        }
        writer.flush();
        if (!(outputFileName.length() == 0)) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("pwd : failed to create the output file!");
                }
            }
            FileWriter fr = new FileWriter(outputFile);
            BufferedWriter br = new BufferedWriter(fr);
            try {
                br.write(currentDirectory);
            } catch (Exception e) {
                br.close();
                fr.close();
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("pwd: failed to create the output file");
            }
            br.close();
            fr.close();

        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName) throws IOException {
        if (!(inputFileName.length() == 0)) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("pwd: not executable with file input!");
        }
        currentOutput = "";
        String pwdsub = "";
        if (!exec_pipe) {
            pwdsub = currentDirectory + " ";

        } else {
            currentOutput = currentDirectory;
        }
        if (!(outputFileName.length() == 0)) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("pwd : failed to create the output file!");
                }
            }
            FileWriter fr = new FileWriter(outputFile);
            BufferedWriter br = new BufferedWriter(fr);
            try {
                br.write(currentDirectory);
            } catch (Exception e) {
                br.close();
                fr.close();
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("pwd: failed to create the output file");
            }
            br.close();
            fr.close();

        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

        pwdsub = Escape.add(pwdsub);

        return pwdsub;
    }

}

class cut extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        boolean frompipe = false;
        currentOutput = "";
        String fileoutput = "";
        if (AppArgs.size() < 2) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cut: wrong number of appArgsuments");
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cut: missing appArgsuments");
        }

        if (AppArgs.size() >= 2 && !AppArgs.get(0).equals("-b")) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cut: wrong appArgsument " + AppArgs.get(0));
        }
        if (inputFileName.length() != 0) {
            AppArgs.add(inputFileName);

        }
        String targetfileStr;

        targetfileStr = AppArgs.get(2);

        Path filePath = null;
        ArrayList<String> storage = new ArrayList<>();
        File tappArgFile = new File(currentDirectory + File.separator + targetfileStr);
        if (tappArgFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            filePath = Paths.get((String) currentDirectory + File.separator + targetfileStr);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);

                }
                reader.close();
            } catch (IOException e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("cut: cannot open " + targetfileStr);
            }
        } else {
            String[] stdin = targetfileStr.split(System.getProperty("line.separator"));
            for (String line : stdin) {

                storage.add(line);
            }
            frompipe = true;
        }
        OutputStream wb = null;
        if (frompipe == false) {
            wb = new FileOutputStream(filePath.toString());
        }
        ArrayList<ArrayList<Byte>> Bytear = new ArrayList<>();
        String[] getcut_section = AppArgs.get(1).split(",");

        for (int i = 0; i < storage.size(); i++) {
            ArrayList<Byte> linebyte = new ArrayList<>();
            for (byte b : storage.get(i).getBytes()) {

                linebyte.add(b);

            }
            Bytear.add(linebyte);
        }

        for (ArrayList<Byte> modify_line : Bytear) {

            int gap = 0;
            for (int index = 0; index < getcut_section.length; index++) {

                String position_remove = getcut_section[index];
                if (Character.isDigit(position_remove.charAt(0)) && position_remove.length() == 1) {
                    int remove_index = Integer.parseInt(position_remove);
                    if (index > 0 && remove_index <= Integer.parseInt(getcut_section[index - 1]
                            .substring(getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                        continue;
                    }

                    byte b = modify_line.get(remove_index - gap - 1);
                    if (exec_pipe) {

                        currentOutput += (char) b;

                    } else {
                        writer.write(modify_line.get(remove_index - gap - 1));

                    }
                    fileoutput += (char) b;

                    modify_line.remove(remove_index - gap - 1);

                    gap++;

                }

                else if (position_remove.length() == 2) {

                    if (position_remove.charAt(0) == '-' && Character.isDigit(position_remove.charAt(1))) {

                        int frombeg;
                        try {
                            frombeg = Integer.parseInt(position_remove.substring(1, 2));
                        } catch (NumberFormatException e) {
                            if (unsafe) {
                                return;
                            } else if (inseq) {
                                seqErr = true;
                                return;
                            }
                            throw new NumberFormatException("not correct format");
                        }

                        if (index != 0) {
                            break;

                        }

                        int j = 0;
                        while (j < frombeg) {
                            byte b = modify_line.get(0);
                            fileoutput += (char) b;
                            if (exec_pipe) {

                                currentOutput += (char) b;
                            } else {
                                writer.write(modify_line.get(0));
                            }
                            modify_line.remove(0);
                            j++;
                        }

                        gap += frombeg;

                    } else if (position_remove.charAt(1) == '-' && Character.isDigit(position_remove.charAt(0))) {
                        int toend;
                        try {
                            toend = Integer.parseInt(position_remove.substring(0, 1));
                        } catch (NumberFormatException e) {
                            if (unsafe) {
                                return;
                            } else if (inseq) {
                                seqErr = true;
                                return;
                            }
                            throw new NumberFormatException("not correct format");
                        }

                        if (index > 0 && toend <= Integer.parseInt(getcut_section[index - 1].substring(
                                getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                            continue;
                        }
                        int j = 0;
                        final int tosize = modify_line.size();

                        while (j < tosize + gap - toend + 1) {
                            byte b = modify_line.get(toend - gap - 1);
                            fileoutput += (char) b;
                            if (exec_pipe) {

                                currentOutput += (char) b;

                            } else {
                                writer.write(modify_line.get(toend - gap - 1));
                            }
                            modify_line.remove(toend - gap - 1);
                            j++;

                        }

                        if (index != getcut_section.length - 1) {
                            break;
                        }

                    }

                } else if (position_remove.length() == 3 && position_remove.charAt(1) == '-') {

                    int begin;
                    int end;
                    try {

                        begin = Integer.parseInt(position_remove.substring(0, 1));
                        end = Integer.parseInt(position_remove.substring(2, 3));
                    } catch (NumberFormatException e) {
                        if (unsafe) {
                            return;
                        } else if (inseq) {
                            seqErr = true;
                            return;
                        }
                        throw new NumberFormatException("not correct format");
                    }
                    if (index > 0 && begin <= Integer.parseInt(getcut_section[index - 1]
                            .substring(getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                        continue;
                    }

                    int j = 0;
                    while (j < end - begin + 1) {
                        byte b = modify_line.get(begin - gap - 1);
                        fileoutput += (char) b;
                        if (exec_pipe) {

                            currentOutput += (char) b;
                        } else {
                            writer.write(modify_line.get(begin - gap - 1));
                        }
                        modify_line.remove(begin - gap - 1);
                        j++;
                    }

                    gap += end - begin + 1;

                }

            }
            fileoutput += System.getProperty("line.separator");
            if (exec_pipe) {
                currentOutput += System.getProperty("line.separator");
            } else {
                writer.write(System.getProperty("line.separator"));

            }

        }
        writer.close();
        if (wb != null) {
            for (ArrayList<Byte> writetofile : Bytear) {
                for (byte bytetofile : writetofile) {
                    wb.write(bytetofile);

                }
                if (writetofile != Bytear.get(Bytear.size() - 1)) {
                    wb.write(System.getProperty("line.separator").charAt(0));
                }
            }
        }
        if (wb != null) {
            wb.close();
        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("cut: failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }

    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName)
            throws IOException {
        boolean frompipe = false;
        String subcut = "";
        currentOutput = "";
        if (AppArgs.size() < 3) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cut: wrong number of appArgsuments");
        }

        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cut: missing appArgsuments");
        }

        if (AppArgs.size() >= 3 && !AppArgs.get(0).equals("-b")) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cut: wrong appArgsument " + AppArgs.get(0));
        }
        String tappArgsetappArgs;
        tappArgsetappArgs = AppArgs.get(2);
        Path filePath = null;
        ArrayList<String> storage = new ArrayList<>();
        File tappArgsetFile = new File(currentDirectory + File.separator + tappArgsetappArgs);
        if (tappArgsetFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            filePath = Paths.get((String) currentDirectory + File.separator + tappArgsetappArgs);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);

                }
                reader.close();
            } catch (IOException e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("cut: cannot open " + tappArgsetappArgs);
            }
        } else {
            storage.add(tappArgsetappArgs);
            frompipe = true;
        }
        OutputStream wb = null;
        if (frompipe == false) {
            wb = new FileOutputStream(filePath.toString());
        }
        ArrayList<ArrayList<Byte>> Bytear = new ArrayList<>();
        String[] getcut_section = AppArgs.get(1).split(",");

        for (int i = 0; i < storage.size(); i++) {
            ArrayList<Byte> linebyte = new ArrayList<>();
            for (byte b : storage.get(i).getBytes()) {

                linebyte.add(b);

            }
            Bytear.add(linebyte);
        }

        for (ArrayList<Byte> modify_line : Bytear) {

            int gap = 0;
            for (int index = 0; index < getcut_section.length; index++) {

                String position_remove = getcut_section[index];
                if (Character.isDigit(position_remove.charAt(0)) && position_remove.length() == 1) {
                    int remove_index = Integer.parseInt(position_remove);
                    if (index > 0 && remove_index <= Integer.parseInt(getcut_section[index - 1]
                            .substring(getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                        continue;
                    }

                    byte b = modify_line.get(remove_index - gap - 1);
                    subcut += (char) b + " ";
                    modify_line.remove(remove_index - gap - 1);

                    gap++;

                }

                else if (position_remove.length() == 2) {

                    if (position_remove.charAt(0) == '-' && Character.isDigit(position_remove.charAt(1))) {

                        int frombeg;
                        try {
                            frombeg = Integer.parseInt(position_remove.substring(1, 2));
                        } catch (NumberFormatException e) {
                            if (unsafe) {
                                return "";
                            } else if (inseq) {
                                seqErr = true;
                                return "";
                            }
                            throw new NumberFormatException("not correct format");
                        }

                        if (index != 0) {
                            break;

                        }

                        int j = 0;
                        while (j < frombeg) {
                            byte b = modify_line.get(0);
                            subcut += (char) b;
                            modify_line.remove(0);
                            j++;
                        }
                        subcut += " ";
                        gap += frombeg;

                    } else if (position_remove.charAt(1) == '-' && Character.isDigit(position_remove.charAt(0))) {

                        int toend;
                        try {
                            toend = Integer.parseInt(position_remove.substring(0, 1));
                        } catch (NumberFormatException e) {
                            if (unsafe) {
                                return "";
                            } else if (inseq) {
                                seqErr = true;
                                return "";
                            }
                            throw new NumberFormatException("not correct format");
                        }

                        if (index > 0 && toend <= Integer.parseInt(getcut_section[index - 1].substring(
                                getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                            continue;
                        }
                        int j = 0;
                        final int tosize = modify_line.size();

                        while (j < tosize + gap - toend + 1) {
                            byte b = modify_line.get(toend - gap - 1);
                            subcut += (char) b;
                            modify_line.remove(toend - gap - 1);
                            j++;

                        }
                        subcut += " ";
                        if (index != getcut_section.length - 1) {
                            break;
                        }

                    }

                } else if (position_remove.length() == 3 && position_remove.charAt(1) == '-') {

                    int begin;
                    int end;
                    try {

                        begin = Integer.parseInt(position_remove.substring(0, 1));
                        end = Integer.parseInt(position_remove.substring(2, 3));
                    } catch (NumberFormatException e) {
                        if (unsafe) {
                            return "";
                        } else if (inseq) {
                            seqErr = true;
                            return "";
                        }
                        throw new NumberFormatException("not correct format");
                    }
                    if (index > 0 && begin <= Integer.parseInt(getcut_section[index - 1]
                            .substring(getcut_section[index - 1].length() - 1, getcut_section[index - 1].length()))) {
                        continue;
                    }

                    int j = 0;
                    while (j < end - begin + 1) {
                        byte b = modify_line.get(begin - gap - 1);
                        subcut += (char) b;
                        modify_line.remove(begin - gap - 1);
                        j++;
                    }
                    subcut += " ";
                    gap += end - begin + 1;

                }

            }

            subcut += System.getProperty("line.separator");
        }

        if (wb != null) {
            for (ArrayList<Byte> writetofile : Bytear) {
                for (byte bytetofile : writetofile) {
                    wb.write(bytetofile);
                }
                if (writetofile != Bytear.get(Bytear.size() - 1)) {
                    wb.write(System.getProperty("line.separator").charAt(0));
                }
            }
        }
        if (wb != null) {
            wb.close();
        }
        if (exec_pipe) {
            currentOutput = Escape.add(subcut);
        }
        return Escape.add(subcut);
    }
}

class ls extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("ls: not executable with file input!");
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        File currDir;
        if (AppArgs.isEmpty()) {
            currDir = new File(currentDirectory);
        } else if (AppArgs.size() == 1) {
            String tempPath;
            if ((currentDirectory.charAt(currentDirectory.length() - 1) + "").equals(File.separator)) {
                tempPath = currentDirectory + AppArgs.get(0);
            } else {
                tempPath = currentDirectory + File.separator + AppArgs.get(0);
            }
            currDir = new File(tempPath);
        } else {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("ls: too many appArgsuments");
        }
        try {
            File[] listOfFiles = currDir.listFiles();
            boolean atLeastOnePrinted = false;
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    if (exec_pipe) {
                        currentOutput = currentOutput + file.getName() + "\t";
                    } else {
                        writer.write(file.getName());
                        writer.write("\t");
                        stringOutput = stringOutput + file.getName() + "\t";
                    }
                    writer.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted && !exec_pipe) {
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
            if (!(outputFileName.length() == 0)) {
                if (exec_pipe) {
                    stringOutput = currentOutput;
                }
                String outputPath = currentDirectory + File.separator + outputFileName;
                File outputFile = new File(outputPath);
                outputFile.getParentFile().mkdirs();
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile();
                    } catch (Exception e) {
                        if (unsafe) {
                            return;
                        } else if (inseq) {
                            throw new RuntimeException("");
                        }
                        throw new RuntimeException("ls : failed to create the output file!");
                    }
                }
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                try {
                    br.write(stringOutput);
                } catch (Exception e) {
                    br.close();
                    fr.close();
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("ls: failed to create the output file");
                }
                br.close();
                fr.close();

            }
        } catch (NullPointerException e) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("ls: no such directory");
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName) throws IOException {
        if (!(inputFileName.length() == 0)) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("ls: not executable with file input!");
        }
        String stringOutput = "";
        currentOutput = "";
        String subls = "";
        File currDir;
        if (AppArgs.isEmpty()) {
            currDir = new File(currentDirectory);
        } else if (AppArgs.size() == 1) {
            String tempPath;
            if ((currentDirectory.charAt(currentDirectory.length() - 1) + "").equals(File.separator)) {
                tempPath = currentDirectory + AppArgs.get(0);
            } else {
                tempPath = currentDirectory + File.separator + AppArgs.get(0);
            }
            currDir = new File(tempPath);
        } else {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("ls: too many appArgsuments");
        }
        try {
            File[] listOfFiles = currDir.listFiles();
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    if (exec_pipe) {
                        currentOutput = currentOutput + file.getName() + "\t";
                    } else {
                        subls += file.getName() + "\t";
                        stringOutput = stringOutput + file.getName() + "\t";

                    }

                }
            }
            if (!(outputFileName.length() == 0)) {
                if (exec_pipe) {
                    stringOutput = currentOutput;
                }
                String outputPath = currentDirectory + File.separator + outputFileName;
                File outputFile = new File(outputPath);
                outputFile.getParentFile().mkdirs();
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile();
                    } catch (Exception e) {
                        if (unsafe) {
                            return "";
                        } else if (inseq) {
                            seqErr = true;
                            return "";
                        }
                        throw new RuntimeException("ls : failed to create the output file!");
                    }
                }
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                try {
                    br.write(stringOutput);
                } catch (Exception e) {
                    br.close();
                    fr.close();
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("ls: failed to create the output file");
                }
                br.close();
                fr.close();

            }
        } catch (NullPointerException e) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("ls: no such directory");
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subls = Escape.add(subls);

        return subls;
    }
}

class cat extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("cat: missing appArgsuments");
        } else {
            for (String appArgs : AppArgs) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + appArgs);
                if (currFile.exists()) {
                    Path filePath = Paths.get(currentDirectory + File.separator + appArgs);
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            if (exec_pipe) {
                                currentOutput += line + System.getProperty("line.separator");
                            } else {
                                writer.write(line);
                                writer.write(System.getProperty("line.separator"));
                                stringOutput += line + System.getProperty("line.separator");
                            }
                            writer.flush();
                        }
                    } catch (IOException e) {
                        if (unsafe) {
                            return;
                        } else if (inseq) {
                            seqErr = true;
                            return;
                        }
                        throw new RuntimeException("cat: cannot open " + appArgs);
                    }
                } else {
                    if (exec_pipe) {
                        currentOutput += appArgs;
                    } else {
                        writer.write(appArgs);
                        writer.write(System.getProperty("line.separator"));
                        stringOutput += appArgs + System.getProperty("line.separator");
                    }
                }
            }
            if (currentOutput.length() > 0) {
                currentOutput = currentOutput.substring(0, currentOutput.length() - 1);
            }
            if (stringOutput.length() > 0) {
                stringOutput = stringOutput.substring(0, stringOutput.length() - 1);
            }
            if (!(outputFileName.length() == 0)) {
                if (exec_pipe) {
                    stringOutput = currentOutput;
                }
                String outputPath = currentDirectory + File.separator + outputFileName;
                File outputFile = new File(outputPath);
                outputFile.getParentFile().mkdirs();
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile();
                    } catch (Exception e) {
                        if (unsafe) {
                            return;
                        } else if (inseq) {
                            seqErr = true;
                            return;
                        }
                        throw new RuntimeException("cat : failed to create the output file!");
                    }
                }
                try {
                    FileWriter fr = new FileWriter(outputFile);
                    BufferedWriter br = new BufferedWriter(fr);
                    br.write(stringOutput);
                    br.close();
                    fr.close();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("cat : failed to create the output file!");
                }

            }
        }
        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName) throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        currentOutput = "";
        String subcat = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("cat: missing appArgsuments");
        } else {
            for (String appArgs : AppArgs) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + appArgs);
                if (currFile.exists()) {
                    Path filePath = Paths.get(currentDirectory + File.separator + appArgs);
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            if (exec_pipe) {
                                currentOutput += line + System.getProperty("line.separator");
                            } else {
                                subcat += line + System.getProperty("line.separator");
                                stringOutput += line + System.getProperty("line.separator");
                            }

                        }
                    } catch (IOException e) {
                        if (unsafe) {
                            return "";
                        } else if (inseq) {
                            seqErr = true;
                            return "";
                        }
                        throw new RuntimeException("cat: cannot open " + appArgs);
                    }
                } else {
                    if (exec_pipe) {
                        currentOutput += appArgs;
                    } else {
                        subcat += appArgs;
                        subcat += System.getProperty("line.separator");
                        stringOutput += appArgs;
                    }
                }
            }
            if (currentOutput.length() > 0) {
                currentOutput = currentOutput.substring(0, currentOutput.length() - 1);
            }
            if (subcat.length() > 0) {
                subcat = subcat.substring(0, subcat.length() - 1);
            }
            if (stringOutput.length() > 0) {
                stringOutput = stringOutput.substring(0, stringOutput.length() - 1);
            }
            if (!(outputFileName.length() == 0)) {
                if (exec_pipe) {
                    stringOutput = currentOutput;
                }
                String outputPath = currentDirectory + File.separator + outputFileName;
                File outputFile = new File(outputPath);
                outputFile.getParentFile().mkdirs();
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile();
                    } catch (Exception e) {
                        if (unsafe) {
                            return "";
                        } else if (inseq) {
                            seqErr = true;
                            return "";
                        }
                        throw new RuntimeException("cat : failed to create the output file!");
                    }
                }
                try {
                    FileWriter fr = new FileWriter(outputFile);
                    BufferedWriter br = new BufferedWriter(fr);
                    br.write(stringOutput);
                    br.close();
                    fr.close();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("cat : failed to create the output file!");
                }

            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subcat = Escape.add(subcat);

        return subcat;
    }
}

class echo extends application {

    @Override
    public void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        boolean atLeastOnePrinted = false;
        for (String arg : appArgs) {
            if (exec_pipe) {
                if (arg == appArgs.get(appArgs.size() - 1)) {
                    currentOutput += arg;
                } else {
                    currentOutput += arg + " ";
                }
            } else {
                if (!(outputFileName.length() > 0)) {
                    writer.write(arg);
                }
                stringOutput += arg;
                if (arg != appArgs.get(appArgs.size() - 1)) {
                    writer.write(" ");
                    stringOutput += " ";
                } else {
                    if (inputFileName.length() > 0) {
                        writer.write(" ");
                        stringOutput += " ";
                    }
                }

            }
            writer.flush();
            if (!(outputFileName.length() > 0)) {
                atLeastOnePrinted = true;
            }
        }

        if (!(inputFileName.length() == 0)) {
            Charset encoding = StandardCharsets.UTF_8;
            File currFile = new File(currentDirectory + File.separator + inputFileName);
            if (currFile.exists()) {
                Path filePath = Paths.get(currentDirectory + File.separator + inputFileName);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                    String line = null;
                    String writerOutput = "";
                    while ((line = reader.readLine()) != null) {
                        if (exec_pipe) {
                            currentOutput += line + System.getProperty("line.separator");
                        } else {
                            if (outputFileName.length() == 0) {
                                writerOutput += line + System.getProperty("line.separator");
                            }
                            stringOutput += line + System.getProperty("line.separator");
                            atLeastOnePrinted = true;
                        }
                    }
                    if (writerOutput.length() > 0) {
                        writer.write(writerOutput.substring(0, writerOutput.length() - 1));
                    }
                    writer.flush();
                    if (stringOutput.length() > 0) {
                        stringOutput = stringOutput.substring(0, stringOutput.length() - 1);
                    }
                    if (currentOutput.length() > 0) {
                        currentOutput = currentOutput.substring(0, currentOutput.length() - 1);
                    }
                } catch (IOException e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("echo: cannot open " + inputFileName);
                }
            } else {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("echo: file does not exist");
            }
        }

        if (!(outputFileName.length() == 0)) {
            if (exec_pipe) {
                stringOutput = currentOutput;
            }
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("echo : failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("echo : failed to create the output file!");
            }

        }

        if (atLeastOnePrinted && !exec_pipe) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

    }

    @Override
    public String execsub(ArrayList<String> appArgs, String inputFileName, String outputFileName) throws IOException {
        String subecho = "";
        String stringOutput = "";
        currentOutput = "";
        int i = 0;
        for (String arg : appArgs) {
            if (exec_pipe) {
                if (i < appArgs.size() - 1) {
                    currentOutput += arg + " ";
                } else {
                    currentOutput += arg;
                }
                i++;
            } else {
                if (i < appArgs.size() - 1) {
                    subecho += arg + " ";
                    stringOutput += arg + " ";
                } else {
                    if (inputFileName.length() > 0) {
                        subecho += arg + " ";
                        stringOutput += " ";
                    } else {
                        subecho += arg;
                        stringOutput += arg;
                    }
                }
                i++;
            }
        }

        if (!(inputFileName.length() == 0)) {
            Charset encoding = StandardCharsets.UTF_8;
            File currFile = new File(currentDirectory + File.separator + inputFileName);
            if (currFile.exists()) {
                Path filePath = Paths.get(currentDirectory + File.separator + inputFileName);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (exec_pipe) {
                            currentOutput += line + System.getProperty("line.separator");
                        } else {
                            subecho += line + System.getProperty("line.separator");
                            stringOutput += line + System.getProperty("line.separator");
                        }
                    }
                    if (currentOutput.length() > 0) {
                        currentOutput = currentOutput.substring(0, currentOutput.length() - 1);
                    }
                    if (subecho.length() > 0) {
                        subecho = subecho.substring(0, subecho.length() - 1);
                    }
                    if (stringOutput.length() > 0) {
                        stringOutput = stringOutput.substring(0, stringOutput.length() - 1);
                    }
                } catch (IOException e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("echo: cannot open " + inputFileName);
                }
            } else {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("echo: file does not exist");
            }
        }

        if (!(outputFileName.length() == 0)) {
            if (exec_pipe) {
                stringOutput = currentOutput;
            }
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("echo : failed to create the output file!");
                }
            }

            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("echo : failed to create the output file!");
            }
            subecho = "";
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subecho = Escape.add(subecho);

        return subecho;
    }

}

class head extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("head: missing appArgsuments");
        }
        if (AppArgs.size() != 1 && AppArgs.size() != 3) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("head: wrong appArgsuments");
        }
        if (AppArgs.size() == 3 && !AppArgs.get(0).equals("-n")) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("head: wrong appArgsument " + AppArgs.get(0));
        }
        int headLines = 10;
        String headappArgs;
        if (AppArgs.size() == 3) {
            try {
                headLines = Integer.parseInt(AppArgs.get(1));
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("head: wrong appArgsument " + AppArgs.get(1));
            }
            headappArgs = AppArgs.get(2);
        } else {
            headappArgs = AppArgs.get(0);
        }
        File headFile = new File(currentDirectory + File.separator + headappArgs);
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + headappArgs);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        if (outputFileName.length() > 0) {
                            stringOutput += line + System.getProperty("line.separator");
                        }
                        if (exec_pipe) {
                            currentOutput = currentOutput + line + System.getProperty("line.separator");
                        } else {
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                        }
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("head: cannot open " + headappArgs);
            }
        } else {
            String[] lines = headappArgs.split(System.getProperty("line.separator"));
            for (int i = 0; i < headLines && i < lines.length; i++) {
                if (outputFileName.length() > 0) {
                    stringOutput += lines[i] + System.getProperty("line.separator");
                }
                if (exec_pipe) {
                    currentOutput = currentOutput + lines[i] + System.getProperty("line.separator");
                } else {
                    writer.write(lines[i]);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("head : failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("head : failed to create the output file!");
            }
        }

    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName) throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        currentOutput = "";
        String subhead = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("head: missing appArgsuments");
        }
        if (AppArgs.size() != 1 && AppArgs.size() != 3) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("head: wrong appArgsuments");
        }
        if (AppArgs.size() == 3 && !AppArgs.get(0).equals("-n")) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("head: wrong appArgsument " + AppArgs.get(0));
        }
        int headLines = 10;
        String headappArgs;
        if (AppArgs.size() == 3) {
            try {
                headLines = Integer.parseInt(AppArgs.get(1));
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("head: wrong appArgsument " + AppArgs.get(1));
            }
            headappArgs = AppArgs.get(2);
        } else {
            headappArgs = AppArgs.get(0);
        }
        File headFile = new File(currentDirectory + File.separator + headappArgs);
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + headappArgs);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        if (outputFileName.length() > 0) {
                            stringOutput += line + System.getProperty("line.separator");
                        }
                        if (exec_pipe) {
                            currentOutput = currentOutput + line + System.getProperty("line.separator");
                        } else {
                            subhead += line + System.getProperty("line.separator");
                        }

                    }
                }
            } catch (IOException e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("head: cannot open " + headappArgs);
            }
        } else {
            String[] lines = headappArgs.split(System.getProperty("line.separator"));
            for (int i = 0; i < headLines && i < lines.length; i++) {
                if (outputFileName.length() > 0) {
                    stringOutput += lines[i] + System.getProperty("line.separator");
                }
                if (exec_pipe) {
                    currentOutput = currentOutput + lines[i] + System.getProperty("line.separator");
                } else {
                    subhead += lines[i] + System.getProperty("line.separator");
                }
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subhead = Escape.add(subhead);

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("head: failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("head: failed to create the output file!");
            }
        }

        return subhead;
    }

}

class tail extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (inputFileName.length() != 0) {
            AppArgs.add(inputFileName);
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("tail: missing appArgsuments");
        }
        if (AppArgs.size() != 1 && AppArgs.size() != 3) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("tail: wrong appArgsuments");
        }
        if (AppArgs.size() == 3 && !AppArgs.get(0).equals("-n")) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("tail: wrong appArgsument " + AppArgs.get(0));
        }
        int tailLines = 10;
        String tailappArgs;
        if (AppArgs.size() == 3) {
            try {
                tailLines = Integer.parseInt(AppArgs.get(1));
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("tail: wrong appArgsument " + AppArgs.get(1));
            }
            tailappArgs = AppArgs.get(2);
        } else {
            tailappArgs = AppArgs.get(0);
        }
        File tailFile = new File(currentDirectory + File.separator + tailappArgs);
        if (tailFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + tailappArgs);
            ArrayList<String> storage = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);
                }
                int index = 0;
                if (tailLines > storage.size()) {
                    index = 0;
                } else {
                    index = storage.size() - tailLines;
                }
                for (int i = index; i < storage.size(); i++) {
                    if (outputFileName.length() > 0) {
                        stringOutput += storage.get(i) + System.getProperty("line.separator");
                    }
                    if (exec_pipe) {
                        currentOutput = currentOutput + storage.get(i) + System.getProperty("line.separator");
                    } else {
                        writer.write(storage.get(i) + System.getProperty("line.separator"));
                    }
                    writer.flush();
                }
            } catch (IOException e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("tail: cannot open " + tailappArgs);
            }
        } else {
            String[] lines = tailappArgs.split(System.getProperty("line.separator"));
            for (int i = lines.length - tailLines; i < lines.length; i++) {
                if (i < 0) {
                    continue;
                }
                if (outputFileName.length() > 0) {
                    stringOutput += lines[i] + System.getProperty("line.separator");
                }
                if (exec_pipe) {
                    currentOutput = currentOutput + lines[i] + System.getProperty("line.separator");
                } else {
                    writer.write(lines[i]);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("tail: failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("tail: failed to create the output file!");
            }
        }
    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        currentOutput = "";
        String subtail = "";
        String stringOutput = "";
        if (AppArgs.isEmpty()) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("tail: missing appArgsuments");
        }
        if (AppArgs.size() != 1 && AppArgs.size() != 3) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("tail: wrong appArgsuments");
        }
        if (AppArgs.size() == 3 && !AppArgs.get(0).equals("-n")) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                throw new RuntimeException("");
            }
            throw new RuntimeException("tail: wrong appArgsument " + AppArgs.get(0));
        }
        int tailLines = 10;
        String tailappArgs;
        if (AppArgs.size() == 3) {
            try {
                tailLines = Integer.parseInt(AppArgs.get(1));
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("tail: wrong appArgsument " + AppArgs.get(1));
            }
            tailappArgs = AppArgs.get(2);
        } else {
            tailappArgs = AppArgs.get(0);
        }
        File tailFile = new File(currentDirectory + File.separator + tailappArgs);
        if (tailFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + tailappArgs);
            ArrayList<String> storage = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);
                }
                int index = 0;
                if (tailLines > storage.size()) {
                    index = 0;
                } else {
                    index = storage.size() - tailLines;
                }
                for (int i = index; i < storage.size(); i++) {
                    if (outputFileName.length() > 0) {
                        stringOutput += storage.get(i) + System.getProperty("line.separator");
                    }
                    if (exec_pipe) {
                        currentOutput = currentOutput + storage.get(i) + System.getProperty("line.separator");
                    } else {
                        subtail += storage.get(i) + System.getProperty("line.separator");

                    }

                }
            } catch (IOException e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("tail: cannot open " + tailappArgs);
            }
        } else {
            String[] lines = tailappArgs.split(System.getProperty("line.separator"));
            for (int i = lines.length - tailLines; i < lines.length; i++) {
                if (i < 0) {
                    continue;
                }
                if (outputFileName.length() > 0) {
                    stringOutput += lines[i] + System.getProperty("line.separator");
                }
                if (exec_pipe) {
                    currentOutput = currentOutput + lines[i] + System.getProperty("line.separator");
                } else {
                    subtail += lines[i] + System.getProperty("line.separator");
                }
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subtail = Escape.add(subtail);

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("tail: failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("tail: failed to create the output file!");
            }
        }

        return subtail;
    }

}

class grep extends application {

    @Override
    public void exec(ArrayList<String> AppArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        OutputStreamWriter writer = new OutputStreamWriter(output);
        currentOutput = "";
        String stringOutput = "";
        if (AppArgs.size() < 2) {
            if (unsafe) {
                return;
            } else if (inseq) {
                seqErr = true;
                return;
            }
            throw new RuntimeException("grep: wrong number of appArgsuments");
        }
        Pattern grepPattern = Pattern.compile(AppArgs.get(0));
        int numOfFiles = AppArgs.size() - 1;
        Path filePath;
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(currentDirectory);
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(AppArgs.get(i + 1));
            filePathArray[i] = filePath;
        }
        for (int j = 0; j < filePathArray.length; j++) {
            if (Files.notExists(filePathArray[j]) || Files.isDirectory(filePathArray[j])
                    || !Files.exists(filePathArray[j])
                    || !Files.isReadable(filePathArray[j])) {
                String[] lines = AppArgs.get(j + 1).split(System.getProperty("line.separator"));
                for (String line : lines) {
                    java.util.regex.Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        if (exec_pipe) {
                            currentOutput += line + System.getProperty("line.separator");
                        } else {
                            if (numOfFiles > 1) {
                                stringOutput += AppArgs.get(j + 1);
                                stringOutput += ":";
                                writer.write(AppArgs.get(j + 1));
                                writer.write(":");
                            }
                            stringOutput += line + System.getProperty("line.separator");
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                        }
                        writer.flush();
                    }
                }
                continue;
            } else {
                Charset encoding = StandardCharsets.UTF_8;
                try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {

                        java.util.regex.Matcher matcher = grepPattern.matcher(line);
                        if (matcher.find()) {
                            if (exec_pipe) {
                                currentOutput = currentOutput + line + System.getProperty("line.separator");
                            } else {
                                if (numOfFiles > 1) {
                                    stringOutput += AppArgs.get(j + 1);
                                    stringOutput += ":";
                                    writer.write(AppArgs.get(j + 1));
                                    writer.write(":");
                                }

                                stringOutput += line + System.getProperty("line.separator");
                                writer.write(line);
                                writer.write(System.getProperty("line.separator"));
                            }
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("grep: cannot open " + AppArgs.get(j + 1));
                }
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;

            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("grep: failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return;
                } else if (inseq) {
                    seqErr = true;
                    return;
                }
                throw new RuntimeException("grep: failed to create the output file!");
            }
        }
    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            AppArgs.add(inputFileName);
        }
        String subgrep = "";
        currentOutput = "";
        String stringOutput = "";
        if (AppArgs.size() < 2) {
            if (unsafe) {
                return "";
            } else if (inseq) {
                seqErr = true;
                return "";
            }
            throw new RuntimeException("grep: wrong number of appArgsuments");
        }
        Pattern grepPattern = Pattern.compile(AppArgs.get(0));
        int numOfFiles = AppArgs.size() - 1;
        Path filePath;
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(currentDirectory);
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(AppArgs.get(i + 1));
            filePathArray[i] = filePath;
        }
        for (int j = 0; j < filePathArray.length; j++) {
            if (Files.notExists(filePathArray[j]) || Files.isDirectory(filePathArray[j])
                    || !Files.exists(filePathArray[j])
                    || !Files.isReadable(filePathArray[j])) {
                String[] lines = AppArgs.get(j + 1).split(System.getProperty("line.separator"));
                for (String line : lines) {
                    java.util.regex.Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        if (exec_pipe) {
                            currentOutput = currentOutput + line + System.getProperty("line.separator");
                        } else {
                            if (numOfFiles > 1) {
                                stringOutput += AppArgs.get(j + 1) + ":";
                                subgrep += AppArgs.get(j + 1) + ":";
                            }
                            stringOutput += line + System.getProperty("line.separator");
                            subgrep += line + " ";
                        }
                    }
                }
                continue;
            }
            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    java.util.regex.Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        if (exec_pipe) {
                            currentOutput = currentOutput + line + System.getProperty("line.separator");
                        } else {
                            if (numOfFiles > 1) {
                                stringOutput += AppArgs.get(j + 1) + ":";
                                subgrep += AppArgs.get(j + 1) + ":";
                            }
                            stringOutput += line + System.getProperty("line.separator");
                            subgrep += line + " ";

                        }

                    }
                }
            } catch (IOException e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("grep: cannot open " + AppArgs.get(j + 1));
            }
        }

        if (exec_pipe) {
            currentOutput = Escape.add(currentOutput);
        }
        subgrep = Escape.add(subgrep);
        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("grep: failed to create the output file!");
                }
            }
            try {
                FileWriter fr = new FileWriter(outputFile);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(stringOutput);
                br.close();
                fr.close();
            } catch (Exception e) {
                if (unsafe) {
                    return "";
                } else if (inseq) {
                    seqErr = true;
                    return "";
                }
                throw new RuntimeException("grep: failed to create the output file!");
            }
        }

        return subgrep;
    }
}

class find extends application {

    @Override
    public void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            appArgs.add(inputFileName);
        }
        currentOutput = "";
        String fileoutput = "";

        OutputStreamWriter writer = new OutputStreamWriter(output);

        for (int i = 0; i < appArgs.size(); i++) {
            if (!exec_pipe) {
                writer.write(appArgs.get(i));
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            } else {
                currentOutput += appArgs.get(i) + System.getProperty("line.separator");
            }
            fileoutput += appArgs.get(i) + System.getProperty("line.separator");
        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("find : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }
    }

    @Override
    public String execsub(ArrayList<String> appArgs, String inputFileName, String outputFileName)
            throws IOException {
        if (!(inputFileName.length() == 0)) {
            appArgs.add(inputFileName);
        }
        currentOutput = "";
        String subfind = "";
        String fileoutput = "";
        for (int i = 0; i < appArgs.size(); i++) {
            if (!exec_pipe) {
                subfind += appArgs.get(i) + " ";
            } else {
                currentOutput += appArgs.get(i) + " ";
            }
            fileoutput += appArgs.get(i) + System.getProperty("line.separator");
        }

        subfind = Escape.add(subfind);

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return "";
                    } else if (inseq) {
                        seqErr = true;
                        return "";
                    }
                    throw new RuntimeException("find : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }

        return subfind;
    }

}

class sort extends application {

    @Override
    public void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        OutputStreamWriter writertostream = new OutputStreamWriter(output);
        currentOutput = "";
        String fileoutput = "";
        BufferedReader reader = null;
        boolean frompipe = false;
        BufferedWriter writer = null;
        Boolean reverse_order = false;
        if (appArgs.size() > 0 && appArgs.get(0).equals("-r")) {
            reverse_order = true;
            appArgs.remove(0);
        }
        if (inputFileName.length() > 0) {
            appArgs.add(inputFileName);
        }
        for (String sorted_file : appArgs) {

            ArrayList<String> lines = new ArrayList<String>();

            try {

                reader = new BufferedReader(new FileReader(currentDirectory + File.separator + sorted_file)); // 输入文件路径放这儿

                String currentLine = reader.readLine();

                while (currentLine != null) {
                    lines.add(currentLine);

                    currentLine = reader.readLine();
                }

                if (reverse_order == true) {
                    Collections.sort(lines);
                    Collections.reverse(lines);
                }

                else {
                    Collections.sort(lines);

                }

                writer = new BufferedWriter(new FileWriter(currentDirectory + File.separator + sorted_file)); // 输出文件，也可以直接键盘输出，我懒得改了
                                                                                                              // -wn

                for (String line : lines) {
                    if (exec_pipe) {

                        currentOutput += line + System.getProperty("line.separator");
                    } else {
                        writertostream.write(line);
                        writertostream.write(System.getProperty("line.separator"));
                        writertostream.flush();
                    }
                    fileoutput = line + System.getProperty("line.separator");

                    writer.write(line);

                    writer.newLine();

                }

                try {
                    if (reader != null) {
                        reader.close();
                    }

                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                frompipe = true;
                break;
            }

        }
        if (frompipe == true) {
            ArrayList<String> form_pipe = new ArrayList<>();
            String[] lines = appArgs.get(0).split(System.getProperty("line.separator"));
            for (String line : lines) {
                form_pipe.add(line);

            }

            if (reverse_order == true) {
                Collections.sort(form_pipe);
                Collections.reverse(form_pipe);

            } else {
                Collections.sort(form_pipe);

            }

            for (String pipeappArgs : form_pipe) {
                if (exec_pipe == true) {
                    currentOutput += pipeappArgs + System.getProperty("line.separator");

                }

                else {

                    writertostream.write(pipeappArgs);

                    writertostream.write(System.getProperty("line.separator"));
                    writertostream.flush();
                }
                fileoutput += pipeappArgs + System.getProperty("line.separator");
            }

        }
        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("sort : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }
    }

    @Override
    public String execsub(ArrayList<String> appArgs, String inputFileName, String outputFileName)
            throws IOException {
        String subsort = "";
        currentOutput = "";
        String fileoutput = "";
        BufferedReader reader = null;
        boolean frompipe = false;
        BufferedWriter writer = null;
        Boolean reverse_order = false;
        if (appArgs.size() > 0 && appArgs.get(0).equals("-r")) {
            reverse_order = true;
            appArgs.remove(0);
        }
        if (inputFileName.length() > 0) {
            appArgs.add(inputFileName);
        }
        for (String sorted_file : appArgs) {

            ArrayList<String> lines = new ArrayList<String>();

            try {
                reader = new BufferedReader(new FileReader(currentDirectory + File.separator + sorted_file)); // 输入文件路径放这儿

                String currentLine = reader.readLine();

                while (currentLine != null) {
                    lines.add(currentLine);

                    currentLine = reader.readLine();
                }

                if (reverse_order == true) {
                    Collections.sort(lines);
                    Collections.reverse(lines);
                }

                else {
                    Collections.sort(lines);

                }

                writer = new BufferedWriter(new FileWriter(currentDirectory + File.separator + sorted_file)); // 输出文件，也可以直接键盘输出，我懒得改了
                                                                                                              // -wn

                for (String line : lines) {
                    if (exec_pipe) {

                        currentOutput += Escape.add(line) + System.getProperty("line.separator");
                    } else {
                        subsort += line + " ";
                    }
                    fileoutput = line + System.getProperty("line.separator");

                    writer.write(line);

                    writer.newLine();

                }

                try {
                    if (reader != null) {
                        reader.close();
                    }

                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                frompipe = true;
                break;
            }

        }
        if (frompipe == true) {
            ArrayList<String> form_pipe = new ArrayList<>();
            String[] lines = appArgs.get(0).split(System.getProperty("line.separator"));
            for (String line : lines) {
                form_pipe.add(line);
            }
            if (reverse_order == true) {
                Collections.sort(form_pipe);
                Collections.reverse(form_pipe);

            } else {
                Collections.sort(form_pipe);

            }

            for (String pipeappArgs : form_pipe) {
                if (exec_pipe == true) {
                    currentOutput += Escape.add(pipeappArgs) + System.getProperty("line.separator");

                }

                else {
                    subsort += pipeappArgs + " ";

                }
                fileoutput += pipeappArgs + System.getProperty("line.separator");
            }

        }
        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return null;
                    } else if (inseq) {
                        seqErr = true;
                        return null;
                    }
                    throw new RuntimeException("sort : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }
        return Escape.add(subsort);
    }

}

class uniq extends application {

    @Override
    public void exec(ArrayList<String> appArgs, OutputStream output, String inputFileName, String outputFileName)
            throws IOException {
        currentOutput = "";
        Boolean ignoreCase = false;
        boolean frompipe = false;
        String fileoutput = "";
        OutputStreamWriter writertostream = new OutputStreamWriter(output);
        if (inputFileName.length() > 0) {
            appArgs.add(inputFileName);
        }
        if (appArgs.size() > 0 && appArgs.get(0).equals("-i")) {
            ignoreCase = true;
            appArgs.remove(0);
        }
        for (int i = 0; i < appArgs.size(); i++) {

            ArrayList<String> lines = new ArrayList<>();
            try {

                BufferedReader reader = new BufferedReader(
                        new FileReader(currentDirectory + File.separator + appArgs.get(i)));

                String currentLine = reader.readLine();

                while (currentLine != null) {
                    lines.add(currentLine);

                    currentLine = reader.readLine();
                }
                ArrayList<String> uniqlines = new ArrayList<>();

                if (ignoreCase == true) {
                    uniqlines.add(lines.get(0));
                    int v = 0;
                    while (v < lines.size() - 1) {
                        if (lines.get(v).compareToIgnoreCase(lines.get(v + 1)) != 0) {
                            uniqlines.add(lines.get(v + 1));

                        }
                        v++;
                    }
                } else {
                    int v = 0;
                    uniqlines.add(lines.get(0));
                    while (v < lines.size() - 1) {
                        if (lines.get(v).compareTo(lines.get(v + 1)) != 0) {
                            uniqlines.add(lines.get(v + 1));

                        }
                        v++;
                    }

                }

                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(currentDirectory + File.separator + appArgs.get(i)));

                for (String line : uniqlines) {
                    if (exec_pipe) {
                        currentOutput += line + System.getProperty("line.separator");
                    } else {
                        writertostream.write(line);
                        writertostream.write(System.getProperty("line.separator"));
                        writertostream.flush();

                    }
                    fileoutput += line + System.getProperty("line.separator");

                    writer.write(line);

                    writer.newLine();
                }
                reader.close();

                writer.close();

            } catch (IOException e) {
                frompipe = true;
                break;
            }

        }

        if (frompipe == true) {
            ArrayList<String> form_pipe = new ArrayList<>();
            String[] lines = appArgs.get(0).split(System.getProperty("line.separator"));
            for (String line : lines) {
                form_pipe.add(line);

            }
            ArrayList<String> uniqlines = new ArrayList<>();
            if (ignoreCase == true) {
                uniqlines.add(form_pipe.get(0));
                int v = 0;
                while (v < form_pipe.size() - 1) {
                    if (form_pipe.get(v).compareToIgnoreCase(form_pipe.get(v + 1)) != 0) {
                        uniqlines.add(form_pipe.get(v + 1));

                    }
                    v++;
                }
            } else {
                int v = 0;
                uniqlines.add(form_pipe.get(0));
                while (v < form_pipe.size() - 1) {
                    if (form_pipe.get(v).compareTo(form_pipe.get(v + 1)) != 0) {
                        uniqlines.add(form_pipe.get(v + 1));

                    }
                    v++;
                }

            }

            for (String pipeappArgs : uniqlines) {
                if (exec_pipe == true) {
                    currentOutput += pipeappArgs + System.getProperty("line.separator");
                }

                else {

                    writertostream.write(pipeappArgs);
                    writertostream.write(System.getProperty("line.separator"));
                    writertostream.flush();
                }
                fileoutput += pipeappArgs + System.getProperty("line.separator");
            }

        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return;
                    } else if (inseq) {
                        seqErr = true;
                        return;
                    }
                    throw new RuntimeException("uniq : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }

    }

    @Override
    public String execsub(ArrayList<String> AppArgs, String inputFileName, String outputFileName)
            throws IOException {
        currentOutput = "";
        Boolean ignoreCase = false;
        boolean frompipe = false;
        String fileoutput = "";
        String subuniq = "";
        if (inputFileName.length() > 0) {
            AppArgs.add(inputFileName);
        }
        if (AppArgs.size() > 0 && AppArgs.get(0).equals("-i")) {
            ignoreCase = true;
            AppArgs.remove(0);
        }
        for (int i = 0; i < AppArgs.size(); i++) {

            ArrayList<String> lines = new ArrayList<>();
            try {

                BufferedReader reader = new BufferedReader(
                        new FileReader(currentDirectory + File.separator + AppArgs.get(i)));

                String currentLine = reader.readLine();

                while (currentLine != null) {
                    lines.add(currentLine);

                    currentLine = reader.readLine();
                }
                ArrayList<String> uniqlines = new ArrayList<>();

                if (ignoreCase == true) {
                    uniqlines.add(lines.get(0));
                    int v = 0;
                    while (v < lines.size() - 1) {
                        if (lines.get(v).compareToIgnoreCase(lines.get(v + 1)) != 0) {
                            uniqlines.add(lines.get(v + 1));

                        }
                        v++;
                    }
                } else {
                    int v = 0;
                    uniqlines.add(lines.get(0));
                    while (v < lines.size() - 1) {
                        if (lines.get(v).compareTo(lines.get(v + 1)) != 0) {
                            uniqlines.add(lines.get(v + 1));

                        }
                        v++;
                    }

                }

                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(currentDirectory + File.separator + AppArgs.get(i)));

                for (String line : uniqlines) {
                    if (exec_pipe) {
                        currentOutput += Escape.add(line) + System.getProperty("line.separator");
                    } else {
                        subuniq += Escape.add(line) + " ";
                    }
                    fileoutput += line + System.getProperty("line.separator");

                    writer.write(line);

                    writer.newLine();
                }
                reader.close();

                writer.close();

            } catch (IOException e) {
                frompipe = true;
                break;
            }

        }

        if (frompipe == true) {
            // System.out.println(appArgs.get(0));
            ArrayList<String> form_pipe = new ArrayList<>();
            String[] lines = AppArgs.get(0).split(System.getProperty("line.separator"));
            for (String line : lines) {

                form_pipe.add(line);

            }
            ArrayList<String> uniqlines = new ArrayList<>();
            if (ignoreCase == true) {
                uniqlines.add(form_pipe.get(0));
                int v = 0;
                while (v < form_pipe.size() - 1) {
                    if (form_pipe.get(v).compareToIgnoreCase(form_pipe.get(v + 1)) != 0) {
                        uniqlines.add(form_pipe.get(v + 1));

                    }
                    v++;
                }
            } else {
                int v = 0;
                uniqlines.add(form_pipe.get(0));
                while (v < form_pipe.size() - 1) {
                    if (form_pipe.get(v).compareTo(form_pipe.get(v + 1)) != 0) {
                        uniqlines.add(form_pipe.get(v + 1));

                    }
                    v++;
                }

            }

            for (String pipeappArgs : uniqlines) {
                if (exec_pipe == true) {
                    currentOutput += Escape.add(pipeappArgs) + System.getProperty("line.separator");
                }

                else {

                    subuniq += Escape.add(pipeappArgs) + " ";
                }
                fileoutput += pipeappArgs + System.getProperty("line.separator");
            }

        }

        if (outputFileName.length() > 0) {
            String outputPath = currentDirectory + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (Exception e) {
                    if (unsafe) {
                        return null;
                    } else if (inseq) {
                        seqErr = true;
                        return null;
                    }
                    throw new RuntimeException("uniq : failed to create the output file!");
                }
            }
            BufferedWriter writer_to_outfile = new BufferedWriter(new FileWriter(outputFile));
            writer_to_outfile.write(fileoutput);
            writer_to_outfile.close();

        }
        return subuniq;
    }

}