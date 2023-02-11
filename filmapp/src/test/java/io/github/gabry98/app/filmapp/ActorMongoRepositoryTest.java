package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetSocketAddress;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.repository.ActorRepository;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for Actors Repository")
class ActorMongoRepositoryTest {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient client;
	private ActorRepository actorRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "actorCollection";
	
	@BeforeAll
	public void setUpBeforeAll() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterAll
	public void tearDownAfterAll() {
		server.shutdown();
	}
	
	@BeforeEach
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		actorRepository = new ActorMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() {
		client.close();
	}

	@Test
	@DisplayName("Test Find All Method when the DB is Empty.")
	void testFindAllWithEmptyDatabase() {
		assertThat(actorRepository.findAll()).isEmpty();
	}

	@DisplayName("Test Find All Method when the DB is not Empty.")
	@ParameterizedTest
	@CsvSource({
        "1,Marco Rossi",
        "2,Andrea Verdi"
    })
	void testFindAllWithSomeActors(int id, String name) {
		actorRepository.addActor(id, name);
		List<Actor> actors = actorRepository.findAll();
		assertThat(actors).isNotNull();
		assertThat(actors.get(0).getId()).isEqualTo(id);
		assertThat(actors.get(0).getName()).isEqualTo(name);
	}
	
	@Test
	@DisplayName("Test adding an Actor with a duplicate ID.")
	void testAddActorWithDuplicateId() {
		actorRepository.addActor(1, "Marco Rossi");
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> actorRepository.addActor(1, "Andrea Verdi"));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test adding an Actor with a non positive ID.")
	void testAddActorWithNonPositiveId() {
		IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> actorRepository.addActor(-1, "Andrea Verdi"));
		IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> actorRepository.addActor(0, "Andrea Verdi"));
		assertThat(thrown1.getMessage()).isEqualTo(thrown2.getMessage()).isEqualTo("ERROR: id should be positive!");
	}
	
	@Test
	@DisplayName("Test adding an Actor with an empty string for the Name.")
	void testAddActorWithEmptyStringForName() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> actorRepository.addActor(1, ""));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Name of the actor must not be an empty string!");
	}
	
	@Test
	@DisplayName("Test the search of Actors by correct ID.")
	void testFindByIdMethodWithSomeActor() {
		actorRepository.addActor(1, "Marco Rossi");
		Actor a = actorRepository.findById(1);
		assertThat(a).isNotNull();
		assertThat(a.getId()).isEqualTo(1);
		assertThat(a.getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test the search of Actors by wrong ID.")
	void testFindByIdMethodWithSomeActorAndWrongID() {
		actorRepository.addActor(1, "Marco Rossi");
		Actor a = actorRepository.findById(2);
		assertThat(a).isNull();
	}
	
	@Test
	@DisplayName("Test the search of Actors by correct Name and with two homonymous.")
	void testFindByNameMethodWithTwoHomonymous() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Marco Rossi");
		List<Actor> actors = actorRepository.findByName("Marco Rossi");
		assertThat(actors).isNotEmpty();
		int actorsNumber = actors.size();
		assertThat(actorsNumber).isEqualTo(2);
		assertThat(actors.get(0).getId()).isEqualTo(1);
		assertThat(actors.get(1).getId()).isEqualTo(2);
		assertThat(actors.get(0).getName()).isEqualTo(actors.get(1).getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test the search of Actors by wrong Name.")
	void testFindByIdMethodWithSomeActorAndWrongName() {
		actorRepository.addActor(1, "Marco Rossi");
		List<Actor> a = actorRepository.findByName("Andrea Verdi");
		assertThat(a).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct update of the ID of an Actor.")
	void testUpdateActorIdCorrectly() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.updateActorId(1,2);
		Actor a = actorRepository.findById(2);
		assertThat(a.getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test the update of the ID of an Actor with a value already present.")
	void testUpdateActorIdWrongly() {
		actorRepository.addActor(1, "Marco Rossi");
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> actorRepository.updateActorId(1,1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test the correct update of the Name of an Actor.")
	void testUpdateActorNameCorrectly() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.updateActorName(1,"Andrea Verdi");
		Actor a = actorRepository.findById(1);
		assertThat(a.getName()).isEqualTo("Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test the update of the Name of an Actor with an empty string.")
	void testUpdateActorNameWithEmptyString() {
		actorRepository.addActor(1, "Marco Rossi");
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> actorRepository.updateActorName(1,""));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Name of the actor must not be an empty string!");
	}
	
	@Test
	@DisplayName("Test the update of the Name of an Actor with a string which already exists.")
	void testUpdateActorNameWithAlreadyExistingString() {
		actorRepository.addActor(1, "Marco Rossi");
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> actorRepository.updateActorName(1,"Marco Rossi"));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: you choose the same current name of the Actor!");
	}
	
	@Test
	@DisplayName("Test the correct deletion of an Actor by ID.")
	void testDeleteActorByIdCorrectly() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.deleteActorById(1);
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of some Actors by a specific name.")
	void testDeleteActorsByNameCorrectly() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Marco Rossi");
		actorRepository.deleteActorsByName("Marco Rossi");
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of an Actor by a specific name.")
	void testDeleteOneActorByNameCorrectly() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Andrea Verdi");
		actorRepository.deleteActorsByName("Marco Rossi");
		List<Actor> actors = actorRepository.findAll();
		int actorsNumber = actors.size();
		assertThat(actorsNumber).isEqualTo(1);
		assertThat(actors.get(0).getId()).isEqualTo(2);
		assertThat(actors.get(0).getName()).isEqualTo("Andrea Verdi");
	}

}
