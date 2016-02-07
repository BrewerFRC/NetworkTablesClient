package com.qwertyness.networktablesclient;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient {
	private static NetworkTable table;
	private static PrintStream out;
	private static boolean diagnostic = false;
	private static boolean diagnosticReady = false;

	public static void main(String[] args) {
		out = System.out;
		try {
			System.setOut(new PrintStream(File.createTempFile("log", "Catcher")));
		} catch (IOException e) {}
		Scanner scanner = new Scanner(System.in);
		NetworkTable.setClientMode();
		
		out.println("Input IPv4 Address or mDNS");
		String ip = scanner.nextLine();
		out.println(ip);
		if (ip.split("\\.").length != 4 && !ip.contains(".local")) {
			out.println("Invalid hostname.  Defaulted to roboRIO-4564-FRC.local");
			ip = "roboRIO-4564-FRC.local";
		}
		NetworkTable.setIPAddress(ip);
		
		table = NetworkTable.getTable("dashTable");
		if (table == null) {
			out.println("Failed to connect to robot.  Is the IP address correct?");
			scanner.close();
			return;
		}
		
		startPushThread();
		while(true) {
			String[] command = scanner.nextLine().split(" ");
			if (command.length <= 1) {
				if (command[0].equalsIgnoreCase("exit")) {
					out.print("Closing...");
					scanner.close();
					return;
				}
				if (command[0].equalsIgnoreCase("getpid")) {
					out.println("Current Values (P, I, D):");
					out.println("Gyro: " + Values.gyroPID[0] + ", " + Values.gyroPID[1] + ", " + Values.gyroPID[2]);
					out.println("Drive: " + Values.drivePID[0] + ", " + Values.drivePID[1] + ", " + Values.drivePID[2]);
				}
				else if (command[0].equalsIgnoreCase("diag")) {
					if (diagnosticReady) {
						diagnostic = !diagnostic;
						out.println("Set diagnostic mode to " + diagnostic);
					}
					else {
						out.println("NetworkTables is not ready for diagnostic mode. Make sure the robot is " +
								"properly connected.");
					}
				}
				else {
					out.print("Invalid Command!");
				}
			}
			switch(command[0]) {
				case "gp":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.gyroPID[0] = Double.parseDouble(command[1]);
					out.println("Set P value to " + Values.gyroPID[0]);
					out.println("Current Values (P, I, D):");
					out.println("Gyro: " + Values.gyroPID[0] + ", " + Values.gyroPID[1] + ", " + Values.gyroPID[2]);
					break;
				case "gi":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.gyroPID[1] = Double.parseDouble(command[1]);
					out.println("Set I value to " + Values.gyroPID[1]);
					out.println("Current Values (P, I, D):");
					out.println("Gyro: " + Values.gyroPID[0] + ", " + Values.gyroPID[1] + ", " + Values.gyroPID[2]);
					break;
				case "gd":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.gyroPID[2] = Double.parseDouble(command[1]);
					out.println("Set D value to " + Values.gyroPID[2]);
					out.println("Current Values (P, I, D):");
					out.println("Gyro: " + Values.gyroPID[0] + ", " + Values.gyroPID[1] + ", " + Values.gyroPID[2]);
					break;
				case "dp":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.drivePID[0] = Double.parseDouble(command[1]);
					out.println("Set P value to " + Values.drivePID[0]);
					out.println("Current Values (P, I, D):");
					out.println("Drive: " + Values.drivePID[0] + ", " + Values.drivePID[1] + ", " + Values.drivePID[2]);
					break;
				case "di":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.drivePID[1] = Double.parseDouble(command[1]);
					out.println("Set I value to " + Values.drivePID[1]);
					out.println("Current Values (P, I, D):");
					out.println("Drive: " + Values.drivePID[0] + ", " + Values.drivePID[1] + ", " + Values.drivePID[2]);
					break;
				case "dd":
					if (!diagnostic) {
						out.println("You must be in diagnostic mode to use this command.");
						break;
					}
					Values.drivePID[2] = Double.parseDouble(command[1]);
					out.println("Set D value to " + Values.drivePID[2]);
					out.println("Current Values (P, I, D):");
					out.println("Drive: " + Values.drivePID[0] + ", " + Values.drivePID[1] + ", " + Values.drivePID[2]);
					break;
				case "p":
					Values.platform = Integer.parseInt(command[1]);
					out.println("Set starting platform to " + Values.platform);
					break;
				case "tp":
					Values.targetPlatform = Integer.parseInt(command[1]);
					out.println("Set target platform to " + Values.targetPlatform);
					break;
				case "d":
					Values.defense = Integer.parseInt(command[1]);
					out.println("Set defense type to " + Values.defense);
					break;
				case "a":
					Values.action = Integer.parseInt(command[1]);
					out.println("Set selected action to " + Values.action);
					break;
			}
		}
	}
	
	public static void startPushThread() {
		new PushThread();
	}
	
	public static class PushThread implements Runnable {
		public Thread thread;
		
		public PushThread() {
			thread = new Thread(this, "pushThread");
			thread.start();
		}
		
		public void run() {
			table.putNumber("platform", Values.platform);
			table.putNumber("targetPlatform", Values.targetPlatform);
			table.putNumber("defense", Values.defense);
			table.putNumber("action", Values.action);
			
			if (diagnosticReady) {
				if (!diagnostic) {
					return;
				}
				table.putNumber("gyroP", Values.gyroPID[0]);
				table.putNumber("gyroI", Values.gyroPID[1]);
				table.putNumber("gyroD", Values.gyroPID[2]);
				table.putNumber("driveP", Values.drivePID[3]);
				table.putNumber("driveI", Values.drivePID[4]);
				table.putNumber("driveD", Values.drivePID[5]);
			}
			else {
				Values.gyroPID[0] = table.getNumber("gyroP", 0.0);
				Values.gyroPID[1] = table.getNumber("gyroI", 0.0);
				Values.gyroPID[2] = table.getNumber("gyroD", 0.0);
				Values.drivePID[0] = table.getNumber("driveP", 0.0);
				Values.drivePID[1] = table.getNumber("driveI", 0.0);
				Values.drivePID[2] = table.getNumber("driveD", 0.0);
				if (Values.gyroPID[0] != 0) {
					diagnosticReady = true;
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
			if (thread.isAlive()) {
				run();
			}
		}
	}
}