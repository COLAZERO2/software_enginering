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

public class headtailTest {
    @Before
    public void create_tested_file() throws IOException {
        String current_dir = application.currentDirectory;
        File test_dir1 = new File(current_dir + File.separator + "Test.dir");
        test_dir1.mkdirs();
        File test1_in_dir1 = new File(test_dir1.getAbsolutePath() + File.separator + "test1.txt");
        if (!test1_in_dir1.exists()) {
            test1_in_dir1.createNewFile();
        }
        FileWriter fw = new FileWriter(test1_in_dir1);
        for (int i = 0; i < 4; i++) {
            fw.write("head");
            if (i != 11) {
                fw.write(System.getProperty("line.separator"));
            }
        }
        for (int i = 0; i < 4; i++) {
            fw.write("doo");
            if (i != 11) {
                fw.write(System.getProperty("line.separator"));
            }
        }
        for (int i = 0; i < 4; i++) {
            fw.write("tail");
            if (i != 11) {
                fw.write(System.getProperty("line.separator"));
            }
        }
        fw.close();

    }

    @Test
    public void test1() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("head Test.dir/test1.txt > result.txt", out);
        File file = new File("result.txt");
        FileReader fr1 = new FileReader(file);
        String str1 = "";
        String str2 = "headheadheadheaddoodoodoodootailtail";
        BufferedReader br1 = new BufferedReader(fr1);
        String line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        br1.close();
        fr1.close();
        assertEquals(str1, str2);
        file.delete();

        Shell.eval("head -n 5 Test.dir/test1.txt > result.txt | echo > echo.txt", out);
        file = new File("result.txt");
        fr1 = new FileReader(file);
        File echo = new File("echo.txt");
        FileReader fr2 = new FileReader(echo);
        str1 = "";
        str2 = "";
        String str3 = "headheadheadheaddoo";
        br1 = new BufferedReader(fr1);
        BufferedReader br2 = new BufferedReader(fr2);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        while ((line = br2.readLine()) != null) {
            str2 += line;
        }
        br1.close();
        fr1.close();
        br2.close();
        fr2.close();
        assertEquals(str1, str3);
        assertEquals(str2, str3);
        file.delete();
        echo.delete();
        Shell.eval("`head -n 1 Test.dir/test1.txt` -n 15 Test.dir/test1.txt > result.txt", out);
        file = new File("result.txt");
        fr1 = new FileReader(file);
        str1 = "";
        str2 = "headheadheadheaddoodoodoodootailtailtailtail";
        br1 = new BufferedReader(fr1);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        br1.close();
        fr1.close();
        assertEquals(str1, str2);
        file.delete();

        Shell.eval("tail Test.dir/test1.txt > result.txt", out);
        file = new File("result.txt");
        fr1 = new FileReader(file);
        str1 = "";
        str2 = "headheaddoodoodoodootailtailtailtail";
        br1 = new BufferedReader(fr1);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        br1.close();
        fr1.close();
        assertEquals(str1, str2);
        file.delete();

        Shell.eval("tail -n 5 Test.dir/test1.txt > result.txt | echo > echo.txt", out);
        file = new File("result.txt");
        fr1 = new FileReader(file);
        echo = new File("echo.txt");
        fr2 = new FileReader(echo);
        str1 = "";
        str2 = "";
        str3 = "dootailtailtailtail";
        br1 = new BufferedReader(fr1);
        br2 = new BufferedReader(fr2);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        while ((line = br2.readLine()) != null) {
            str2 += line;
        }
        br1.close();
        fr1.close();
        fr2.close();
        br2.close();
        assertEquals(str1, str3);
        assertEquals(str2, str3);
        file.delete();

        Shell.eval("`tail -n 1 Test.dir/test1.txt` -n 15 Test.dir/test1.txt > result.txt", out);
        file = new File("result.txt");
        fr1 = new FileReader(file);
        str1 = "";
        str2 = "headheadheadheaddoodoodoodootailtailtailtail";
        br1 = new BufferedReader(fr1);
        line = null;
        while ((line = br1.readLine()) != null) {
            str1 += line;
        }
        br1.close();
        fr1.close();
        assertEquals(str1, str2);
        file.delete();
    }

    @Test
    public void test2() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn = new Scanner(in);
        Shell.eval("tail -n 1 tail", out);
        assertEquals("tail", scn.nextLine());
        Shell.eval("head -n 1 head", out);
        assertEquals("head", scn.nextLine());
        Shell.eval("echo `tail -n 1 tail`", out);
        assertEquals("tail", scn.nextLine());
        Shell.eval("echo `head -n 1 head`", out);
        assertEquals("head", scn.nextLine());
        Shell.eval("_head -n a b; echo foo", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("_tail -n a b; echo foo", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo `_head a b c | echo foo` `_head a b c`", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo `_tail a b c | echo foo` `_tail a b c`", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("head -n 1 `head -n 1 Test.dir/test1.txt > head.txt`", out);
        assertEquals("head", scn.nextLine());
        Shell.eval("echo < head.txt", out);
        assertEquals("head", scn.nextLine());
        Shell.eval("tail -n 1 `tail -n 1 Test.dir/test1.txt > tail.txt`", out);
        assertEquals("tail", scn.nextLine());
        Shell.eval("echo < tail.txt", out);
        assertEquals("tail", scn.nextLine());
        scn.close();
        try {
            Shell.eval("head", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: missing appArgsuments", thrown);
        }
        try {
            Shell.eval("tail", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: missing appArgsuments", thrown);
        }
        try {
            Shell.eval("head -n 1", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsuments", thrown);
        }
        try {
            Shell.eval("tail -n 1", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: wrong appArgsuments", thrown);
        }
        try {
            Shell.eval("head a 1 b", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsument a", thrown);
        }
        try {
            Shell.eval("tail a 1 b", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: wrong appArgsument a", thrown);
        }
        try {
            Shell.eval("head -n a b", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsument a", thrown);
        }
        try {
            Shell.eval("tail -n a b", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: wrong appArgsument a", thrown);
        }
        
        try {
            Shell.eval("echo `head`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: missing appArgsuments", thrown);
        }
        try {
            Shell.eval("echo `tail`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: missing appArgsuments", thrown);
        }
        try {
            Shell.eval("echo `head -n 1`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsuments", thrown);
        }
        try {
            Shell.eval("echo `tail -n 1`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: wrong appArgsuments", thrown);
        }
        try {
            Shell.eval("echo `head a 1 b`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsument a", thrown);
        }
        try {
            Shell.eval("echo `head -n a b`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("head: wrong appArgsument a", thrown);
        }
        try {
            Shell.eval("echo `tail -n a b`", out);
        } catch (Exception e) {
            String thrown = e.getMessage();
            assertEquals("tail: wrong appArgsument a", thrown);
        }
    }

    @After
    public void delete_test_fir() {
        String current_dir = System.getProperty("user.dir");
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
