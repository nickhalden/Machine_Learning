package com.ml.project.adaboosting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Iteration {
	
	double[] xValues;
	double[] yValues;
	double[] pValues;
	double[] pValuesReal;
	Data data;
	
	public Iteration(double[] xValues, double[] yValues, double[] pValues, double[] pValuesReal){
		this.xValues = xValues;
		this.yValues = yValues;
		this.pValues = pValues;
		this.pValuesReal = pValuesReal;
	}
	
	public void execute(){
		double[] midX = new double[xValues.length-1];
		for(int i=0;i<midX.length;i++){
			midX[i]=(xValues[i]+xValues[i+1])/2;
		}
		List<Divider> dividers = new ArrayList<Divider>();
		List<Divider> dividers1 = new ArrayList<Divider>();
		for(int i=0;i<midX.length;i++){
			double threshold = midX[i];
			String sign = "";
			String sign1 = "";
			double error = 0;
			double errorLessThan = 0;
			double errorGreaterThan = 0;
			int[] errorArray = new int[xValues.length];
			int[] errorLessThanArray = new int[xValues.length];
			int[] errorGreaterThanArray = new int[xValues.length];
			for(int j=0;j<xValues.length;j++){
				double x = xValues[j];
				if((x<threshold && yValues[j]!=1) || (x>threshold && yValues[j]!=-1)){
					errorLessThan = errorLessThan + pValues[j]; 
					errorLessThanArray[j]=1;
				}
				if((x<threshold && yValues[j]!=-1) || (x>threshold && yValues[j]!=1)){
					errorGreaterThan = errorGreaterThan + pValues[j]; 
					errorGreaterThanArray[j]=1;
				}
			}
			double[] gHL = getHLess(threshold);
			double[] gHM = getHMore(threshold);
			double[] g = gHL;
			sign1 = "<";
			if(gHM[4]<gHL[4]){
				g = gHM;
				sign1 = ">";
			}
			error = errorLessThan;
			sign = "<";
			errorArray = errorLessThanArray;
			if(errorGreaterThan<errorLessThan){
				error = errorGreaterThan;
				sign = ">";
				errorArray = errorGreaterThanArray;
			}
			dividers.add(new Divider(sign, threshold, errorArray, error, null));
			dividers1.add(new Divider(sign1, threshold, null, g[5], g));
		}
		Divider divider = dividers.get(0);
		Iterator<Divider> iterator = dividers.iterator();
		while(iterator.hasNext()){
			Divider div = iterator.next();
			if(div.error<divider.error)
				divider = div;
		}
		Divider divider1 = dividers1.get(0);
		Iterator<Divider> iterator1 = dividers1.iterator();
		while(iterator1.hasNext()){
			Divider div1 = iterator1.next();
			if(div1.g[4]<divider1.g[4]){
				divider1 = div1;
			}
		}
		double[] c = getC(divider1.g);
		double[] gt = getGT(c, divider1);
		double[] pValuesNewReal = getPValuesNewReal(gt);
		double z1 = 0.0;
		for(int i=0;i<pValuesNewReal.length;i++){
			z1 += pValuesNewReal[i];
		}
		for(int i=0;i<pValuesNewReal.length;i++){
			pValuesNewReal[i] = pValuesNewReal[i]/z1;
		}
		double alpha = calculateAlpha(divider.error);
		double[] pValuesNew = new double[pValues.length];
		for(int i=0;i<pValues.length;i++){
			double q = 0.0;
			if(divider.errorArray[i]==0){
				q = Math.exp(-alpha);
			}
			else if(divider.errorArray[i]==1){
				q = Math.exp(alpha);
			}
			pValuesNew[i] = pValues[i]*q;
		}
		double z = 0.0;
		for(int i=0;i<pValuesNew.length;i++){
			z += pValuesNew[i];
		}
		for(int i=0;i<pValuesNew.length;i++){
			pValuesNew[i] = pValuesNew[i]/z;
		}
		this.data = new Data(new WeakClassifier(divider.sign, divider.threshold), new WeakClassifier(divider1.sign, divider1.threshold), 
				divider.error, divider1.error, alpha, divider1.g[4], z, z1, pValuesNew, pValuesNewReal, c[0], c[1], gt);
	}
	
	private double calculateAlpha(double error){
		return error = 0.5*Math.log((1-error)/error);
	}
	
	private double[] getHLess(double threshold){
		double prPlus = 0.0;
		double prMinus = 0.0;
		double pwPlus = 0.0;
		double pwMinus = 0.0;
		double error = 0.0;
		for(int j=0;j<xValues.length;j++){
			double x = xValues[j];
			if(x<threshold && yValues[j]==1){
				prPlus = prPlus + pValuesReal[j];
			}
			if(x>threshold && yValues[j]==-1){
				prMinus = prMinus + pValuesReal[j];
			}
			if(x>threshold && yValues[j]==1){
				pwPlus = pwPlus + pValuesReal[j];
				error = error + pValuesReal[j];
			}
			if(x<threshold && yValues[j]==-1){
				pwMinus = pwMinus + pValuesReal[j];
				error = error + pValuesReal[j];
			}
		}
		double g = Math.sqrt(prPlus*pwMinus)+Math.sqrt(pwPlus*prMinus);
		double[] hLess = {prPlus, prMinus, pwPlus, pwMinus, g, error};
		return hLess;
	}
	
	private double[] getHMore(double threshold){
		double prPlus = 0.0;
		double prMinus = 0.0;
		double pwPlus = 0.0;
		double pwMinus = 0.0;
		double error = 0.0;
		for(int j=0;j<xValues.length;j++){
			double x = xValues[j];
			if(x>threshold && yValues[j]==1){
				prPlus = prPlus + pValuesReal[j];
			}
			if(x<threshold && yValues[j]==-1){
				prMinus = prMinus + pValuesReal[j];
			}
			if(x<threshold && yValues[j]==1){
				pwPlus = pwPlus + pValuesReal[j];
				error = error + pValuesReal[j];
			}
			if(x>threshold && yValues[j]==-1){
				pwMinus = pwMinus + pValuesReal[j];
				error = error + pValuesReal[j];
			}
		}
		double g = Math.sqrt(prPlus*pwMinus)+Math.sqrt(pwPlus*prMinus);
		double[] hMore = {prPlus, prMinus, pwPlus, pwMinus, g, error};
		return hMore;
	}
	
	private double[] getC(double[] g){
		double prp = g[0];
		double prm = g[1];
		double pwp = g[2];
		double pwm = g[3];
		double e = Launch.e;
		double cTPlus = 0.5*Math.log((prp+e)/(pwm+e));
		double cTMinus = 0.5*Math.log((pwp+e)/(prm+e));
		double[] c = {cTPlus, cTMinus};
		return c;
	}
	
	private double[] getGT(double[] c, Divider divider1){
		double[] gt = new double[xValues.length];
		double threshold = divider1.threshold;
		String sign = divider1.sign;
		for(int i=0;i<xValues.length;i++){
			double x = xValues[i];
			if(sign!=null && sign.equals("<")){
				if(x<threshold){
					gt[i]=c[0];
				}
				else if(x>threshold){
					gt[i]=c[1];
				}
			}
			if(sign!=null && sign.equals(">")){
				if(x>threshold){
					gt[i]=c[0];
				}
				else if(x<threshold){
					gt[i]=c[1];
				}
			}
		}
		return gt;
	}
	
	private double[] getPValuesNewReal(double[] gt){
		double[] pValuesNewReal = new double[pValuesReal.length];
		for(int i=0;i<pValuesNewReal.length;i++){
			pValuesNewReal[i] = pValuesReal[i]*Math.exp(-(yValues[i]*gt[i]));
		}
		return pValuesNewReal;
	}
}
