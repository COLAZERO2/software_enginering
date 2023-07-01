package uk.ac.ucl.shell;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class ShellTest {

    @Test
    public void testpwd() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn = new Scanner(in);
        Shell.eval("pwd", out);
        assertEquals(System.getProperty("user.dir"), scn.nextLine());
        Shell.eval("pwd > pwd1.txt | echo", out);
        assertEquals(System.getProperty("user.dir"), scn.nextLine());
        Shell.eval("echo `pwd > pwd2.txt | echo`", out);
        assertEquals(System.getProperty("user.dir"), scn.nextLine());
        Shell.eval("echo < pwd1.txt", out);
        assertEquals(System.getProperty("user.dir"), scn.nextLine());
        Shell.eval("echo < pwd2.txt", out);
        assertEquals(System.getProperty("user.dir"), scn.nextLine());
        Shell.eval("_pwd < pwd1.txt; echo foo", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo foo; pwd < pwd1.txt", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo `_pwd < pwd2.txt; echo foo`", out);
        assertEquals("foo", scn.nextLine());
        new File("pwd1.txt").delete();
        new File("pwd2.txt").delete();
        scn.close();
    }

    @Test
    public void testecho() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo a'a'a", out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.nextLine(), "aaa");

        Shell.eval("echo `echo foo`", out);
        assertEquals("foo", scn.nextLine());

        Shell.eval("echo foo > foo.txt", out);
        Shell.eval("echo < foo.txt", out);
        assertEquals("foo", scn.nextLine());

        Shell.eval("echo echo > echo.txt; echo < echo.txt", out);
        assertEquals("echo", scn.nextLine());
        Shell.eval("`echo < echo.txt` foo | echo foo", out);
        assertEquals("foo foo", scn.nextLine());

        Shell.eval("`echo cd` src", out);
        Shell.eval("`echo pwd`", out);
        assertEquals(Shell.currentDirectory, scn.nextLine());

        Shell.eval("echo doo > `echo '\\doo.txt'`", out);
        Shell.eval("echo < `echo 'do\\o.txt'`", out);
        assertEquals("doo", scn.nextLine());

        Shell.eval("echo `echo poo > poo.txt; echo poo | echo` < poo.txt", out);
        assertEquals("poo poo", scn.nextLine());

        Shell.eval("echo \\\"foo\\\\foo\\\" > foo.txt", out);
        Shell.eval("echo < foo.txt", out);
        assertEquals("\"foo\\foo\"", scn.nextLine());

        String toadd = "'`*;|";
        toadd = Escape.add(toadd);
        assertEquals("\\'\\`\\*\\;\\|", toadd);

        File file1 = new File("foo.txt");
        File file2 = new File("echo.txt");
        File file3 = new File("doo.txt");
        File file4 = new File("poo.txt");
        file1.delete();
        file2.delete();
        file3.delete();
        file4.delete();
        scn.close();
    }

    @Test
    public void testcd() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo foo > Test1/Test2/test.txt", out);
        Shell.eval("echo `cd Test1`", out);
        assertEquals(System.getProperty("user.dir") + File.separator + "src" + File.separator + "Test1", Shell.currentDirectory);
        Shell.eval("echo Test2 | cd", out);
        assertEquals(System.getProperty("user.dir") + File.separator + "src" + File.separator + "Test1" + File.separator + "Test2", Shell.currentDirectory);
        Scanner scn = new Scanner(in);
        Shell.eval("echo `_cd foo | echo foo`", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo foo; cd foo", out);
        assertEquals("foo", scn.nextLine());
        scn.close();
        new File("Test1/Test2/test.txt").delete();
        new File("Test1/Test2").delete();
        new File("Test1").delete();
    }

    @Test
    public void testls() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo foo > ls/echo.txt", out);
        Shell.eval("ls ls > ls1.txt | echo", out);
        Scanner scn = new Scanner(in);
        assertEquals("echo.txt", scn.nextLine().trim());
        Shell.eval("echo `ls ls > ls2.txt | echo`", out);
        assertEquals("echo.txt", scn.nextLine().trim());
        Shell.eval("echo < ls1.txt", out);
        assertEquals("echo.txt", scn.nextLine().trim());
        Shell.eval("echo < ls2.txt", out);
        assertEquals("echo.txt", scn.nextLine().trim());
        Shell.eval("_ls aaaa; echo foo", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo foo; ls aaaa", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo `_ls aaaa; echo foo`", out);
        assertEquals("foo", scn.nextLine());
        scn.close();
        (new File("ls/echo.txt")).delete();
        (new File("ls")).delete();
        (new File("ls1.txt")).delete();
        (new File("ls2.txt")).delete();
    }

}
