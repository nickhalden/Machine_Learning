package com.ml.project.adaboosting;

public class Classifier {
	
	String[] sign;
	double[] threshold;
	double[] weight;
	
	public Classifier(String[] sign, double[] threshold, double[] weight){
		this.sign = sign;
		this.threshold = threshold;
		this.weight = weight;
	}
}