package hufman;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/* Huffman coding , decoding */

class Node {
    //Tree structure
    Node left, right;
    double value;
    String character;

    public Node(double value, String character) {
        this.value = value;
        this.character = character;
        left = null;
        right = null;
    }

    public Node(Node left, Node right) {
        this.value = left.value + right.value;
        character = left.character + right.character;
        if (left.value < right.value) {
            this.right = right;
            this.left = left;
        } else {
            this.right = left;
            this.left = right;
        }
    }
}

public class Huffman {

    static PriorityQueue<Node> nodes = new PriorityQueue<>((o1, o2) -> (o1.value < o2.value) ? -1 : 1); //tree nodes
    static TreeMap<Character, String> codes = new TreeMap<>(); //tree map for codes
    static String text = ""; //string of file
    static String filepath = "";
    static String encoded = "";
    static String decoded = "";
    static int ASCII[] = new int[128];
    static char [] chars ;
    static Double [] freq ;
    static HashMap<Integer,Double> codebook = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Scanner scanner_c =  new Scanner(System.in); // scanner for command line
        System.out.println("Enter the file path");
        
               filepath=scanner_c.nextLine();
        int choice ;
        System.out.println("Press:\n0 to compress a text file \n1 to decompress a text file \n2 to compress a binary file\n3 to compress a folder\n4 to decompress a folder ");
        choice = Integer.parseInt(scanner_c.nextLine());
        if (choice==0){
            //compress
        	
     	   
      	  text = new String(Files.readAllBytes(Paths.get(filepath)));
            System.out.println("Text to Encode: " + text);
            long lStartTime = System.nanoTime();
            handleNewText(); //
            encodeText();
            long lEndTime = System.nanoTime();
            long output = lEndTime - lStartTime;


            String content ="Header\n";
            for(Map.Entry code:codes.entrySet()) {
               content+= ""+  code.getKey() +": "+ code.getValue()+" : "+  codebook.get(code.getKey()) + '\n';
            }
            content += "Body\n" + encoded;
            printCodes();
            Files.write(Paths.get(filepath+".comp"), content.getBytes(), StandardOpenOption.CREATE);
            System.out.println("Compression time in milliseconds: " + output / 1000000);
            System.out.println("Compression Ratio: " + compressionRatio());


        }
        else if (choice ==1)
        {
            //decompress
            //build codes
            String chars_s = "";
            String freq_s =  "";
            boolean header= false ;
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            for(String line: lines)
            {
                if(Objects.equals(line, "Header"))
                {
                    header = true;
                }
                if(Objects.equals(line, "Body"))
                {
                    header = false;
                }
                if(header)
                {
                    //get codes and freq
                    String [] code =line.split(":");
                    if(code.length==3)
                    {
                        chars_s = chars_s+code[0]+",";
                        freq_s = freq_s+code[2]+",";
                        //codes.put(code[0].charAt(1),code[1]);
                    }

                }
                else
                {
                    if(!Objects.equals(line, "Body"))
                {
                    encoded+= line ;
                }


                }
            }
            String [] bytes_array = chars_s.split(",");
            String [] freq_array = freq_s.split(",");
            freq = new Double[freq_array.length];
            chars = new char[bytes_array.length];
            for(int i =0; i< freq_array.length; i++)
            {
                freq[i]= Double.parseDouble(freq_array[i]);
                chars[i] = (char) Integer.parseInt(bytes_array[i]);

            }

            System.out.println("Text to Decode: " + encoded);
            //decode text
            handleDecodingNewText();
            decodeText();
            System.out.println("Decoded Text: " + decoded);
            Files.write(Paths.get(filepath.replace(".comp","")), decoded.getBytes(), StandardOpenOption.CREATE);
        }
        else if (choice==2)
        {
            //binary files
            byte [] binary = read(filepath);
            System.out.println(binary[0]);
            StringBuilder sb = new StringBuilder();
            for (byte b : binary) {
                sb.append(String.format("%02X ", b));
            }
            
            text = sb.toString().replace(" ", "");
            
            System.out.println("Text to Encode: " + text);
            handleNewText();
            encodeText();
          //  printCodes();
            String content ="Header\n";
            for(Map.Entry code:codes.entrySet()) {
                content+= ""+  code.getKey() +": "+ code.getValue()+" : "+  codebook.get(code.getKey()) + '\n';
            }
            content += "Body\n" + encoded;
            Files.write(Paths.get(filepath+".comp"), content.getBytes(), StandardOpenOption.CREATE);
        }
        else if(choice==3)
        {
            //compress folder
            //get path
        	File folder = new File("./"+filepath);
        	File[] files = folder.listFiles();
           // File[] files = new File(filepath).listFiles();
            //main header Header:foldername.comp:number of files
            String content ="Header:";
            content+= filepath + ":" ;
            content+= String.valueOf(files.length)+"\n";
            long lStartTime = System.nanoTime();
            for (int i=0 ; i< files.length;i++)
            {

                text = new String(Files.readAllBytes(Paths.get(filepath +"/"+files[i].getName())));
                handleNewText(); //
                encodeText();
              //  printCodes();
                content +="Header:" + files[i].getName()+"\n";
                for(Map.Entry code:codes.entrySet()) {
                    content+= ""+  code.getKey() +": "+ code.getValue()+" : "+  codebook.get(code.getKey()) + '\n';
                }
                content += "Body:"+  encoded +"\n";
            }


            //System.out.println("Text to Encode: " + text);
            long lEndTime = System.nanoTime();
            long output = lEndTime - lStartTime;


            Files.write(Paths.get(filepath+".comp"), content.getBytes(), StandardOpenOption.CREATE);
        }
        else if (choice ==4)
        {
           //decompress

            List<String> lines = Files.readAllLines(Paths.get(filepath));
            String main_header[] = lines.get(0).split(":");
            int num_files = Integer.parseInt(main_header[2]);
            String filename ="" ;
            lines.remove(0);
            for (int i=0 ; i <num_files;i++)
            {
                encoded = "";
                decoded = "";
                String chars_s = "";
                String freq_s =  "";
                boolean header_ = false;
                int x = 0 ;
                for (x =0 ; x< lines.size();x++)
                {
                    String header[] = lines.get(x).split(":");
                    if (Objects.equals(header[0], "Header"))
                    {

                        if (Objects.equals(filename, "") || !Objects.equals(header[1], filename) )
                        {
                            //new file
                            header_ = true ;
                            filename = header[1];

                        }

                    }
                    if(Objects.equals(lines.get(x), "Body"))
                    {
                        header_ = false;

                    }
                    if(header_)
                    {
                        //get codes and freq
                        String [] code =lines.get(x).split(":");
                        if(code.length==3)
                        {
                            chars_s = chars_s+code[0]+",";
                            freq_s = freq_s+code[2]+",";
                            //codes.put(code[0].charAt(1),code[1]);
                        }

                    }
                    else
                    {
                        if(!Objects.equals(lines.get(x), "Body"))
                        {
                            encoded+= lines.get(x) ;
                            lines.remove(x);
                            break;
                        }

                    }
                    lines.remove(x);
                }

                String [] bytes_array = chars_s.split(",");
                String [] freq_array = freq_s.split(",");
                freq = new Double[freq_array.length];
                chars = new char[bytes_array.length];
                for(int j =0; j< freq_array.length; j++)
                {
                    freq[j]= Double.parseDouble(freq_array[j]);
                    chars[j] = (char) Integer.parseInt(bytes_array[j]);

                }

                System.out.println("Text to Decode: " + encoded);
                //decode text
                handleDecodingNewText();
                decodeText();
                System.out.println("Decoded Text: " + decoded);
                Files.write(Paths.get(filepath.replace(".comp","") +"/"+ filename), decoded.getBytes(), StandardOpenOption.CREATE);
            }
        }
    }

    private static void handleDecodingNewText() {
        nodes.clear();
        codes.clear();

        putCharIntervals(nodes,chars,freq);
        buildTree(nodes);
        generateCodes(nodes.peek(),"");
        printCodes();
        //decodeText();
    }

    private static boolean handleNewText() {
        int oldTextLength = text.length();
        //System.out.println("Enter the text:");
        //text = scanner.nextLine();
        if (oldTextLength == 0) {
            System.out.println("Not Valid input");
            text = "";
            return true;
        }
            ASCII = new int[128];
            nodes.clear();
            codes.clear();
            encoded = "";
            decoded = "";
            calculateCharIntervals(nodes); // cal freqs
            buildTree(nodes);
            generateCodes(nodes.peek(), ""); //.peek() 2wl node fel tree
            return false;



    }

    private static boolean IsSameCharacterSet() {
        boolean flag = true;
        for (int i = 0; i < text.length(); i++)
            if (ASCII[text.charAt(i)] == 0) {
                flag = false;
                break;
            }
        return flag;
    }

    private static void decodeText() {
        decoded = "";
        Node node = nodes.peek();
        for (int i = 0; i < encoded.length(); ) {
            Node tmpNode = node;
            while (tmpNode.left != null && tmpNode.right != null && i < encoded.length()) {
                if (encoded.charAt(i) == '1')
                    tmpNode = tmpNode.right;
                else tmpNode = tmpNode.left;
                i++;
            }
            if (tmpNode != null)
                if (tmpNode.character.length() == 1)
                    decoded += tmpNode.character;
                else
                    System.out.println("Input not Valid");

        }
        //System.out.println("Decoded Text: " + decoded);
    }

    private static void encodeText() {
        encoded = "";
        for (int i = 0; i < text.length(); i++)
            encoded += codes.get(text.charAt(i)).toString();
        System.out.println("Encoded Text: " + encoded);
    }

    private static void buildTree(PriorityQueue<Node> vector) {
        while (vector.size() > 1)
            vector.add(new Node(vector.poll(), vector.poll()));

    }

    private static void printCodes() {
        System.out.println("--- Printing Codes ---");
        codes.forEach((k, v) -> System.out.println("'" + k + "' : " +  Integer.toBinaryString(Integer.parseInt(String.valueOf(k)))+":" + v));
    }

    private static void calculateCharIntervals(PriorityQueue<Node> vector) {
       System.out.println("-- Freqs --");

        for (int i = 0; i < text.length(); i++)
            ASCII[text.charAt(i)]++;

        for (int i = 0; i < ASCII.length; i++)
            if (ASCII[i] > 0) {
                vector.add(new Node(ASCII[i] / (text.length() * 1.0), ((char) i) + ""));
                codebook.put(i,ASCII[i] / (text.length() * 1.0));


                System.out.println("" +  i + " : " + ASCII[i] / (text.length() * 1.0));
            }
    }
    private static void putCharIntervals(PriorityQueue<Node> vector, char[] chars, Double[] freq) {



        for (int i = 0; i < chars.length; i++)

                vector.add(new Node(freq[i], chars[i] + ""));
                //if (printIntervals)
                  //  System.out.println("'" + ((char) i) + "' : " + ASCII[i] / (text.length() * 1.0));

    }
    private static void generateCodes(Node node, String s) {
        if (node != null) {
            if (node.right != null)
                generateCodes(node.right, s + "1");
                           
            if (node.left != null)
                generateCodes(node.left, s + "0");

            if (node.left == null && node.right == null)
                codes.put(node.character.charAt(0), s);
        }
    }
    static double compressionRatio()
    {
        double sum =0 ;
        for(Map.Entry code:codebook.entrySet()) {

            sum+= codes.get(code.getKey()).length() * Double.parseDouble(code.getValue().toString());
        }
        return sum/8.0;
    }
    static byte[] read(String aInputFileName){

        File file = new File(aInputFileName);

        byte[] result = new byte[(int)file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length){
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0){
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }

            }
            finally {

                input.close();
            }
        }
        catch (FileNotFoundException ex) {

        }
        catch (IOException ex) {

        }
        return result;
    }
}
