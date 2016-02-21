package com.qwertyness.networktablesclient;

public class Values {
	public static double[] gyroPID = new double[3];
	public static double[] drivePID = new double[3];
	public static int platform;
	public static int targetPlatform;
	public static int defense;
	public static int action;
	
	public static String translateName(int value, int type) {
		if (type == 0) {
			switch(value) {
				case 1: return "Low Bar";
				case 2: return "Cheval de Fris";
				case 3: return "Moat";
				case 4: return "Ramparts";
				case 5: return "Drawbridge";
				case 6: return "Sally Port";
				case 7: return "Rock Wall";
				case 8: return "Rough Terain";
				case 9: return "Portcullis";
				default: return new Integer(value).toString();
			}
		}
		else {
			switch(value) {
				case 0: return "Stop.";
				case 1: return "U-turn.";
				case 2: return "Approach Tower";
				case 3: return "Approach and shoot.";
			}
		}
		return new Integer(value).toString();
	}
}
