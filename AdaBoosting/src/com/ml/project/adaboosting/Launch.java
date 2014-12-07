package com.ml.project.adaboosting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Launch {
	
	static int T;
	static int n;
	static double e;
	static String inputFilePath = "inputo.txt";
	static HashMap<Integer, Iteration> iterations = new HashMap<Integer, Iteration>();
	static Iteration firstIteration;
	
	public static void main(String[] args) throws IOException {
		readFile(inputFilePath);
		recurse(firstIteration, 1);
		printIterations();
	}
	
	private static void readFile(String inputFilePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
		String line = null;
		int count = 0;
		double[] xValues = null;
		double[] yValues = null;
		double[] pValues = null;
		while((line = br.readLine())!=null){
			//System.out.println(line);
			String[] lineArr = line.trim().split(" ");
			if(count==0){
				T = Integer.parseInt(lineArr[0]);
				n = Integer.parseInt(lineArr[1]);
				e = Double.parseDouble(lineArr[2]);
			}
			if(count==1){
				xValues = new double[lineArr.length];
				for(int i=0;i<lineArr.length;i++){
					xValues[i] = Double.parseDouble(lineArr[i]);
				}
			}
			if(count==2){
				yValues = new double[lineArr.length];
				for(int i=0;i<lineArr.length;i++){
					yValues[i] = Double.parseDouble(lineArr[i]);
				}
			}
			if(count==3){
				pValues = new double[lineArr.length];
				for(int i=0;i<lineArr.length;i++){
					pValues[i] = Double.parseDouble(lineArr[i]);
				}
			}
			count++;
		}
		firstIteration = new Iteration(xValues, yValues, pValues, pValues);
	}
	
	private static void recurse(Iteration currentIteration, int count){
		if(count>T) return;
		currentIteration.execute();
		iterations.put(count, currentIteration);
		Iteration nextIteration = new Iteration(firstIteration.xValues, firstIteration.yValues, currentIteration.data.pValues, currentIteration.data.pValuesReal);
		recurse(nextIteration, count+1);
	}
	
	private static void printIterations(){
		System.out.println("BINARY ADABOOSTING\n");
		StringBuilder sb3 = null;
		StringBuilder function = new StringBuilder();
		double bound1 = 1.0;
		HashMap<Integer, Function> map = new HashMap<Integer, Function>();
		for(int t=1;t<=T;t++){
			sb3 = new StringBuilder();
			Data data = iterations.get(t).data;
			System.out.println("Iteration "+t);
			System.out.println("Classifier h = "+"I(x"+data.weakH.sign+data.weakH.threshold+")");	
			System.out.println("Error = "+data.error);
			System.out.println("Alpha = "+data.weight);
			System.out.println("Normalization factor Z = "+data.z);
			for(int i=0;i<data.pValues.length;i++){
				sb3.append(data.pValues[i]+" ");
			}
			System.out.println("Pi after normalization = "+sb3.toString());
			function.append(data.weight+"*I(x"+data.weakH.sign+data.weakH.threshold+")");
			System.out.println("Boosted Classifier f(x) = "+function.toString());
			function.append(" + ");
			map.put(t, new Function(data.weight, data.weakH.sign, data.weakH.threshold));
			System.out.println("Boosted Classifier Error = "+getError1(map, t, iterations.get(t).xValues, iterations.get(t).yValues));
			bound1 = bound1*data.z;
			System.out.println("Bound on Error = "+bound1);
			System.out.println("");
		}
		System.out.println("\n");
		System.out.println("===========================================================================================");
		System.out.println("REAL ADABOOSTING\n");
		double bound = 1.0;
		StringBuilder sb1 = null;
		StringBuilder sb2 = null;
		for(int t=1;t<=T;t++){
			sb1 = new StringBuilder();
			sb2 = new StringBuilder();
			Data data = iterations.get(t).data;
			System.out.println("Iteration "+t);
			System.out.println("Classifier h = "+"I(x"+data.weakHReal.sign+data.weakHReal.threshold+")");	
			System.out.println("G error = "+data.weightReal);
			System.out.println("C_plus = "+data.ctp+", C_minus = "+data.ctm);
			System.out.println("Normalization factor Z = "+data.zReal);
			for(int i=0;i<data.pValuesReal.length;i++){
				sb1.append(data.pValuesReal[i]+" ");
			}
			System.out.println("Pi after normalization = "+sb1.toString());
			for(int i=0;i<data.gt.length;i++){
				if(t!=1){
					Data prevData = iterations.get(t-1).data;
					data.gt[i] = data.gt[i] + prevData.gt[i];
				}
				sb2.append(data.gt[i]+" ");
			}
			System.out.println("f(x) = "+sb2.toString());
			//System.out.println("E"+t+" = "+data.errorReal);
			bound = bound*data.zReal;
			System.out.println("Boosted Classifier Error = "+getError(sb2.toString(), iterations.get(t).yValues));
			System.out.println("Bound on Error = "+bound);
			System.out.println("");
		}
	}
	
	private static double getError(String function, double[] yValues){
		String[] functionArr = function.trim().split(" ");
		double[] functionArrV = new double[functionArr.length];
		int[] boolArr1 = new int[yValues.length];
		double e = 0.0;
		for(int i=0;i<functionArr.length;i++){
			functionArrV[i] = Double.parseDouble(functionArr[i]);
			if(functionArrV[i]>0)
				boolArr1[i]=1;
			if(functionArrV[i]<0)
				boolArr1[i]=-1;
		}
		for(int i=0;i<boolArr1.length;i++){
			double p = boolArr1[i]*yValues[i];
			if(!(p>0)){
				e++;
			}
		}
		return e/yValues.length;
	}
	
	private static double getError1(HashMap<Integer, Function> map, int t, double[] xValues, double[] yValues){
		double[] combinedFOXValues = new double[xValues.length];
		double e = 0.0;
		for(int i=1;i<=t;i++){
			if(i>1)
				combinedFOXValues = add2Arrays(fxnVal(map.get(i), xValues), fxnVal(map.get(i-1), xValues));
			else
				combinedFOXValues = fxnVal(map.get(i), xValues);
		}
		for(int i=0;i<xValues.length;i++){
			double xx = combinedFOXValues[i];
			double y = yValues[i];
			double p = xx*y;
			if(!(p>0))
				e++;
		}
		
		return e/xValues.length;
	}
	
	private static double[] add2Arrays(double[] a1, double[] a2){
		double[] a3 = new double[a1.length];
		for(int i=0;i<a3.length;i++){
			a3[i] = a1[i]+a2[i];
		}
		return a3;
	}
	
	private static double[] fxnVal(Function fxn, double[] xValues){
		double[] foxValues = new double[xValues.length];
		for(int i=0;i<xValues.length;i++){
			double x = xValues[i];
			double fox = 0.0;
			if(fxn.sign.equals("<")){
				if(x<fxn.threshold){
					fox=1;
				}
				else{
					fox=-1;
				}
			}
			if(fxn.sign.equals(">")){
				if(x>fxn.threshold){
					fox=1;
				}
				else{
					fox=-1;
				}
			}
			fox = fox*fxn.weight;
			foxValues[i] = fox;
		}
		return foxValues;
	}
}
