package uk.ac.ucl.shell;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Scanner;
import java.io.*;


public class sortTest {
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
        String[] writeinf={"ccc","zzz","AbA"};
        FileWriter fw=new FileWriter(test1_in_dir1);
        for (String in: writeinf){
            fw.write(in);
            
            fw.write(System.getProperty("line.separator"));
            

        }
        fw.close();
        
        String[] writeinf2={"AAa","AAA","AAb"};
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
    public void testsort() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("sort  Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);

        
        //test standard sort
        assertEquals("AbA", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("zzz", scn.nextLine());
        
       
        //test sort in reverse -sorted order
        Shell.eval("sort -r Test.dir/test1_dir/*.txt", out);
        assertEquals("zzz", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("AbA", scn.nextLine());
        scn.nextLine();
        scn.close();
       
        
    }


    @Test
    public void testpipe_sort() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn=new Scanner(in);
        //test stdin in sort
        Shell.eval("cat Test.dir/test1_dir/test1.txt|sort ", out);
        assertEquals("AbA", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("zzz", scn.nextLine());
        
        Shell.eval("cat Test.dir/test1_dir/test1.txt|sort -r ", out);
        assertEquals("zzz", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("AbA", scn.nextLine());
        
        //command substitution in sort
        Shell.eval("echo `cat Test.dir/test1_dir/test1.txt|sort -r` ", out);
        assertEquals("zzz ccc AbA", scn.nextLine());
        //output,input redirection in sort 
        Shell.eval("<Test.dir/test1_dir/test1.txt sort -r >Test.dir/test1_dir/test2.txt", out);
        assertEquals("zzz", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("AbA", scn.nextLine());
        
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
