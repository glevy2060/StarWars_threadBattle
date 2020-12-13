package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	public static Input jsonReader(String path){
		Gson gson = new Gson();
		Input in=null;
		try (Reader reader = new FileReader(path)) {
			in = gson.fromJson(reader, Input.class);
		}
		catch (Exception e){
			System.out.println("there is no such json file");
		}
		return in;
	}



	public static void main(String[] args) {
		Input in=jsonReader(args[0]);
		Attack[] attacksFromJson=in.getAttacks();
		int numOfEwoks=in.getEwoks();
		Ewoks ewoks= Ewoks.getInstance();
		for(int i=0;i<numOfEwoks;i++)//initialize ewok and add it to the list
			ewoks.addEwok(new Ewok());

		Thread leia=new Thread(new LeiaMicroservice((attacksFromJson)));
		Thread hanSolo=new Thread(new HanSoloMicroservice());
		Thread cepo= new Thread(new C3POMicroservice());
		Thread r2d2=new Thread(new R2D2Microservice(in.getR2D2()));
		Thread lando=new Thread(new LandoMicroservice(in.getLando()));

		hanSolo.start();
		r2d2.start();
		cepo.start();
		lando.start();
		CountDownInit count=CountDownInit.getInstance();
		count.awaitCount();
		leia.start();

		try {
			cepo.join();
			leia.join();
			hanSolo.join();
			lando.join();
			r2d2.join();
		} catch (InterruptedException e) { }

		Diary d = Diary.getInstance();
		Writer writer = null;

		try {
			writer = new FileWriter(args[1]);
			Gson gson=new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(d ,writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}}
