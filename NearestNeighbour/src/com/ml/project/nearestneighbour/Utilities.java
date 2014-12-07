package com.ml.project.nearestneighbour;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Utilities {
	
	private double[] errs;
	private double avg;
	
	//takes two examples as input and returns the distance as the output
	public static double calculateDistance(Example e1, Example e2){
		return Math.sqrt(Math.pow(e1.x1-e2.x1, 2) + Math.pow(e1.x2-e2.x2, 2));
	}
	
	//calculates standard deviation for given errors and their average
	public double getStandardDeviation(){
		double x = 0.0;
		for(int i=0;i<this.errs.length;i++){
			x += Math.pow(this.errs[i]-this.avg, 2);
		}
		x /= this.errs.length-1;
		x = Math.sqrt(x);
		return x;
	}
	
	//returns the error for all permutations for a given nn
	public double getError(DataModel dataModel, int nn){
		double[] errs = new double[dataModel.permutations.length];
		for(int i=0;i<dataModel.permutations.length;i++){
			errs[i]=getErrorForCurrentPermutation(dataModel, dataModel.permutations[i], nn);
		}
		double avg = 0.0;
		for(int i=0;i<errs.length;i++){
			avg += errs[i];
		}
		avg = avg/errs.length;
		this.errs = errs;
		this.avg = avg;
		return avg;
	}
	
	//returns the error for a single permutation
	@SuppressWarnings("unchecked")
	public double getErrorForCurrentPermutation(DataModel dataModel, int[] permutation, int nn){
		int m = dataModel.m;
		int k = dataModel.k;
		int q = m/k;
		int r = m%k;
		int min = 0;
		int max = q-1;
		double error = 0.0;
		Object[] sets = new Object[k];
		for(int i=0;i<sets.length;i++){
			if(i==sets.length-1 && r!=0) max = permutation.length-1;
			sets[i] = new ArrayList<Integer>();
			for(int j=min;j<=max;j++){
				((ArrayList<Integer>)sets[i]).add(permutation[j]);
			}
			min = min + q;
			max = max + q;
		}
		List<Integer> distTo = null;
		List<Integer> distFrom = null;
		List<Integer> temp = null;
		List<Integer> temp1 = null;
		for(int i=0;i<sets.length;i++){
			distTo = new ArrayList<Integer>();
			for(int j=0;j<sets.length;j++){
				if(j!=i){
					temp = (ArrayList<Integer>)sets[j];
					Iterator<Integer> itr = temp.iterator();
			        while(itr.hasNext()){
			            distTo.add(itr.next());
			        }
				}
			}
			distFrom = new ArrayList<Integer>();
			temp1 = (ArrayList<Integer>)sets[i];
			Iterator<Integer> itr1 = temp1.iterator();
	        while(itr1.hasNext()){
	            distFrom.add(itr1.next());
	        }
	        
	        //At this point we have distTo and distFrom for each set
	        
	        Iterator<Integer> itrFrom = distFrom.iterator();
	        while(itrFrom.hasNext()){
	        	String predictedValue = "";
	        	Integer from = itrFrom.next();
	        	//HashMap<Integer, Double> map = new HashMap<Integer, Double>();
	        	List<Element1> list = new ArrayList<Element1>();
	        	double[] dists = new double[distTo.size()];
	        	int count = 0;
	        	Iterator<Integer> itrTo = distTo.iterator();
	        	while(itrTo.hasNext()){
	        		Integer to = itrTo.next();
	        		double dist = calculateDistance(dataModel.numberedExamples.get(from), dataModel.numberedExamples.get(to));
	        		dists[count++] = dist;
	        		//map.put(to, dist);
	        		list.add(new Element1(to, dist));
	        	}
	        	Arrays.sort(dists);
	        	HashSet<Integer> set = new HashSet<Integer>();
	        	boolean flag = false;
	        	for(int z=0;z<nn;z++){
	        		if(z<dists.length){
	        			double currDist = dists[z];
		        		//Iterator<Entry<Integer, Double>> itr4 = map.entrySet().iterator();
	        			ListIterator<Element1> listItr = list.listIterator();
		        	    while (listItr.hasNext()) {
		        	    	Element1 pair = listItr.next();
		        	        if(pair.dist==currDist){
		        	        	set.add(pair.to);
		        	        	if(set.size()==nn){
		        	        		flag = true;
		        	        		break;
		        	        	}
		        	        }
		        	    }
		        	    if(flag)
		        	    	break;
	        		}
	        	}
	        	int noOfPos = 0;
	        	int noOfNeg = 0;
	        	Iterator<Integer> setItr = set.iterator();
	        	while(setItr.hasNext()){
	        		Integer setElement = setItr.next();
	        		String y = dataModel.numberedExamples.get(setElement).y;
	        		if(y.equals("+")) noOfPos++;
	        		if(y.equals("-")) noOfNeg++;
	        	}
	        	if(noOfPos>noOfNeg) predictedValue = "+";
	        	else predictedValue = "-";
	        	String x = dataModel.numberedExamples.get(from).y;
	        	if(!x.equals(predictedValue)) error++;
	        }
		}
		return error/permutation.length;
	}

	public String getNewGrid(DataModel dataModel, int nn) {
		List<Example> distTo = new ArrayList<Example>();
		distTo.addAll(dataModel.numberedExamples);
		for(int i=0;i<dataModel.examples.length;i++){
			for(int j=0;j<dataModel.examples[i].length;j++){
				String predictedValue = "";
				Example from = dataModel.examples[i][j];
				if(from.y.equals(".")){
					int count = 0;
					Iterator<Example> distToItr = distTo.iterator();
					double[] dists = new double[distTo.size()];
					//HashMap<Example, Double> map = new HashMap<Example, Double>();
					List<Element2> list = new ArrayList<Element2>();
			        while(distToItr.hasNext()){
			        	Example to = distToItr.next();
			        	double dist = calculateDistance(from, to);
			        	dists[count++] = dist;
			        	//map.put(to, dist);
			        	list.add(new Element2(to, dist));
			        }
			        Arrays.sort(dists);
		        	HashSet<Example> set = new HashSet<Example>();
		        	boolean flag = false;
		        	for(int z=0;z<nn;z++){
		        		if(z<dists.length){double currDist = dists[z];
		        		//Iterator<Entry<Integer, Double>> itr4 = map.entrySet().iterator();
	        			ListIterator<Element2> listItr = list.listIterator();
		        	    while (listItr.hasNext()) {
		        	    	Element2 pair = listItr.next();
		        	        if(pair.dist==currDist){
		        	        	set.add(pair.to);
		        	        	if(set.size()==nn){
		        	        		flag = true;
		        	        		break;
		        	        	}
		        	        }
		        	    }
		        	    if(flag)
		        	    	break;}
		        	}
		        	int noOfPos = 0;
		        	int noOfNeg = 0;
		        	Iterator<Example> itr2 = set.iterator();
		        	while(itr2.hasNext()){
		        		Example setElement = itr2.next();
		        		String y = setElement.y;
		        		if(y.equals("+")) noOfPos++;
		        		if(y.equals("-")) noOfNeg++;
		        	}
		        	if(noOfPos>noOfNeg) predictedValue = "+";
		        	else predictedValue = "-";
		        	from.y = predictedValue;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<dataModel.examples.length;i++){
			for(int j=0;j<dataModel.examples[i].length;j++){
				sb.append(dataModel.examples[i][j].y+" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	

	private class Element1 {
		
		Integer to;
		double dist;
	
		public Element1(Integer to, double dist) {
			this.to = to;
			this.dist = dist;
		}
	}	


	private class Element2 {
		
		Example to;
		double dist;
	
		public Element2(Example to, double dist) {
			this.to = to;
			this.dist = dist;
		}
	}

}
