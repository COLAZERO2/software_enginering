package uk.ac.ucl.shell;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


public class sort_test {
    @Before
    public void create_tested_file() throws IOException{
        String current_dir=System.getProperty("user.dir");
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
            if(in!=writeinf[writeinf.length-1]){
            fw.write(System.getProperty("line.separator"));
            }

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
            if(in!=writeinf2[writeinf.length-1]){
            fw2.write(System.getProperty("line.separator"));
            }

        }
        fw2.close();
        


    }

/** 
    @Test
    public void testShell() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo ss ", out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "ss");
    }

    @Test
    public void testpwd() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn = new Scanner(in);
        Shell.eval("pwd", out);
        
        // 测试pwd,目录地址根据本机可改
        assertEquals(System.getProperty("user.dir"), scn.nextLine());

    }

    // ------cd会改变路径，没法用junit测试------

    @Test
    public void testhead() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn = new Scanner(in);
        // 测试head，读取第一行

        Shell.eval("head -n 1 src\\\\test\\\\java\\\\uk\\\\ac\\\\ucl\\\\shell\\\\headtest.in", out);

        assertEquals("abstract", scn.nextLine());

    }

    @Test
    public void testtail() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试tail，读取最后一行
        Shell.eval("tail -n 1 tailtest.in", out);

        assertEquals("new", out);

    }

    @Test
    public void testecho() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试echo
        Shell.eval("echo abcde", out);

        assertEquals("abcde", out);

    }

    @Test
    public void testcat() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试cat
        Shell.eval("cat cattest.in", out);

        assertEquals("Wenxuan Mei&&Yipeng%%QuXinrui Yang", out);

    }

    @Test
    public void testgrep() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试grep
        Shell.eval("grep java cattest.in", out);

        assertEquals("javais the bestlanguage", out);

    }

    @Test
    public void testcut() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试cut
        Shell.eval("cut -b 1,2,3 cut_test.in", out);

        assertEquals("Xin", out);

        Shell.eval("cut -b -6 cut_test.in", out);

        assertEquals("Xinrui", out);

        Shell.eval("cut -b 1-10 cut_test.in", out);

        assertEquals("XinruiYang", out);

    }
   **/

  
   
    @Test
    public void testsort() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("sort  C:/Users/yangx/Documents/GitHub/comp0010-shell-java-j8/Test.dir/test1_dir/test1.txt", out);
        
        
        Scanner scn = new Scanner(in);

        
        //test find with no globbing pattern applied
        assertEquals("AbA", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("zzz", scn.nextLine());
        
       
        //test with globbing pattern appied
        Shell.eval("sort -r Test.dir/test1_dir/*.txt", out);
        assertEquals("zzz", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("AbA", scn.nextLine());
        scn.nextLine();
        scn.close();
        /*
        Shell.eval("sort -r C:/Users/yangx/Documents/GitHub/comp0010-shell-java-j8/Test.dir/test2_dir/test1.txt", out);
        assertEquals("AAb", scn.nextLine());
        assertEquals("AAa", scn.nextLine());
        assertEquals("AAA", scn.nextLine());
        */
        
    }


    @Test
    public void testpipe_sort() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Scanner scn=new Scanner(in);
        
        Shell.eval("cat Test.dir/test1_dir/test1.txt|sort ", out);
        assertEquals("AbA", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("zzz", scn.nextLine());
        
        Shell.eval("cat Test.dir/test1_dir/test1.txt|sort -r ", out);
        assertEquals("zzz", scn.nextLine());
        assertEquals("ccc", scn.nextLine());
        assertEquals("AbA", scn.nextLine());
        scn.close();
    }
/*
    @Test
    public void testfind_withglobbing() throws Exception {

        // 测试find
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Shell.eval("echo <ssss", out);
        ArrayList<String> output=new ArrayList<>();
        Scanner scn = new Scanner(in);
        //test with specified start dir and globbing applied
        assertEquals("test1_dir\\test1.txt", scn.nextLine());
        scn.nextLine();
        
        Shell.eval("find C:\\\\Users\\\\yangx\\\\Documents\\\\GitHub\\\\comp0010-shell-java-j8\\\\Test.dir\\\\test2_dir -name *.txt", out);
        assertEquals("test2_dir\\test1.txt", scn.nextLine());

        //test with specified start dir but not globbing applied
        scn.nextLine();
        Shell.eval("find C:\\\\Users\\\\yangx\\\\Documents\\\\GitHub\\\\comp0010-shell-java-j8\\\\Test.dir\\\\test1_dir -name test1.txt", out);
        assertEquals("test1_dir\\test1.txt", scn.nextLine());
        
        

        

        // assertEquals(scn.next(),"ss");
    }
    
*/

/** 
    @Test
    public void testuniq() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);

        // 测试uniq
        Shell.eval("uniq uniqtest.in", out);

        assertEquals("abstractbasdd directory", out);

    }

    @Test
    public void testsort() throws Exception {

        PipedInputStream in = new PipedInputStream();
        PipedInputStream ansin = new PipedInputStream();
        PipedOutputStream out;
        PipedOutputStream expected;
        out = new PipedOutputStream(in);
        expected = new PipedOutputStream(ansin);

        // 测试sort, 这是一种比较基本的办法，用生成好的答案直接对比
        // 还有一种方法是逐条对比每行的数据以确认每一行是否一致，我没写

        Shell.eval("sort sorttest.in", out);

        // 这个时候用uniq的原因呢，是因为这个答案文件里面没有重复的元素

        Shell.eval("uniq sorttest_ans.in", expected);

        assertEquals(expected, out);

    }
    **/
    @After
    public void delete_test_fir(){
        String current_dir=System.getProperty("user.dir");
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
