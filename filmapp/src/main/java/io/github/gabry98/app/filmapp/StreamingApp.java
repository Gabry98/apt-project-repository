package io.github.gabry98.app.filmapp;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.jacoco.Generated;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.ReportMongoRepository;
import io.github.gabry98.app.filmapp.view.gui.ActorGUIView;
import io.github.gabry98.app.filmapp.view.gui.FilmGUIView;
import io.github.gabry98.app.filmapp.view.gui.ReportGUIView;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
@Generated
public class StreamingApp implements Callable<Void> {
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "project-db";
	
	@Option(names = { "--db-actor-collection" }, description = "Actors Collection name")
	private String actorsCollection = "actorCollection";
	
	@Option(names = { "--db-film-collection" }, description = "Films Collection name")
	private String filmsCollection = "filmCollection";
	
	@Option(names = { "--db-report-collection" }, description = "Reports Collection name")
	private String reportsCollection = "reportCollection";
	
	private static final Logger LOGGER = LogManager.getLogger(StreamingApp.class);

	public static void main(String[] args) {
		new CommandLine(new StreamingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				
				ActorMongoRepository actorRepository 
					= new ActorMongoRepository(client, databaseName, actorsCollection);
				FilmMongoRepository filmRepository 
					= new FilmMongoRepository(client, databaseName, filmsCollection);
				ReportMongoRepository reportRepository 
					= new ReportMongoRepository(client,databaseName,reportsCollection);
				
				StreamingController streamingController 
					= new StreamingController(actorRepository,filmRepository,reportRepository);
				
				ActorGUIView actorView = new ActorGUIView();
				FilmGUIView filmView = new FilmGUIView();
				ReportGUIView reportView = new ReportGUIView();
				
				actorView.setStreamingController(streamingController);
					actorView.setVisible(true);
				filmView.setStreamingController(streamingController);
					filmView.setVisible(true);
				reportView.setStreamingController(streamingController);
					reportView.setVisible(true);
			} catch(Exception e) {
				LOGGER.error("An Exception has been thrown: {}", e.getMessage());
			}
		});
		return null;
	}

}
