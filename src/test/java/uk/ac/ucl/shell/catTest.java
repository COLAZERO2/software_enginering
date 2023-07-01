package uk.ac.ucl.shell;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import org.junit.Test;

public class catTest {

    @Test
    public void test() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo foo > 1.txt; echo doo > 2.txt; echo poo > 3.txt", out);
        Scanner scn = new Scanner(in);
        Shell.eval("cat `cat 1.txt > cat1.txt` 2.txt 3.txt > cat2.txt", out);
        assertEquals("foo", scn.nextLine());
        assertEquals("doo", scn.nextLine());
        assertEquals("poo", scn.nextLine());
        Shell.eval("echo < cat1.txt", out);
        assertEquals("foo", scn.nextLine());
        Shell.eval("echo < cat2.txt", out);
        assertEquals("foo", scn.nextLine());
        assertEquals("doo", scn.nextLine());
        assertEquals("poo", scn.nextLine());
        scn.close();
        (new File("1.txt")).delete();
        (new File("2.txt")).delete();
        (new File("3.txt")).delete();
        (new File("cat1.txt")).delete();
        (new File("cat2.txt")).delete();
    }

}