package com.qwertyness.networktablesclient;

import java.util.Scanner;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient {
	/*
	 * 0 - P
	 * 1 - I
	 * 2 - D
	 */
	private static double[] values;
	private NetworkTable table;

	public static void main(String[] args) {
		values = new double[3];
		Scanner scanner = new Scanner(System.in);
		NetworkTable.setClientMode();
		System.out.println("Input IPv4 Address");
		String ip = scanner.nextLine();
		System.out.println(ip);
		if (ip.split("\\.").length != 4 && !ip.contains(".local")) {
			System.out.println("Invalid IP.  Defaulted to roboRIO-4564-FRC.local");
			ip = "roboRIO-4564-FRC.local";
		}
		NetworkTable.setIPAddress(ip);
		NetworkTable table = NetworkTable.getTable("dashTable");
		if (table == null) {
			System.out.println("Failed to connect to robot.  Is the IP address correct?");
			scanner.close();
			return;
		}
		values[0] = table.getNumber("gyroP", 0.0);
		values[1] = table.getNumber("gyroI", 0.0);
		values[2] = table.getNumber("gyroD", 0.0);
		System.out.println("Starting Gyro PID Values (P, I, D):");
		System.out.println(values[0] + ", " + values[1] + ", " + values[2]);
		
		while(true) {
			String[] command = scanner.nextLine().split(" ");
			if (command.length <= 1) {
				if (command[0].equalsIgnoreCase("exit")) {
					System.out.print("Closing...");
					scanner.close();
					return;
				}
				System.out.print("Invalid Command!");
			}
			switch(command[0]) {
				case "p":
					values[0] = Double.parseDouble(command[1]);
					System.out.println("Set P value to " + values[0]);
					System.out.println("Current Values (P, I, D):");
					System.out.println(values[0] + ", " + values[1] + ", " + values[2]);
					break;
				case "i":
					values[1] = Double.parseDouble(command[1]);
					System.out.println("Set I value to " + values[1]);
					System.out.println("Current Values (P, I, D):");
					System.out.println(values[0] + ", " + values[1] + ", " + values[2]);
					break;
				case "d":
					values[2] = Double.parseDouble(command[1]);
					System.out.println("Set D value to " + values[2]);
					System.out.println("Current Values (P, I, D):");
					System.out.println(values[0] + ", " + values[1] + ", " + values[2]);
					break;
			}
			table.putNumber("gyroP", values[0]);
			table.putNumber("gyroI", values[1]);
			table.putNumber("gyroD", values[2]);
		}
	}
}