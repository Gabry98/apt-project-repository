package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for Actor Repository.")
@Testcontainers
class ActorMongoRepositoryIT {

	@Container
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "actorCollection";
	
	private MongoClient client;
	private ActorMongoRepository actorRepository;
	
	@BeforeEach
	public void setup() {
		
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017))
		);
		
		actorRepository = new ActorMongoRepository(client,DATABASE_NAME,COLLECTION_NAME);
		
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test
	@DisplayName("Test when we add an Actor.")
	void testAddActor() {
		actorRepository.addActor(1, "Marco Rossi");
		Actor a = actorRepository.findById(1);
		assertThat(a.getId()).isEqualTo(1);
		assertThat(a.getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we remove an Actor by id.")
	void testRemoveActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.deleteActorById(1);
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove an Actor by name.")
	void testRemoveActorByName() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.deleteActorsByName("Marco Rossi");
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update an Actor by id.")
	void testUpdateActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.updateActorId(1,2);
		Actor a = actorRepository.findById(2);
		assertThat(a.getId()).isEqualTo(2);
		assertThat(a.getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we update an Actor by name.")
	void testUpdateActorByName() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.updateActorName(1, "Mauro Rossi");
		List<Actor> actors = actorRepository.findByName("Mauro Rossi");
		assertThat(actors.get(0).getId()).isEqualTo(1);
		assertThat(actors.get(0).getName()).isEqualTo("Mauro Rossi");
	}
}
