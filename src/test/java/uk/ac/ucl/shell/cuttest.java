package uk.ac.ucl.shell;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class cuttest {
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
        String[] writeinf={"abcdefghijklmn","ABCDEFGHIJKLMN"};
        FileWriter fw=new FileWriter(test1_in_dir1);
        for (String in: writeinf){
            fw.write(in);
            
            fw.write(System.getProperty("line.separator"));
            

        }
        fw.close();
        
        String[] writeinf2={"我","最","帅","aaaaa","AAAAAA","aaaaaa","BBBBBB"};
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
    public void testcut() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b 3  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);

        
        //test find with no globbing pattern applied
        assertEquals("c", scn.nextLine());
        assertEquals("C", scn.nextLine());
        in.close();
        /*
        Shell.eval("cut -b 4-5  Test.dir/test1_dir/test1.txt", out);
        assertEquals("ef", scn.nextLine());
        assertEquals("EF", scn.nextLine());
        scn.nextLine();
        //test with globbing pattern appied
        Shell.eval("cut -b -2  Test.dir/test1_dir/test1.txt", out);
        assertEquals("ab", scn.nextLine());
        assertEquals("AB", scn.nextLine());
        Shell.eval("cut -b 5-  Test.dir/test1_dir/test1.txt", out);
        assertEquals("jklmn", scn.nextLine());
        assertEquals("JKLMN", scn.nextLine());
        
        
        */

        // assertEquals(scn.next(),"ss");
        scn.close();
    }
    @Test
    public void testcut2() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b 4-5  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);
        assertEquals("de", scn.nextLine());
        assertEquals("DE", scn.nextLine());
        in.close();
        scn.close();

    }
    @Test
    public void testcut3() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b -2  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);
        assertEquals("ab", scn.nextLine());
        assertEquals("AB", scn.nextLine());
        in.close();
        scn.close();

    }
    @Test
    public void testcut4() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b 5-  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);
        assertEquals("efghijklmn", scn.nextLine());
        assertEquals("EFGHIJKLMN", scn.nextLine());
        in.close();
        scn.close();
    }
    @Test
    public void testcut5_mix() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b -2,3,4-5,7-  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);
        assertEquals("abcdeghijklmn", scn.nextLine());
        assertEquals("ABCDEGHIJKLMN", scn.nextLine());
        in.close();
        scn.close();

    }
    //some error input in cut parameter
    @Test
    public void testcut6_someerro1() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b 1,-2,4-5,7-  Test.dir/test1_dir/test1.txt", out);//-2 not in the first parameter can only 
                                                                            //recognize the input before (overlap error)
        
        
        Scanner scn = new Scanner(in);
        assertEquals("a", scn.nextLine().trim());
        assertEquals("A", scn.nextLine().trim());
        in.close();
        scn.close();
    }
    @Test
    public void testcut7_someerro2() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b -3,5,7-,4-5,  Test.dir/test1_dir/test1.txt", out);//7- not in the last cut parameter can only 
                                                                            //recognize the input including and before the toend-cut(overlap error)
        
        
        Scanner scn = new Scanner(in);
        assertEquals("abceghijklmn", scn.nextLine().trim());
        assertEquals("ABCEGHIJKLMN", scn.nextLine().trim());
        in.close();
        scn.close();
    }
    @Test
    public void testcut8_someerro3() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cut -b 2,5-7,6-8,4-5,  Test.dir/test1_dir/test1.txt", out);//5-7,6-8 get overlapped can only 
                                                                            //recognize the last valid input  (overlap error)
        
        
        Scanner scn = new Scanner(in);
        assertEquals("befg", scn.nextLine().trim());
        assertEquals("BEFG", scn.nextLine().trim());
        in.close();
        scn.close();

    }
    @Test
    public void testcut9_stdin() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("cat  Test.dir/test1_dir/test1.txt|cut -b 2-3,5,7", out);// cut get tested with stdin functionality
                                                                            //
        
        
        Scanner scn = new Scanner(in);
        assertEquals("bceg", scn.nextLine().trim());
        assertEquals("BCEG", scn.nextLine().trim());
        in.close();
        scn.close();
    }
    
    @Test
    public void testcut10_pipe() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        
        
        Scanner scn = new Scanner(in);
        Shell.eval("echo abc | cut -b 1", out);
        assertEquals("a", scn.nextLine().trim());
        scn.close();
    }
    @Test
    public void testcut10_backquote() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo `cut -b 2-3,5,7 Test.dir/test1_dir/test1.txt`", out);//code substitution get tested in cut
        
        
        Scanner scn = new Scanner(in);
        assertEquals("bc e g BC E G", scn.nextLine().trim());
        scn.close();
    }
    @Test
    public void testcut11() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("<Test.dir/test1_dir/test1.txt cut -b 2-3,5,7 >Test.dir/test1_dir/test2.txt", out);
        //input and output redirection get test in this case
        
        Scanner scn = new Scanner(in);
        assertEquals("bceg", scn.nextLine().trim());
        scn.close();
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