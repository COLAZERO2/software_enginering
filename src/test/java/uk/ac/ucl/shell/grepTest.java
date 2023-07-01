package uk.ac.ucl.shell;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class grepTest {
    @Before
    public void create_tested_file() throws IOException {
        String current_dir = application.currentDirectory;
        File test_dir1 = new File(current_dir + File.separator + "Test.dir");
        test_dir1.mkdirs();
        File test1_in_dir1 = new File(test_dir1.getAbsolutePath() + File.separator + "test1.txt");
        test1_in_dir1.createNewFile();
        File test2_in_dir1 = new File(test_dir1.getAbsolutePath() + File.separator + "test2.txt");
        test2_in_dir1.createNewFile();
        File test3_in_dir1 = new File(test_dir1.getAbsolutePath() + File.separator + "test3.txt");
        test3_in_dir1.createNewFile();
        FileWriter fw1 = new FileWriter(test1_in_dir1);
        FileWriter fw2 = new FileWriter(test2_in_dir1);
        FileWriter fw3 = new FileWriter(test3_in_dir1);
        String str1 = "(foo)" + System.getProperty("line.separator") + "doo";
        String str2 = "\"doo\"" + System.getProperty("line.separator") + "foo";
        String str3 = "abcd" + System.getProperty("line.separator") + "(abcd)";
        fw1.write(str1);
        fw2.write(str2);
        fw3.write(str3);
        fw1.close();
        fw2.close();
        fw3.close();

    }

    @Test
    public void test1() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("grep foo Test.dir/test1.txt > result1.txt", out);
        Shell.eval("grep '.oo' Test.dir/test1.txt Test.dir/test2.txt Test.dir/test3.txt > result2.txt", out);
        Shell.eval("grep 'a..d' Test.dir/test1.txt Test.dir/test2.txt Test.dir/test3.txt > result3.txt", out);

        File file = new File(application.currentDirectory + File.separator + "result1.txt");
        assertEquals(true, file.exists());
        FileReader fr1 = new FileReader(file);
        String str1 = "";
        String str2 = "(foo)";
        BufferedReader br1 = new BufferedReader(fr1);
        String line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        br1.close();
        fr1.close();
        assertEquals(str2, str1);
        file.delete();

        file = new File(application.currentDirectory + File.separator + "result2.txt");
        fr1 = new FileReader(file);
        str1 = "";
        str2 = "Test.dir/test1.txt:(foo)" + System.getProperty("line.separator") + "Test.dir/test1.txt:doo"
                + System.getProperty("line.separator") + "Test.dir/test2.txt:\"doo\""
                + System.getProperty("line.separator")
                + "Test.dir/test2.txt:foo" + System.getProperty("line.separator");
        br1 = new BufferedReader(fr1);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line + System.getProperty("line.separator");
        }
        br1.close();
        fr1.close();
        assertEquals(str2, str1);
        file.delete();

        file = new File(application.currentDirectory + File.separator + "result3.txt");
        fr1 = new FileReader(file);
        str1 = "";
        str2 = "Test.dir/test3.txt:abcd" + System.getProperty("line.separator") + "Test.dir/test3.txt:(abcd)"
                + System.getProperty("line.separator");
        br1 = new BufferedReader(fr1);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line + System.getProperty("line.separator");
        }
        br1.close();
        fr1.close();
        assertEquals(str2, str1);
        file.delete();
    }

    @Test
    public void test2() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("grep grep grep > grep.txt", out);
        Scanner scn = new Scanner(in);
        Shell.eval("_`grep grep grep.txt`; `grep grep grep.txt` `grep grep < grep.txt` < grep.txt", out);
        assertEquals("grep", scn.nextLine());
        Shell.eval("echo `grep grep grep > grep2.txt`", out);
        assertEquals("grep", scn.nextLine());
        Shell.eval("echo `_grep` < grep2.txt", out);
        assertEquals("grep", scn.nextLine());
        File file = new File("grep.txt");
        File file2 = new File("grep2.txt");
        scn.close();
        file.delete();
        file2.delete();
    }

    @After
    public void delete_test_fir() {
        String current_dir = application.currentDirectory;
        File test_dir = new File(current_dir + File.separator + "Test.dir");
        deletefolder(test_dir);

    }

    public void deletefolder(File dirfile) {

        if (dirfile.isFile()) {
            dirfile.delete();
        } else {
            for (File file : dirfile.listFiles()) {
                deletefolder(file);
            }
            dirfile.delete();
        }

        return;
    }

}
