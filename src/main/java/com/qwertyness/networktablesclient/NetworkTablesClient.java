package com.qwertyness.networktablesclient;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient {
	private static NetworkTable table;
	private static PrintStream out;
	private static boolean diagnostic = false;
	private static boolean diagnosticReady = false;

	public static void main(String[] args) {
		out = System.out;
		Scanner scanner = new Scanner(System.in);
		NetworkTable.setClientMode();
		
		out.println("Press enter or input alternate IP address.");
		String ip = scanner.nextLine();
		if (ip.split("\\.").length != 4 && !ip.contains(".local")) {
			out.println("Invalid hostname.  Defaulted to roboRIO-4564-FRC.local");
			ip = "roboRIO-4564-FRC.local";
			out.println("Set hostname to mDNS roboRIO-4564-FRC.local");
		}
		NetworkTable.setIPAddress(ip);
		
		table = NetworkTable.getTable("dashTable");
		if (table == null) {
			out.println("Failed to connect to robot.  Is the IP address correct?");
			scanner.close();
			return;
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {}
		startPushThread();
		while(true) {
			out.print(">");
			String commandAvecArgs = scanner.nextLine();
			if (commandAvecArgs.length() < 1) {
				sendAutonomousInformation();
				continue;
			}
			String[] command = {commandAvecArgs.substring(0, 1), commandAvecArgs.substring(1)};
			if (command.length <= 1) {
				if (commandAvecArgs.contains("getpid")) {
					out.println("Current Values (P, I, D):");
					out.println("Gyro: " + Values.gyroPID[0] + ", " + Values.gyroPID[1] + ", " + Values.gyroPID[2]);
					out.println("Drive: " + Values.drivePID[0] + ", " + Values.drivePID[1] + ", " + Values.drivePID[2]);
				}
				else if (commandAvecArgs.contains("diag")) {
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
					sendAutonomousInformation();
				}
			}
			try {
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
				case "s":
					Values.platform = Integer.parseInt(command[1]);
					if (Values.platform < 0 || Values.platform > 5) {
						Values.platform = 0;
						out.println("Invalid input number.  Defaulted to platform 0.");
					}
					out.println("Set starting platform to " + Values.platform);
					sendAutonomousInformation();
					break;
				case "t":
					Values.targetPlatform = Integer.parseInt(command[1]);
					if (Values.targetPlatform < 0 || Values.targetPlatform > 5) {
						Values.targetPlatform = 0;
						out.println("Invalid input number.  Defaulted to platform 0.");
					}
					out.println("Set target platform to " + Values.targetPlatform);
					sendAutonomousInformation();
					break;
				case "d":
					Values.defense = Integer.parseInt(command[1]);
					if (Values.defense < 0 || Values.defense > 9) {
						Values.defense = 0;
						out.println("Invalid input number.  Defaulted to defense 0.");
					}
					out.println("Set defense type to " + Values.translateName(Values.defense, 0));
					sendAutonomousInformation();
					break;
				case "a":
					Values.action = Integer.parseInt(command[1]);
					if (Values.action < 0 || Values.action > 3) {
						Values.action = 0;
						out.println("Invalid input number.  Defaulted to action 0.");
					}
					out.println("Set selected action to " + Values.translateName(Values.action, 1));
					sendAutonomousInformation();
					break;
				default:
					sendAutonomousInformation();
					break;
			}
			} catch(NumberFormatException e) {
				out.println("Argument must be a valid number.");
			}
		}
	}
	
	public static void sendAutonomousInformation() {
		out.println();
		out.println("----- Current Autonomous Selections -----");
		out.println("s - Starting Platform: " + Values.platform);
		out.println("d - Defense: " + Values.translateName(Values.defense, 0));
		out.println("a - Action: " + Values.translateName(Values.action, 1));
		out.println("t - Target Platform: " + Values.targetPlatform);
		out.println("-----------------------------------------");
		out.println();
	}
	
	public static void startPushThread() {
		new PushThread();
	}
	
	public static class PushThread extends TimerTask {
		
		public PushThread() {
			new Timer().scheduleAtFixedRate(this, 0, 1000);
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
				table.putNumber("driveP", Values.drivePID[0]);
				table.putNumber("driveI", Values.drivePID[1]);
				table.putNumber("driveD", Values.drivePID[2]);
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
			run();
		}
	}
}