package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;

@RunWith(GUITestRunner.class)
@DisplayName("E2E Tests for ActorGUIView.")
class ActorGUIViewE2E extends AssertJSwingJUnitTestCase {
	
	private static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private static final String DB_NAME = "project-db";
	
	private static final String ACTORS_COLLECTION_NAME = "actorCollection";
	
	private MongoClient mongoClient;
	
	private FrameFixture actorView;
	
	private static final long TIMEOUT = 60000;
	
	@BeforeEach
	public void setup() throws Exception {
		onSetUp();
	}

	@Override
	protected void onSetUp() throws Exception {
		mongo.start();
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.getDatabase(DB_NAME).drop();
		addActorToTheDatabase(1,"Marco Rossi");
		application("io.github.gabry98.app.filmapp.StreamingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-actor-collection=" + ACTORS_COLLECTION_NAME
			).start();

		actorView = WindowFinder.findFrame("Actor View")
				.using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
	}
	
	private void addActorToTheDatabase(int id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(ACTORS_COLLECTION_NAME)
			.insertOne(
					new Document()
						.append("id",id)
						.append("name",name));
	}

	@AfterEach
	@Override
	protected void onTearDown() {
		mongo.stop();
		actorView.cleanUp();
		mongoClient.close();
	}

	@Test
	@DisplayName("Test when we add an Actor with duplicate id.")
	void testAddActorWithDuplicateId() {
		testShowInitialActorsCorrectly();
		actorView.textBox("actorId").enterText("1");
		actorView.textBox("actorName").enterText("Marco Rossi");
		actorView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !actorView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we delete an Actor by id correctly.")
	void testDeleteActorByIdCorrectly() {
		testShowInitialActorsCorrectly();
		addActor();
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(actorView.list("actorsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we delete an Actor by name correctly.")
	void testDeleteActorByNameCorrectly() {
		testShowInitialActorsCorrectly();
		addActor();
		actorView.textBox("actorName").enterText("Andrea Verdi");
		actorView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(actorView.list("actorsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,3,'3,Andrea Verdi'",
        "actorName,Federico Marroni,'2,Federico Marroni'"
    })
	@DisplayName("Test when we update an Actor by id and name correctly.")
	void testUpdateActorByIdAndNameCorrectly(String field, String value, String list) {
		testShowInitialActorsCorrectly();
		addActor();
		actorView.textBox(field).enterText(value);
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly(list);
	}
	
	@Test
	@DisplayName("Test when we update an Actor with duplicated id.")
	void testUpdateActorWithDuplicatedId() {
		testShowInitialActorsCorrectly();
		addActor();
		actorView.textBox("actorId").enterText("2");
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0)
								&& !actorView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("2,Andrea Verdi");
		assertThat(actorView.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 2 already exists!");
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,2",
        "actorName,Andrea Verdi"
    })
	@DisplayName("Test when we find an Actor by id and name correctly.")
	void testFindActorByIdAndNameCorrectly(String field, String value) {
		testShowInitialActorsCorrectly();
		addActor();
		actorView.textBox(field).enterText(value);
		actorView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		
		assertThat(actorView.list("findList").contents()).containsExactly("2,Andrea Verdi");
		
		
	}

	private void addActor() {
		actorView.textBox("actorId").enterText("2");
		actorView.textBox("actorName").enterText("Andrea Verdi");
		actorView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("2,Andrea Verdi");
	}
	
	private void testShowInitialActorsCorrectly() {
		actorView.moveToFront();
		actorView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("findList").contents()).containsExactly("1,Marco Rossi");
	}


}
