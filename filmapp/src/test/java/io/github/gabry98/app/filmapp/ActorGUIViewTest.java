package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import java.awt.Dimension;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.view.gui.ActorGUIView;

@RunWith(GUITestRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for ActorGUIView")
class ActorGUIViewTest extends AssertJSwingJUnitTestCase {
	
	private FrameFixture window;
	private ActorGUIView actorView;
	private AutoCloseable closeable;
	
	@Mock
	private StreamingController streamingController;
	
	@BeforeEach
	public void setup() throws Exception {
		onSetUp();
	}
	
	@AfterEach
	public void teardown() throws Exception {
		onTearDown();
	}

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			closeable = MockitoAnnotations.openMocks(this);
			actorView = new ActorGUIView();
			actorView.setStreamingController(streamingController);
			return actorView;
		});
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), actorView);
		window.show(new Dimension(450,300));
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
		window.cleanUp();
	}
	
	@Test @GUITest
	@DisplayName("Test when we have all initial states in the view.")
	void testInitialStates() {
		window.label(JLabelMatcher.withText("Id:"));
		window.label(JLabelMatcher.withText("Name:"));
		window.label(JLabelMatcher.withText(""));
		window.textBox("actorId").requireEnabled();
		window.textBox("actorName").requireEnabled();
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.list("actorsList").requireEnabled();
		window.list("findList").requireEnabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the ADD button enabled.")
	void testAddButtonEnabled() {
		window.textBox("actorName").enterText("Marco Rossi");
		window.textBox("actorId").enterText("1");
		window.button(JButtonMatcher.withText("ADD")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorName,actorId",
        "actorId,actorName"
    })
	@DisplayName("Test the scenarios where we have the ADD button disabled for actorId and actorName.")
	void testAddButtonDisabledForFields(String field1, String field2) {
		window.textBox(field1).enterText(" ");
		window.textBox(field2).enterText(" ");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the REMOVE button enabled for actorId and actors list selection.")
	void testRemoveButtonEnabledForIdAndActorsList() {
		actorView.actorAdded(1, "Marco Rossi");
		window.textBox("actorId").enterText("1");
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
		window.textBox("actorName").enterText(" ");
		window.textBox("actorId").setText("");
		window.textBox("actorId").enterText(" ");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the REMOVE button disabled for actors list.")
	void testRemoveButtonDisabledForActorsList() {
		actorView.actorAdded(1, "Marco Rossi");
		window.textBox("actorId").enterText("1");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
		window.textBox("actorId").setText("");
		window.textBox("actorName").enterText(" ");
		window.list("actorsList").unselectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button enabled for actors list selection.")
	void testUpdateButtonEnabledForActorsListSelection() {
		actorView.actorAdded(1, "Marco Rossi");
		window.textBox("actorId").enterText("1");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
		window.textBox("actorId").setText("");
		window.textBox("actorName").enterText("Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorId,actorName,Marco Rossi, 1",
        "actorName,actorId,1, Marco Rossi"
    })
	@DisplayName("Test the scenarios where we have the UPDATE button enabled for actorId, actorName and actors list.")
	void testUpdateButtonEnabledForIdAndNameAndActorsList(String field1, String field2, String value1, String value2) {
		actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.textBox(field1).enterText(value1);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
		window.textBox(field2).enterText(value2);
		window.textBox(field1).setText("");
		window.textBox(field1).enterText(" ");
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorId,actorName,Marco Rossi",
        "actorName,actorId,1"
    })
	@DisplayName("Test the scenarios where we have the UPDATE button disabled for actorId, actorName and actors list.")
	void testUpdateButtonDisabledForIdAndNameAndActorsList(String field1, String field2, String value) {
		actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.textBox(field1).enterText(" ");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.list("actorsList").unselectItems(0);
		window.textBox(field2).enterText(value);
		window.textBox(field1).setText("");
		window.textBox(field1).enterText(" ");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}

	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button disabled for empty actorId and actorName.")
	void testUpdateButtonDisabledForIdAndName() {
		actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button disabled for actors list.")
	void testUpdateButtonDisabledForActorsList() {
		actorView.actorAdded(1, "Marco Rossi");
		window.textBox("actorId").enterText("1");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.textBox("actorId").setText("");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we add an actor correctly.")
	void testAddActorCorrectly() {
		window.textBox("actorId").enterText("1");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorId").requireText("");
		window.textBox("actorName").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		verify(streamingController).addActor(actorView, 1, "Marco Rossi");
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we add an actor with an exception.")
	void testAddActorWithException() {
		doThrow(new IllegalArgumentException("ERROR: id should be positive!")).
			when(streamingController).addActor(actorView, 0, "Marco Rossi");
		window.textBox("actorId").enterText("0");
		window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("actorId").requireText("");
		window.textBox("actorName").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: id should be positive!");
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we add an actor by calling explicitly the method of the view.")
	void testActorAdded() {
		actorView.actorAdded(1, "Marco Rossi");
		assertThat(window.list("actorsList").contents()).contains("1,Marco Rossi");
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorId,2",
        "actorName,Mauro Rossi"
    })
	@DisplayName("Test the scenarios where we update an actor by actorName and actorId.")
	void testActorUpdatedByNameAndId(String field, String value) {
		actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.textBox(field).enterText(value);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox(field).requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		if(field.equals("actorId"))
			verify(streamingController).updateActor(actorView, 1, Integer.parseInt(value), 0);
		else
			verify(streamingController).updateActor(actorView, 1, value, 0);
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we update an actor with an Exception.")
	void testActorUpdatedWithException() {
		doThrow(new IllegalArgumentException("ERROR: Actor with id 1 already exists!")).
			when(streamingController).updateActor(actorView, 1, 1, 0);
			actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		window.textBox("actorId").enterText("1");
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox("actorId").requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: Actor with id 1 already exists!");
		
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "2,Marco Rossi",
        "1,Mauro Rossi"
    })
	@DisplayName("Test the scenarios where we update an actor correctly by actorName and actorId.")
	void testActorUpdatedCorrectlyByIdAndName(int id, String name) {
		actorView.actorAdded(1, "Marco Rossi");
		window.list("actorsList").selectItems(0);
		if(id == 2) 
			actorView.actorUpdated(1, id, 0);
		else
			actorView.actorUpdated(1, name, 0);
		assertThat(window.list("actorsList").contents()).contains(id+","+name);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorId",
        "actorName"
    })
	@DisplayName("Test the scenarios where we remove an actor by actorName and actorId.")
	void testActorRemovedByIdAndName(String field) {
		actorView.actorAdded(1, "Marco Rossi");
		if(field.equals("actorId"))
			window.list("actorsList").selectItems(0);
		else
			window.textBox("actorName").enterText("Marco Rossi");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		window.textBox(field).requireText("");
		if(field.equals("actorId"))
			verify(streamingController).deleteActor(actorView, 1, 0);
		else
			verify(streamingController).deleteActor(actorView, "Marco Rossi");
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we remove an actor by id, by calling explicitly the method of the view.")
	void testActorRemovedByIdFromActorsList() {
		actorView.actorAdded(1, "Marco Rossi");
		actorView.actorDeletedById(0);
		assertThat(window.list("actorsList").contents()).isEmpty();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we remove an actor by name, by calling explicitly the method of the view.")
	void testActorRemovedByNameFromActorsList() {
		Actor a = new Actor(1, "Marco Rossi");
		List<Actor> actors = List.of(a);

		actorView.actorAdded(1, "Marco Rossi");
		actorView.actorsDeletedByName(actors);
		assertThat(window.list("actorsList").contents()).isEmpty();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we find all actors in our list.")
	void testAllActorsFound() {
		actorView.actorAdded(1, "Marco Rossi");
		actorView.actorAdded(2, "Andrea Verdi");
		actorView.actorAdded(3, "Federico Bianchi");
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox("actorId").requireText("");
		verify(streamingController).searchAllActors(actorView);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
        "actorId,2",
        "actorName,Marco Rossi"
    })
	@DisplayName("Test the scenarios where we find the actors in our list by actorId and actorName.")
	void testActorFoundByIdAndName(String field, String value) {
		actorView.actorAdded(1, "Marco Rossi");
		actorView.actorAdded(2, "Andrea Verdi");
		actorView.actorAdded(3, "Federico Bianchi");
		window.textBox(field).enterText(value);
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox(field).requireText("");
		if(field.equals("actorId"))
			verify(streamingController).searchActorById(actorView, 2);
		else
			verify(streamingController).searchActorsByName(actorView, "Marco Rossi");
	}
	
	@Test @GUITest
	@DisplayName("Test the scenarios where we find the actors in our list by calling explicitly the method of the view.")
	void testActorFoundByCallingViewMethod() {
		actorView.actorAdded(1, "Marco Rossi");
		actorView.actorAdded(2, "Andrea Verdi");
		actorView.actorAdded(3, "Federico Bianchi");
		actorView.actorFound(1, "Marco Rossi");
		assertThat(window.list("findList").contents()).contains("1,Marco Rossi");
	}

}
