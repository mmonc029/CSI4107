
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;



public class Parser
{
	
	private static List<String> ParsedList = new ArrayList<String>();
	

	public static void main(String[] args)
	{
		

		
		
		ParsedList=getListParsed();
		
        for (int i = 0; i < ParsedList.size(); i++)
        {
	        String twitter_Data = ParsedList.get(i);
	        System.out.println(twitter_Data);
        }
        
        

	}
	
		
	private static  List<String> getListParsed()
	{
		
		BufferedReader reader = null;
		List<String> ListParsed  = new ArrayList<String>();
		List<String> StopWordsList = new ArrayList<String>();
		StopWordsList = getListWords("Finalstopwords.txt");
		List<String> PositiveWords = new ArrayList<String>();
		PositiveWords = getListWords("Posemo.txt");
		//System.out.println(PositiveWords.size());
		List<String> NegativeWords = new ArrayList<String>();
		NegativeWords = getListWords("Negemo.txt");
		//System.out.println(NegativeWords.size());
		
		
		List<String> PositiveEmoticons= new ArrayList<String>();
		PositiveEmoticons = getListWords("PositiveEmoticons.txt");
		
		List<String> NegativeEmoticons = new ArrayList<String>();
		NegativeEmoticons = getListWords("NegativeEmoticons.txt");
		


		try {
		    File file = new File("Data/defaultMessages.txt");
		    reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {

		    	String ClassAndData = line.substring(line.indexOf('"' )+ 1, line.length());
		    	int first_occurence = ClassAndData.indexOf('"' ); 
		    	String classifier = ClassAndData.substring(0, ClassAndData.indexOf('"'));
		    	
		    	
		    	
		    	//String ClassAttribute = line.substring(line.indexOf('"' ) + 1, line.indexOf('"' ));
		    	String example = ClassAndData.substring(ClassAndData.indexOf('"' ) + 1, ClassAndData.length());
		    	String defaultExample =ClassAndData.substring(ClassAndData.indexOf('"' ) + 1, ClassAndData.length());
		    	defaultExample=defaultExample.replaceAll("[']", " ");
		    	String emoticonexample = example.replaceAll("[a-zA-Z0-9]", " ");
		    	String wordexample = example.replaceAll("[^a-zA-Z0-9]", " ");
		    	//System.out.println(emoticonexample);
				StringBuilder result = new StringBuilder(example.length());
				
				//StringBuilder emoticonresult = new StringBuilder(emoticonexample.length());

				//Remove stopwords
				for (String s : example.toLowerCase().split("\\b"))
				{
				    if (!StopWordsList.contains(s)) result.append(s);
				}
				//count positive and negative words
				int poscount = 0;
				int negcount=0;
				int posemoticoncount = 0;
				int negemoticoncount=0;
				for (String s : wordexample.toLowerCase().split("\\s"))
				{
					//System.out.println(s);
				    if (PositiveWords.contains(s)) poscount++;
				}
				
				for (String s : wordexample.toLowerCase().split("\\s"))
				{
				    if (NegativeWords.contains(s)) negcount++;
				}
				
				for (String s : emoticonexample.split("\\s"))
				{
					//System.out.println(s);
				    if (PositiveEmoticons.contains(s)) posemoticoncount++;
				}
				
				for (String s : emoticonexample.split("\\s"))
				{
				    if (NegativeEmoticons.contains(s)) negemoticoncount++;
				}
				

		        // delimiter used .,;:'"?()!
				
				
				//example = result.toString().replaceAll("[0-9]", " ");
		    	//filter out websites and punctuation marks
				example = result.toString().replaceAll("[w][w][w][.][a-zA-Z0-9|^a-zA-Z0-9|\\S]*[\\s]","");
				example = example.replaceAll("[h][t][t][p][:][/][/][\\S]*|[h][t][t][p][:][/][/][\\S]*[\\s]","");
				example = example.replaceAll("[.,'?@%&â€+->$/#`]", " ");
				example = example.replaceAll("[0-9]", " ");
				example = example.replaceAll("[ ][\\S][ ]", " ");
				int positivefeeling = poscount + posemoticoncount;
				int negativefeeling = negcount + negemoticoncount;
				
				if(positivefeeling > negativefeeling)
				{
					
					example = "'" + example.toLowerCase() + " '," + classifier + "," + poscount+ "," + negcount+ ","+posemoticoncount+","+negemoticoncount+",positive";
				}
				else if(positivefeeling < negativefeeling)
				{
					example = "'" + example.toLowerCase() + " '," + classifier + "," + poscount+ "," + negcount+ ","+posemoticoncount+","+negemoticoncount+",negative";
					
				}
				else if(positivefeeling==0 && negativefeeling==0 )
				{
					example = "'" + example.toLowerCase() + " '," + classifier + "," + poscount+ "," + negcount+ ","+posemoticoncount+","+negemoticoncount+",objective";
					
				}
				else
				{
					example = "'" + example.toLowerCase() + " '," + classifier + "," + poscount+ "," + negcount+ ","+posemoticoncount+","+negemoticoncount+",neutral";
					
				
				}
				
				//example = "'" + defaultExample.toLowerCase() + " '," + classifier;
				ListParsed.add(example);
		    	
		    }		
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		CreateFile("data/ParsedData.arff", ListParsed);
		
		return ListParsed;	
	}

	//Retrieve the list of stopwords from stopwords.txt and store into an array list to be used for stop word removal
	private static  List<String> getListWords(String File_name)
	{
		BufferedReader reader = null;
		List<String> WordsList  = new ArrayList<String>();

		try {
		    File file = new File("data/"+File_name);
		    reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	WordsList.add(line);
		    }
		
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		return WordsList;	
	}
	

	//Creates a file with the information from an array list 
	private static void CreateFile(String Directory, List<String> parser)
	{
		try {
			 
		      File file = new File(Directory);
	
		      if (file.createNewFile()){
		        System.out.println("File is created!");
		      }else{
		  		Writer writer = null;

				try {
				    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Directory), "utf-8"));
				    writer.write("@relation opinion\n");
				    writer.write("@attribute sentence string\n");
				    writer.write("@attribute category {positive,negative,neutral,objective}\n");
				    
				    writer.write("@attribute positiveW NUMERIC\n");
				    writer.write("@attribute negativeW NUMERIC\n");
				    writer.write("@attribute positiveE NUMERIC\n");
				    writer.write("@attribute negativeE NUMERIC\n");
				    writer.write("@attribute feelingcategory {positive,negative,neutral,objective}\n");
				    writer.write("@data\n");

				    for(int i=0; i< parser.size(); i++)
				    {
				    	writer.write(parser.get(i) + "\n");
				    }
				} catch (IOException ex) {
				  // report
				} finally {
				   try {writer.close();} catch (Exception ex) {}
				}
			 }
	
	  	} catch (IOException e) {
		      e.printStackTrace();
		}
		
		
	}
	//remove items from one list that are the same as the other then save the file
	private static  void ListCompare(String Directory, List<String> stopwordslist, List<String> list)
	{
	      System.out.println(stopwordslist.size());
      for (int i = 0; i < stopwordslist.size(); i++)
      {    	
	            for (int j = 0; j < list.size(); j++)
	            {
		            	if(stopwordslist.get(i).equals(list.get(j)))
		            	{
		            		
		            		stopwordslist.remove(stopwordslist.get(i));
		            		//i=0;
		            		//length = stopwordslist.size();
		            	}
	
	    	        
	            }
			}
        System.out.println(stopwordslist.size());
        System.out.println(list.size());
        CreateFile(Directory, stopwordslist);
		
		
	}
	
	
	
	
}