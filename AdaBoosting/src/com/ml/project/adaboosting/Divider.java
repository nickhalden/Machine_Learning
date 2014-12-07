package com.ml.project.adaboosting;

public class Divider {
	
	String sign;
	double threshold;
	int[] errorArray;
	double error;
	double[] g;
	
	public Divider(String sign, double threshold, int[] errorArray, double error, double[] g){
		this.sign = sign;
		this.threshold = threshold;
		this.errorArray = errorArray;
		this.error = error;
		this.g = g;
	}
}
