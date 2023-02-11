package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;
import io.github.gabry98.app.filmapp.view.gui.ActorGUIView;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

@RunWith(GUITestRunner.class)
@DisplayName("Integration Tests for ActorGUIView.")
class ActorGUIViewIT extends AssertJSwingJUnitTestCase {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	private ActorGUIView actorView;
	private StreamingController streamingController;
	private ActorMongoRepository actorRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "actorCollection";
	
	private static final long TIMEOUT = 60000;
	
	@BeforeAll
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@BeforeEach
	public void setup() throws Exception {
		onSetUp();
	}
	
	@AfterEach
	public void tearDownClient() {
		onTearDown();
	}
	
	@AfterAll
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		actorRepository = new ActorMongoRepository(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		
		for(Actor actor : actorRepository.findAll()) {
			actorRepository.deleteActorById(actor.getId());
		}
		
		GuiActionRunner.execute(() -> {
			actorView = new ActorGUIView();
			streamingController = new StreamingController(actorRepository, null, null);
			actorView.setStreamingController(streamingController);
			return actorView;
		});
		
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), actorView);
		window.show(new Dimension(450,300));
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	@DisplayName("Test when we find all actors in the ActorRepository.")
	void testFindAllActors() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Andrea Verdi");
		streamingController.searchAllActors(actorView);
		pause(
			new Condition("findList must be not empty") {
				@Override
				public boolean test() {
					return !(window.list("findList").contents().length == 0);
				}
			}
		,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("1,Marco Rossi","2,Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test when we find an actor by id in the ActorRepository.")
	void testFindActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Andrea Verdi");
		streamingController.searchActorById(actorView, 1);
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).containsExactly("1,Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we find an actor by name in the ActorRepository.")
	void testFindActorByName() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Andrea Verdi");
		streamingController.searchActorsByName(actorView, "Andrea Verdi");
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).containsExactly("2,Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test when we add an actor to the view with success.")
	void testAddActorSuccess() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).containsExactly("1,Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we add an actor to the view with an error.")
	void testAddActorError() {
		actorRepository.addActor(1, "Andrea Verdi");
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).isEmpty();
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we delete an actor from the view by id with success.")
	void testDeleteActorByIdSuccess() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("actorsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we delete an actor from the view by name with success.")
	void testDeleteActorByNameSuccess() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("actorsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update an actor from the view by id with success.")
	void testUpdateActorByIdSuccess() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorId").enterText("2");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).containsExactly("2,Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we update an actor from the view by id with an error.")
	void testUpdateActorByIdError() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		actorRepository.addActor(2, "Andrea Verdi");
		window.textBox("actorId").enterText("2");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("actorsList").contents().length == 0)
								&& !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).containsExactly("1,Marco Rossi");
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 2 already exists!");
	}
	
	@Test
	@DisplayName("Test when we update an actor from the view by name with success.")
	void testUpdateActorByNameSuccess() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorName").enterText("Andrea Verdi");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).containsExactly("1,Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test when we update an actor from the view by name with an error.")
	void testUpdateActorByNameError() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorName").enterText("Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("actorsList").contents().length == 0)
								&& !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("actorsList").contents()).containsExactly("1,Marco Rossi");
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: you choose the same current name of the Actor!");
	}
	

}
