package com.ml.project.nearestneighbour;
import java.io.IOException;

public class Launch {
	
	static String inputCrossValidFilePath = "input_cross_validation_1.txt";
	static String inputDataFilePath = "input_data_1.txt";
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		System.out.println("***********************************");
		for(int nn=1;nn<=5;nn++){
			DataModel dataModel = new DataModel(inputCrossValidFilePath, inputDataFilePath);
			Utilities utilities = new Utilities();
			System.out.println("For nn="+nn+", the values are:");
			System.out.println("e: "+utilities.getError(dataModel, nn));
			System.out.println("sigma: "+utilities.getStandardDeviation());
			System.out.println("Grid: \n"+utilities.getNewGrid(dataModel, nn));
			System.out.println("***********************************");
		}
	}
}