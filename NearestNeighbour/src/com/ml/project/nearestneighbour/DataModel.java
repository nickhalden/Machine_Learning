package com.ml.project.nearestneighbour;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataModel {
	
	int k;
	int m;
	int t;
	int r;
	int c;
	String inputCrossValidFilePath;
	String inputDataFilePath;
	Example[][] examples = null;
	int[][] permutations = null;
	List<Example> numberedExamples = null;
	
	DataModel(String inputCrossValidFilePath, String inputDataFilePath) throws NumberFormatException, IOException{
		this.inputCrossValidFilePath = inputCrossValidFilePath;
		this.inputDataFilePath = inputDataFilePath;
		readFile(inputCrossValidFilePath);
		readFile(inputDataFilePath);
	}
	
	private void readFile(String path) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		String fileType = null;
		int count = 0;
		int no = -1;
		while ((line = br.readLine()) != null) {
			count++;
		    String[] attrs = line.trim().split(" ");
		    int len = attrs.length;
		    if(len==2){
		    	r = Integer.parseInt(attrs[0]);
		    	c = Integer.parseInt(attrs[1]);
		    	examples = new Example[r][c];
		    	numberedExamples = new ArrayList<Example>();
		    	fileType = "data";
		    }
		    else if(len==3){
		    	k = Integer.parseInt(attrs[0]);
		    	m = Integer.parseInt(attrs[1]);
		    	t = Integer.parseInt(attrs[2]);
		    	permutations = new int[t][m];
		    	fileType = "crossvalid";
		    }
		    else{
		    	if(fileType!=null){
		    		if(fileType.equals("data")){
		    			for(int i=0;i<len;i++){
		    				if(attrs[i].equals("+")||attrs[i].equals("-")){
		    					examples[count-2][i] = new Example(i, count-2, attrs[i], ++no);
		    					numberedExamples.add(examples[count-2][i]);
		    				}
		    				else
		    					examples[count-2][i] = new Example(i, count-2, attrs[i], -1);
		    			}		
		    		}
		    		else if(fileType.equals("crossvalid")){
	    				for(int i=0;i<len;i++)
	    					permutations[count-2][i]=Integer.parseInt(attrs[i]);	
		    		}
		    	}
		    }
		}
	}
}