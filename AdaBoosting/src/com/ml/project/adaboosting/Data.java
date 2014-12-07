package com.ml.project.adaboosting;

public class Data {
	
	WeakClassifier weakH;
	WeakClassifier weakHReal;
	double error;
	double errorReal;
	double weight;
	double weightReal;
	double z;
	double zReal;
	double[] pValues;
	double[] pValuesReal;
	double ctp;
	double ctm;
	double[] gt;
	
	public Data(WeakClassifier weakH, WeakClassifier weakHReal, double error, double errorReal, 
			double weight, double weightReal, double z, double zReal, double[] pValues, double[] pValuesReal,
			double ctp, double ctm, double[] gt){
		this.weakH = weakH;
		this.weakHReal = weakHReal;
		this.error = error;
		this.errorReal = errorReal;
		this.weight = weight;
		this.weightReal = weightReal;
		this.z = z;
		this.zReal = zReal;
		this.pValues = pValues;
		this.pValuesReal = pValuesReal;
		this.ctp = ctp;
		this.ctm = ctm;
		this.gt = gt;
	}
}
