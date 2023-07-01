package uk.ac.ucl.shell;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.io.*;


public class findTest {
    @Before
    public void create_tested_file() throws IOException{
        String current_dir=application.currentDirectory;
        File test_dir1 = new File(current_dir + File.separator + "Test.dir"+File.separator+"test1_dir");
        File test_dir2=new File(current_dir + File.separator + "Test.dir"+File.separator+"test2_dir");
        test_dir1.mkdirs();
        test_dir2.mkdirs();
        File test1_in_dir1=new File(test_dir1.getAbsolutePath()+File.separator+"test1.txt");
        
        if(!test1_in_dir1.exists()){
            test1_in_dir1.createNewFile();
            
        }
        String[] writeinf={"aaa","AAA","aaa"};
        FileWriter fw=new FileWriter(test1_in_dir1);
        for (String in: writeinf){
            fw.write(in);
            fw.write(System.getProperty("line.separator"));

        }
        fw.close();
        
        String[] writeinf2={"我","最"};
        File test1_in_dir2 =new File(test_dir2.getAbsolutePath()+File.separator+"test1.txt");
        if(!test1_in_dir2.exists()){
            test1_in_dir2.createNewFile();
            
        }
        FileWriter fw2=new FileWriter(test1_in_dir2);
        for (String in: writeinf2){
            fw2.write(in);
            
            fw2.write(System.getProperty("line.separator"));
            

        }
        fw2.close();
        


    } 
   
    @Test
    public void testfind() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("find -name test1.txt", out);
        
        Scanner scn = new Scanner(in);
        String test1="./Test.dir/test1_dir/test1.txt";
        String test2="./Test.dir/test2_dir/test1.txt";
        test1=test1.replaceAll("/", Matcher.quoteReplacement(File.separator));
        test2=test2.replaceAll("/", Matcher.quoteReplacement(File.separator));
        //test find with no globbing pattern applied with no start dir
        assertEquals(test1, scn.nextLine());
        assertEquals(test2, scn.nextLine());
        scn.nextLine();
        //test with globbing pattern appied with no start dir
        Shell.eval("find -name *st1.txt", out);
        assertEquals(test1, scn.nextLine());
        assertEquals(test2, scn.nextLine());
        scn.nextLine();
        //test with globbing pattern appied with pipe (stdin)
        Shell.eval("find -name *st1.txt|echo", out);
        assertEquals(test1, scn.nextLine());
        assertEquals(test2, scn.nextLine());
        
        
        scn.nextLine();
        scn.nextLine();
        //test command substitution in find
        Shell.eval("echo `find -name *st1.txt`", out);
        assertEquals(test1+" "+test2, scn.nextLine());
        scn.close();
        

        

        // assertEquals(scn.next(),"ss");
    }


    @Test
    public void testfind_withglobbing() throws Exception {
        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        String cmd = "find " + application.currentDirectory;
        cmd = cmd.replaceAll("\\\\", "/");
        String cmd1 = cmd + "/Test.dir/test1_dir -name *.txt";
        String cmd2 = cmd + "/Test.dir/test2_dir -name *.txt";
        String cmd3 = cmd + "/Test.dir/test1_dir -name test1.txt";
        Shell.eval(cmd1, out);
        
        Scanner scn = new Scanner(in);
        String test1="test1_dir/test1.txt";
        String test2="test2_dir/test1.txt";
        test1=test1.replaceAll("/", Matcher.quoteReplacement(File.separator));
        test2=test2.replaceAll("/", Matcher.quoteReplacement(File.separator));
        //test with specified start dir and globbing applied
        assertEquals(test1, scn.nextLine());
        scn.nextLine();
        
        Shell.eval(cmd2, out);
        assertEquals(test2, scn.nextLine());

        //test with specified start dir but not globbing applied
        scn.nextLine();
        Shell.eval(cmd3, out);
        assertEquals(test1, scn.nextLine());
        scn.close();
        
        
        // assertEquals(scn.next(),"ss");
    }
    
    @Test
    public void testfind_file() throws Exception{
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo foo > foo.txt", out);
        Shell.eval("find -name foo.txt > find1.txt", out);
        Scanner scn = new Scanner(in);
        assertEquals("./foo.txt", scn.nextLine());
        scn.nextLine();
        Shell.eval("echo `find -name foo.txt > find2.txt` > foo.txt", out);
        Shell.eval("echo < find1.txt", out);
        assertEquals("./foo.txt", scn.nextLine());
        scn.nextLine();
        Shell.eval("echo < find2.txt", out);
        assertEquals("./foo.txt", scn.nextLine());
        scn.close();
        (new File("foo.txt")).delete();
        (new File("find1.txt")).delete();
    }

    @After
    public void delete_test_fir(){
        String current_dir=application.currentDirectory;
        File test_dir = new File(current_dir + File.separator + "Test.dir");
        deletefolder(test_dir);
        

    }
    public void deletefolder(File dirfile){
        
        if(dirfile.isFile()){
             dirfile.delete();
        }
        else{
            for(File file:dirfile.listFiles()){
                deletefolder(file);
            }
            dirfile.delete();
        }
    
    return ;
    }

}