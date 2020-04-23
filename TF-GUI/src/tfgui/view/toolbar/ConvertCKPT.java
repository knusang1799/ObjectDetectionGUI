package tfgui.view.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tfgui.controller.putty.runPutty;
import tfgui.controller.sshclient.SSHClient;
import tfgui.model.Model;
import tfgui.view.MainView;
import tfgui.view.right.RightUnderView;
/*
* Copyright 2019 The Block-AI-VIsion Authors. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ==========================================================================
*
* This file is part of Tensorflow GUI program under Block-AI-VIsion project.
*
* Made in University of Tasmania, Australia.
*
* @Authors : Dr.Mira Park (mira.park@utas.edu.au)
*	     Dr.Sanghee Lee (knusang1799@gmail.com)
*	     Heemoon Yoon (boguss1225@gmail.com)
*
* Date : Initial Development in 2019
*
* For the latest version, please check the github 
* (https://github.com/boguss1225/TF-GUI)
* 
* ==========================================================================
* Description : This program allows users to train models, configure settings,
*		detect objects and control image data within GUI level. 
*		This program converted almost every steps of Tensorflow model 
*		training into GUI so that user can easily utilize Tensorflow. 
*		To operate this program, server need to have preinstalled 
*		Tensorflow virtual environment and relevant script code.
*/
public class ConvertCKPT extends JDialog {
	private static final long serialVersionUID = 1L;

	ConvertCKPT(){
		/*redirect to training folder*/
		MainView.mainViewFrame.leftPane.showFolders("/home/"+Model.username+"/tensorflowGUI/"+Model.ActivatedEnv+"/models/research/object_detection/training");
		
		/*create new dialog*/
	 	this.setTitle("Convert CKPT to PB file");
	 	this.setVisible(true);
	 	this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 	
	 	/*set size of dialog*/
	 	this.setSize(250, 400);
	 	
	 	/*set location*/
	 	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int xpos = (int) (screen.getWidth() / 2 - this.getWidth() / 2);
		int ypos = (int) (screen.getHeight() / 2 - this.getHeight() / 2);
		this.setLocation(xpos, ypos);
	 	
	 	/*set layout*/
		this.setLayout(new BorderLayout());
	 	
	 	/*result pane*/
		/*ckpt pane*/
		JPanel ckptpane = new ckptlistpane();
	 	
		/*set button1*/
		JButton b1 = new JButton("OK");
		class b1EventHandler implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent ae){
				dispose();
			}}
		b1.addActionListener(new b1EventHandler());
		JPanel pb1 = new JPanel(new FlowLayout());
		pb1.add(b1);
		
		/*add components*/
		JPanel buttonPane = new JPanel();
		buttonPane.add(pb1);
		this.add(ckptpane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);
		this.setVisible(true);
	}
}

class ckptlistpane extends JPanel{
	private static final long serialVersionUID = 1L;
	private SSHClient sshclient;
	private JLabel selectedFilenamel;
	private JButton convertbt;
	
	ckptlistpane(){
		//set Model
		sshclient = Model.sshclient;

		//set Layout
		this.setLayout(new BorderLayout(3,3));
		
		//set selected file Label		
		selectedFilenamel = new JLabel("Not selected");
		selectedFilenamel.setForeground(Color.red);
		
		//get list of ckpt files
		String filelist = sshclient.sendCommand("cd ~/tensorflowGUI/"+ Model.ActivatedEnv +"/models/research/object_detection/training"+" && find . -maxdepth 1 -not -type d");
		String[] filename = filelist.split("\n");
		int numofFiles = filename.length;
		int cnt = 0;
		for (int i = 0 ; i < numofFiles ; i ++){
			if(filename[i].startsWith("./model.ckpt")&&filename[i].endsWith("index")){
				filename[cnt] = filename[i].substring(2, filename[i].lastIndexOf("."));
				cnt++;
			}
		}
		
		//set file list panel
		JPanel filelistpanel = new JPanel(new GridLayout(5,1));
		for (int i = 0 ; i < cnt ; i ++){
			JButton filenamebt = new JButton(filename[i]);
			class filenamebtEventHandler implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent ae){
					selectedFilenamel.setText(filenamebt.getText());
					selectedFilenamel.setForeground(Color.ORANGE);
					convertbt.setForeground(Color.ORANGE);
				}}
			filenamebt.addActionListener(new filenamebtEventHandler());
			filelistpanel.add(filenamebt);
		}
		   //fill empty cells for clean layout
		for(int i = 0 ; i < 5-cnt ; i++) 
			filelistpanel.add(new JLabel());
		
		//set selected File Name Panel
		JPanel selectedFileNamePanel = new JPanel(new FlowLayout());
		convertbt = new JButton("Convert-->");
		class convertbtEventHandler implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent ae){
				new convertdia(selectedFilenamel.getText());
			}}
		convertbt.addActionListener(new convertbtEventHandler());
		selectedFileNamePanel.add(convertbt);
		selectedFileNamePanel.add(selectedFilenamel);
		
		//add components
		JLabel lb = new JLabel("Select a file to convert to 'pb'");
		this.add(lb, BorderLayout.NORTH);
		this.add(filelistpanel,BorderLayout.CENTER);
		this.add(selectedFileNamePanel,BorderLayout.SOUTH);
	}
}

class convertdia{
	convertdia(String str){
		/*create new dialog*/
	 	JDialog Dia = new JDialog((JFrame)null,"convert 'ckpt' to 'pb' file",true);
	 	Dia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 	
	 	/*set size of dialog*/
	 	Dia.setSize(300, 100);
	 	
	 	/*set location*/
	 	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int xpos = (int) (screen.getWidth() / 2 - Dia.getWidth() / 2);
		int ypos = (int) (screen.getHeight() / 2 - Dia.getHeight() / 2);
		Dia.setLocation(xpos, ypos);
	 	
	 	/*set layout*/
	 	Dia.setLayout(new BorderLayout(3,3));
	 	
	 	/*get number*/
	 	String number = str.substring(str.lastIndexOf("-")+1,str.length());
	 	System.out.println(number);
	 	
	 	/*result pane*/
	 	JLabel infolabel = new JLabel(" Do you want to convert \n" + str + " to 'pb' file?");

		/*set button1*/
		JButton b1 = new JButton("OK");
		class b1EventHandler implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent ae){
				//close all step1 dialog
				String command = "cd /home/"+Model.username+"/tensorflowGUI/scripts "
						+ "&& bash converckpt.sh "
						+ Model.ActivatedEnv +" "+ number;
				
				//run Putty
				new runPutty(command);
				
				//update log on RightUnderView
				RightUnderView.updateCMDtxtField("**** Step4 'Convert ckpit file' executed ****\n");
				Dia.dispose();
			}}
		b1.addActionListener(new b1EventHandler());
		JPanel pb1 = new JPanel(new FlowLayout());
		pb1.add(b1);
		
		/*set button2*/
		JButton b2 = new JButton("Cancel");
		class b2EventHandler implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent ae){
				//close all step1 dialog
				Dia.dispose();
			}}
		b2.addActionListener(new b2EventHandler());
		JPanel pb2 = new JPanel(new FlowLayout());
		pb2.add(b2);
		
		/*add components*/
		JPanel buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(pb1);
		buttonPane.add(pb2);
		Dia.add(infolabel, BorderLayout.CENTER);
		Dia.add(buttonPane, BorderLayout.SOUTH);
		Dia.setVisible(true);
	}
}