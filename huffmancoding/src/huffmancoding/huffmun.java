package huffmancoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;
/**
 * 
 */
/**
 * @author MernaAdel
 *
 */
public class huffmun {
	 
static String text="";	
static String path="";
static String encoded = "";
static String decoded = "";
static HashMap <Character,Double>hash =new HashMap();
static TreeMap<Character, String> codes = new TreeMap<>(); 
static String [] chararr ;
static Double [] freqarr ;
static int frearr[];
static int arr[];
static PriorityQueue<node> nodes = new PriorityQueue<>((o1, o2) -> (o1.valueofnode < o2.valueofnode) ? -1 : 1);
	static class node
	{
	   node left,right;
	   double valueofnode;
	   String c;
	   public node( double value,String chara) {
		   this.valueofnode=value;
		   this.c=chara;
		   this.left=null;
		   this.right=null;
		   
	   }
	   public node(node right,node left) {
		   this.valueofnode = left.valueofnode + right.valueofnode;
	        c = left.c + right.c;
	        if (left.valueofnode < right.valueofnode) { //as binary tree
	            this.right = right;
	            this.left = left;
	        } else {
	            this.right = left;
	            this.left = right;
	        }
		   
	   }
	}
	
	
public static void main (String args[]) throws Exception
	{
	  Scanner sc=new Scanner(System.in);
	  System.out.println("enter the name of textfile ");
	   path=sc.next();
	  
	  System.out.print("enter 1 to Compress File \n"
	  		+ "enter 2 to Decompress File \n"
	  		+ "enter 3 to compressFolder ");
	  
	  int n=sc.nextInt();
	  switch(n)
	  { 
	  case 1:
	  {  text = new String(Files.readAllBytes(Paths.get(path)));
	  long starttime = System.nanoTime();
		 if( gettingready(nodes))//rg3tly true eshta
		 {
			 buildtree(nodes);
			//System.out.print(nodes.peek().left.c);
			
			recursivestage(nodes.peek(), ""); //.peek() 2wl node fel tree
			//System.out.println(codes);//hashtable
			 long finishtime = System.nanoTime();
	            long time = finishtime - starttime;
	            System.out.println("Time in nanosecond   "+time);
	            int x=text.length()-1;
	            int y=0;
			 for(int i=0;i<x-1;i++)
			 {  //System.out.println(i);
				 encoded+=codes.get(text.charAt(i)); //search for every char 
				
				
			 }
			 for(int i=0;i<encoded.length();i++)
			 {if(y==7)
				 y=0; 
			 y++;
				//System.out.println(y+" "+i); 
			 }
			 //111100001
			 //11110000
			//System.out.println(encoded);
			BitSet bitSet = getBitSet(encoded);
			byte[] writeBytes = bitSet.toByteArray();
			
			String content =writeBytes.length+"\n";
			content+=y+"\n";
			content+="Header\n";
            for(Map.Entry code:codes.entrySet()) {
               content+= ""+  code.getKey() +">"+ code.getValue()+">"+  hash.get(code.getKey().toString().charAt(0)) + '\n';
            }
     
            content += "Body\n";

            Files.write(Paths.get(path+"s.txt"), content.getBytes(), StandardOpenOption.CREATE);
            BinaryOut binOut = new BinaryOut(path+"s.txt");
			for (int i = 0; i < encoded.length(); i++) {
				if (encoded.charAt(i) == '0')
					binOut.write(false); //Write 0 to the file
				else
					binOut.write(true); //Write 1 to the file
			}
                  binOut.close();
          
         // Files.write(Paths.get(path+"s.txt"), bval,  StandardOpenOption.APPEND);
                  System.out.println("character"+"    "+"ASCII"+"          "+"Code");
                  for(Map.Entry code:codes.entrySet()) {
                	  char c=code.getKey().toString().charAt(0);
                	  
                	 String u=Integer.toBinaryString((int) c);
                      System.out.println(code.getKey()+"            "+u+"         "+code.getValue());
                   }
			  double ratio=compressionRatio();
		 System.out.println("compression ratio  "+ratio);
		// System.out.print(hash);
		 }
		
		//for(int i=0;i<nodes.size();i++)
		//	System.out.print(nodes.peek().c);
		 
	  }break;
	  case 2:
	  {
		  
		//decompress
          //build codes
		  BufferedReader reader;
		 // Path fileLocation = Paths.get(path);
		  //byte[] data = Files.readAllBytes(fileLocation);
		//  String str = new String(data, "UTF-8");
          String string1 = "";
          String string2 =  "";
          boolean flag= false ;
       //   List<String> lines = Files.readAllLines(Paths.get(path));
          reader = new BufferedReader(new FileReader(
					path));
          String line = reader.readLine();
          line = reader.readLine();
			line = reader.readLine();
			while (line != null) {
				//System.out.println(line);
				// read next line
				 if(Objects.equals(line, "Header"))
	              {
	                  flag = true;
	              }
	              if(Objects.equals(line, "Body"))
	              {
	                 flag = false;
	              }
	              if(flag)
	              {
	                  //get codes and freq
	                  String [] merna =line.split(">");
	                  if(merna.length==3)
	                  { System.out.println(merna[0]);
	                	  if (merna[0].length() == 0) {
							
							 string1 = string1+System.getProperty("line.separator")+"~";
							 string2 = string2+merna[2]+"~";
							
                          	}else {
	                     string1 = string1+merna[0]+"~";
	                      string2 = string2+merna[2]+"~";}
	                      //codes.put(code[0].charAt(1),code[1]);
	                  }

	              }
	              else
	              {
	                  if(!Objects.equals(line, "Body"))
	              {    
	                 // encoded+= line ;
	                  break;

	                //  String decryptedText = encrypter.decrypt(encoded);
	               //  String str = new String(encoded, "UTF-8");
	                  
	              }}
				line = reader.readLine();
			}
			int bytesLength = getBytesLength(path);
			byte[] allFile = Files.readAllBytes(Paths.get(path));
			int fileSize = allFile.length;
			int u=0;
			BufferedReader buff = null;
			try {
				buff = new BufferedReader(new FileReader(path));
				String g=buff.readLine();
				 u=Integer.parseInt(buff.readLine());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			encoded = getEncodedString(allFile, fileSize, bytesLength,u);
		//	System.out.println(encoded);
			
			reader.close();
          
              string1.trim();
          
          String [] bytes_array = string1.split("~");//kol 7rof
          String [] freq_array = string2.split("~");
          freqarr = new Double[freq_array.length];
          chararr = new String[bytes_array.length];
          for(int i =0; i< bytes_array.length; i++)
          { // System.out.print(bytes_array[i]);
               
              freqarr[i]= Double.parseDouble(freq_array[i]);
             
               if(bytes_array[i].length()!=0)
              chararr[i] = bytes_array[i];

          }

         // System.out.println("Text to Decode: " + encoded);
          //decode text
          long starttime = System.nanoTime();
          ready();
          decodeText();
          long finishtime = System.nanoTime();
          long time = finishtime - starttime;
          System.out.println("Time in nanosecond  "+time);
         // System.out.println("Decoded Text: " + decoded);
         Files.write(Paths.get(path.replace("s.txt","")), decoded.getBytes(), StandardOpenOption.CREATE);
        //  decodeText();
	  }
	  break;
	  case 3:
	  {
		//compress folder
          //get path 
		  int flag=0;
      	File folder = new File("./"+path);
      	File[] files = folder.listFiles();
         // File[] files = new File(filepath).listFiles();
          //main header Header:foldername.comp:number of files
          String content ="Header:";
          content+= path + ":" ;
          content+= String.valueOf(files.length)+"\n";
          long starttime = System.nanoTime();
          for (int i=0 ; i< files.length;i++)
          {   flag++;
        	  
              text = new String(Files.readAllBytes(Paths.get(path +"/"+files[i].getName())));
              if( gettingready(nodes))//rg3tly true eshta....
     		 {
     			 buildtree(nodes);
     			//System.out.print(nodes.peek().left.c);
     			
     			recursivestage(nodes.peek(), ""); //.peek() 2wl node fel tree
     		//	System.out.println(codes);//hashtable
     			 
     	            int x=text.length()-1;
     			 for(int k=0;k<x-1;k++)
     			 { // System.out.println(k);
     				 encoded+=codes.get(text.charAt(k)); //search for every char 
     				 
     			 }
     			//System.out.println(encoded);
     			BitSet bitSet = getBitSet(encoded);
     			byte[] writeBytes = bitSet.toByteArray();
     			
     			 content+=writeBytes.length+"\n";
     			content+="Header\n";
                 for(Map.Entry code:codes.entrySet()) {
                    content+= ""+  code.getKey() +">"+ code.getValue()+">"+  hash.get(code.getKey().toString().charAt(0)) + '\n';
                 }
                // System.out.print(encoded);
              // BitSet bit=getBitSet(encoded);
                // System.out.print(bit);
              //  byte[] writebytes=bit.toByteArray();
//                 System.out.print(writebytes[0]);
//                 System.out.print(writebytes[1]);
//                 System.out.print(writebytes[2]);
                 content += "Body\n";
                 if(flag==1)
                 Files.write(Paths.get(path+"s.txt"), content.getBytes(), StandardOpenOption.CREATE);
                 else
                	 Files.write(Paths.get(path+"s.txt"), content.getBytes(),  StandardOpenOption.APPEND);
                 BinaryOut binOut = new BinaryOut(path+"s.txt");
     			for (int j = 0; j < encoded.length(); j++) {
     				if (encoded.charAt(j) == '0')
     					binOut.write(false); //Write 0 to the file
     				else
     					binOut.write(true); //Write 1 to the file
     			}
                       binOut.close();
                       System.out.println("character"+"    "+"ASCII"+"          "+"Code");
                       for(Map.Entry code:codes.entrySet()) {
                     	  char c=code.getKey().toString().charAt(0);
                     	  
                     	 String u=Integer.toBinaryString((int) c);
                           System.out.println(code.getKey()+"            "+u+"         "+code.getValue());
                        }
        

	  
	  }}
              long finishtime = System.nanoTime();
	            long time = finishtime - starttime;
	            System.out.println("Time in nanosecond  "+time);
              
	  } break;
	  case 4:{
		  BufferedReader reader;
		  reader = new BufferedReader(new FileReader(
						path));
		  
		  String line = reader.readLine();
		  String[]s=line.split(":");
		  int numoffiles=Integer.parseInt(s[2]);
		  for(int i=0;i<numoffiles;i++)
		  {
			//decompress
	          //build codes
			 
			 // Path fileLocation = Paths.get(path);
			  //byte[] data = Files.readAllBytes(fileLocation);
			//  String str = new String(data, "UTF-8");
	          String string1 = "";
	          String string2 =  "";
	          boolean flag= false ;
	       //   List<String> lines = Files.readAllLines(Paths.get(path));
	          
	          line = reader.readLine();
				line = reader.readLine();
				while (line != null) {
					//System.out.println(line);
					// read next line
					 if(Objects.equals(line, "Header"))
		              {
		                  flag = true;
		              }
		              if(Objects.equals(line, "Body"))
		              {
		                 flag = false;
		              }
		              if(flag)
		              {
		                  //get codes and freq
		                  String [] merna =line.split(">");
		                  if(merna.length==3)
		                  {
		                     string1 = string1+merna[0]+",";
		                      string2 = string2+merna[2]+",";
		                      //codes.put(code[0].charAt(1),code[1]);
		                  }

		              }
		              else
		              {
		                  if(!Objects.equals(line, "Body"))
		              {    
		                 // encoded+= line ;
		                  break;

		                //  String decryptedText = encrypter.decrypt(encoded);
		               //  String str = new String(encoded, "UTF-8");
		                  
		              }}
					line = reader.readLine();
				}
				int bytesLength = getBytesLength(path);
				BufferedReader buff = null;
				int o=0;
				try {
					buff = new BufferedReader(new FileReader(path));
					String g=buff.readLine();
					 o=Integer.parseInt(buff.readLine());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] allFile = Files.readAllBytes(Paths.get(path));
				int fileSize = allFile.length;
				encoded = getEncodedString(allFile, fileSize, bytesLength,o);
			//	System.out.println(encoded);
				
				reader.close();
	          

	              
	          
	          String [] bytes_array = string1.split(",");
	          String [] freq_array = string2.split(",");
	          freqarr = new Double[freq_array.length];
	          chararr = new String[bytes_array.length];
	          for(int j =0; j< freq_array.length; j++)
	          { // System.out.print(bytes_array[i]);
	                bytes_array[i].trim();
	              freqarr[j]= Double.parseDouble(freq_array[j]);
	              
	              chararr[j] = bytes_array[j]; //from string to char

	          }

	         // System.out.println("Text to Decode: " + encoded);
	          //decode text
	          long starttime = System.nanoTime();
	          ready();
	          decodeText();
	          long finishtime = System.nanoTime();
	          long time = starttime - finishtime;
	          System.out.println("Time in nanosecond"+time);
	          //System.out.println("Decoded Text: " + decoded);
	         Files.write(Paths.get(path.replace("s.txt","")), decoded.getBytes(), StandardOpenOption.CREATE);
			  
			  
			  
			  
		  }
		  
		  
	  }
	  }}

	static boolean gettingready(PriorityQueue<node> frame)
	{  //calc freq o(text)
		int n=text.length();
		if(n==0)
		{
			System.out.print("invalid...string empty");
			return false;
		}
		else
		{ int count=0;
			frearr=new int[256];
			
			for(int i=0;i<n-1;i++)
			{
				frearr[text.charAt(i)]++;  
				
			}
			//System.out.println("textlength"+n);
			for(int i=0;i<frearr.length;i++)
			{
				if(frearr[i]>0)
				{    
					 frame.add(new node(frearr[i] / ((n-1) * 1.0),((char) i) + "")); //node of char
					 hash.put((char)i, frearr[i] / ((n-1) * 1.0));
					 //System.out.println((char) i+" "+frearr[i] );
					 
				}
			}
			return true;
		}
	}
	  
	static void buildtree(PriorityQueue<node> queue)  
	{  
		while(queue.size()>1) { //o(3addnodes)
			node x =queue.poll();
			node y=queue.poll();
     //  System.out.println(x.c+" "+y.c);
			queue.add(new node(x,y));
		}
	}
	  
	 private static void recursivestage(node node, String s) {//peek
	        if (node != null) {
	            if (node.right != null) {
	            	//System.out.println(node.right.c);
	                recursivestage(node.right, s + "1");
	            }
	            if (node.left != null) {
	            	//System.out.println(node.left.c);
	                recursivestage(node.left, s + "0");}

	            if (node.left == null && node.right == null)
	                codes.put( node.c.charAt(0), s);//hashtable becouse search o(1) //s represent huffmun code
	        }
	    }  
	  
	 private static void ready() {
	        nodes.clear();
	        codes.clear();

	       makenewnodes(nodes,chararr,freqarr);
	        buildtree(nodes);
	        recursivestage(nodes.peek(),"");
	      
	    }
	  private static void makenewnodes(PriorityQueue<node> vector, String[] chars, Double[] freq) {



	        for (int i = 0; i < chars.length; i++)

	                vector.add(new node(freq[i], chars[i] + ""));
	                //if (printIntervals)
	                  //  System.out.println("'" + ((char) i) + "' : " + ASCII[i] / (text.length() * 1.0));

	    }
	  private static void decodeText() {
	        decoded = ""; int j=0;
	        node node = nodes.peek(); String newencode="";
	        //  System.out.print(encoded);
	        for (int i = 0; i < encoded.length(); ) {
	            node tmpNode = node;
	            while (tmpNode.left != null && tmpNode.right != null && i < encoded.length()) {
	                if (encoded.charAt(i) == '1')
	                    tmpNode = tmpNode.right;
	                else tmpNode = tmpNode.left;
	                i++;
	            }
	            if (tmpNode != null)
	                if (tmpNode.c.length() == 1)
	                    decoded += tmpNode.c;
	             //   else
	                   // System.out.println("Input not Valid");

	        }
	        //System.out.println("Decoded Text: " + decoded);
	    }
	  private static BitSet getBitSet(String str) {
			BitSet bitSet = new BitSet(str.length());
			int bitcounter = 0;
			for (Character c : str.toCharArray()) {
				if (c.equals('1')) {
					bitSet.set(bitcounter);
				}
				bitcounter++;
			}
			return bitSet;
		}
	  static int getBytesLength(String str) throws Exception {
			BufferedReader buff = null;
			try {
				buff = new BufferedReader(new FileReader(str));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (Integer.parseInt(buff.readLine()));
	}
	 static  String getEncodedString(byte[] allFile, int fileSize, int bytesLength,int u) {
			String s = "";
			/**
			 * Dealing with file bytes.
			 */
			for (int i = (fileSize - bytesLength); i < fileSize - 1; i++) {
				int temp = (byte) allFile[i] & 0xFF;
				s += String.format("%8s", Integer.toBinaryString(temp)).replace(' ', '0');
				
			}
		//	System.out.println(s);
			/**
			 * Dealing with the last byte.
			 */
			char lastShit = (char) allFile[allFile.length - 1];
			String lastShitaSString = String.format("%8s", Integer.toBinaryString(lastShit)).replace(' ', '0');
			int idx = 0;
		//	System.out.println(u);
			for (int i = 0; i < lastShitaSString.length(); i++) {
				if (i<u) {
					idx = i;
					s+=lastShitaSString.charAt(i);
					}
			}
		//	System.out.print(s);
			
			return s;
	}
	  static double compressionRatio()
	    {
	        double sum =0 ;
	        for(Map.Entry code:hash.entrySet()) {

	            sum+= codes.get(code.getKey()).length() * Double.parseDouble(code.getValue().toString());//sum+=huffmun code *freq
	        }
	        return sum/8.0;
	    }

}
