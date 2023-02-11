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
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.view.gui.FilmGUIView;

@RunWith(GUITestRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for FilmGUIView")
class FilmGUIViewTest extends AssertJSwingJUnitTestCase {
	
	private FrameFixture window;
	private FilmGUIView filmView;
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
			filmView = new FilmGUIView();
			filmView.setStreamingController(streamingController);
			return filmView;
		});
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), filmView);
		window.show(new Dimension(450,300));
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
		window.cleanUp();
	}
	
	@Test @GUITest
	@DisplayName("Test when we have all initial states of the view.")
	void testInitialStates() {
		window.label(JLabelMatcher.withText("Id:"));
		window.label(JLabelMatcher.withText("Name:"));
		window.label(JLabelMatcher.withText("Date:"));
		window.label(JLabelMatcher.withText(""));
		window.textBox("filmId").requireEnabled();
		window.textBox("filmName").requireEnabled();
		window.textBox("filmDate").requireEnabled();
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.list("filmsList").requireEnabled();
		window.list("findList").requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmName,filmDate,filmId,King Kong,1950,1",
		"filmName,filmId,filmDate,King Kong,1,1950",
		"filmDate,filmId,filmName,1950,1,King Kong"
    })
	@DisplayName("Test all scenarios where we have the ADD button enabled for fields.")
	void testAddButtonEnabledForFields(String field1, String field2, String field3, String value1, String value2, String value3) {
		window.textBox(field1).enterText(value1);
		window.textBox(field2).enterText(value2);
		window.textBox(field3).enterText(value3);
		window.button(JButtonMatcher.withText("ADD")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"'','',1",
        "'',1955,''",
        "'',1955,1",
        "King Kong,'',''",
        "King Kong,'',1",
        "King Kong,1955,''",
    })
	@DisplayName("Test all scenarios where we have the ADD button disabled for filmId.")
	void testAddButtonDisabledForId(String filmName, String filmDate, String filmId) {
		window.textBox("filmName").enterText(filmName);
		window.textBox("filmDate").enterText(filmDate);
		window.textBox("filmId").enterText(filmId);
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"'','',King Kong",
        "'',1955,''",
        "'',1955,King Kong",
        "1,'',''",
        "1,'',King Kong",
        "1,1955,''"
    })
	@DisplayName("Test all scenarios where we have the ADD button disabled for filmName.")
	void testAddButtonDisabledForName(String filmId, String filmDate, String filmName) {
		window.textBox("filmId").enterText(" ");
		window.textBox("filmDate").enterText(" ");
		window.textBox("filmName").enterText(" ");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"'','',1955",
		"'',King Kong,''",
        "'',King Kong,1955",
        "1,'',''",
        "1,'',1955",
        "1,King Kong,''"
    })
	@DisplayName("Test all scenarios where we have the ADD button disabled for filmDate.")
	void testAddButtonDisabledForDate(String filmId, String filmName, String filmDate) {
		window.textBox("filmId").enterText(filmId);
		window.textBox("filmName").enterText(filmName);
		window.textBox("filmDate").enterText(filmDate);
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',1960,' '",
		"' ',1960,1",
		"King Kong,' ',' '",
		"King Kong,' ',1",
		"King Kong,1960,' '"
    })
	@DisplayName("Test all scenarios where we have the REMOVE button disabled for filmId.")
	void testRemoveButtonDisabledForId(String filmName, String filmDate, String filmId) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmName").enterText(filmName);
		window.textBox("filmDate").enterText(filmDate);
		window.textBox("filmId").enterText(filmId);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId",
		"filmName",
		"filmDate"
    })
	@DisplayName("Test all scenarios where we have the REMOVE button disabled for fields and films list.")
	void testRemoveButtonEnabledForFieldsAndFilmsList(String field) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox(field).enterText(" ");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',1950,' '",
		"' ',1950,King Kong",
		"1,' ',' '",
		"1,' ',King Kong",
		"1,1950,' '"
    })
	@DisplayName("Test all scenarios where we have the REMOVE button disabled for filmName.")
	void testRemoveButtonEnabledForName(String filmId, String filmDate, String filmName) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmId").enterText(filmId);
		window.textBox("filmDate").enterText(filmDate);
		window.textBox("filmName").enterText(filmName);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',King Kong,' '",
		"' ',King Kong,1950",
		"1,' ',' '",
		"1,' ',1950",
		"1,King Kong,' '"
    })
	@DisplayName("Test all scenarios where we have the REMOVE button disabled for filmDate.")
	void testRemoveButtonEnabledForDate(String filmId, String filmName, String filmDate) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmId").enterText(filmId);
		window.textBox("filmName").enterText(filmName);
		window.textBox("filmDate").enterText(filmDate);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the REMOVE button disabled for films list.")
	void testRemoveButtonDisabledForFilmsList() {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmId").enterText("1");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
		window.textBox("filmId").setText("");
		window.textBox("filmName").enterText(" ");
		window.textBox("filmDate").enterText(" ");
		window.list("filmsList").unselectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button enabled for films list.")
	void testUpdateButtonEnabledForFilmsListSelection() {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmId").enterText("1");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
		window.textBox("filmId").setText("");
		window.textBox("filmName").enterText("King Kong");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId,filmDate,1,1950",
		"filmName,filmId,King Kong,1",
		"filmDate,filmId,1950,1"
    })
	@DisplayName("Test the scenario where we have the UPDATE button enabled for fields and films list.")
	void testUpdateButtonEnabledForFieldsAndFilmsList(String field1, String field2, String value1, String value2) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
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
		"filmId,filmName,King Kong",
		"filmName,filmId,1",
		"filmDate,filmId,1"
    })
	@DisplayName("Test the scenario where we have the UPDATE button disabled for fields and films list.")
	void testUpdateButtonDisabledForFieldsAndFilmsList(String field1, String field2, String value1) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		window.textBox(field1).enterText(" ");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.list("filmsList").unselectItems(0);
		window.textBox(field2).enterText(value1);
		window.textBox(field1).setText("");
		window.textBox(field1).enterText(" ");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button disabled for films list selection.")
	void testUpdateButtonDisabledForFilmsListSelection() {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button disabled for id and films list.")
	void testUpdateButtonDisabledForIdAndFilmsList() {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox("filmId").enterText("1");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.textBox("filmId").setText("");
		window.textBox("filmName").enterText("King Kong");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test when we add a Film correctly.")
	void testAddFilmCorrectly() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmId").requireText("");
		window.textBox("filmName").requireText("");
		window.textBox("filmDate").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		verify(streamingController).addFilm(filmView, 1, "King Kong", 1950);
	}
	
	@Test @GUITest
	@DisplayName("Test when we add a Film with an exception.")
	void testAddFilmWithException() {
		doThrow(new IllegalArgumentException("ERROR: id should be positive!"))
			.when(streamingController).addFilm(filmView, 0, "King Kong", 1950);
		window.textBox("filmId").enterText("0");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmId").requireText("");
		window.textBox("filmName").requireText("");
		window.textBox("filmDate").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: id should be positive!");
	}
	
	@Test @GUITest
	@DisplayName("Test when we add a Film by calling explicitly the method of the view.")
	void testFilmAdded() {
		filmView.filmAdded(1, "King Kong", 1950);
		assertThat(window.list("filmsList").contents()).contains("1,King Kong,1950");
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId,2",
		"filmName,King Kong",
		"filmDate,1950"
    })
	@DisplayName("Test all scenarios where we update films by fields values.")
	void testFilmUpdatedByFields(String field, String value) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		window.textBox(field).enterText(value);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox(field).requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		if(field.equals("filmName"))
			verify(streamingController).updateFilmByName(filmView, 1, "King Kong", 0);
		else if(field.equals("filmDate"))
			verify(streamingController).updateFilmDate(filmView, 1, 1950, 0);
		else
			verify(streamingController).updateFilmById(filmView, 1, 2, 0);
	}

	@Test @GUITest
	@DisplayName("Test when we update films with an exception.")
	void testFilmUpdatedWithException() {
		doThrow(new IllegalArgumentException("ERROR: Film with id 1 already exists!")).
			when(streamingController).updateFilmById(filmView, 1, 1, 0);
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		window.textBox("filmId").enterText("1");
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox("filmId").requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: Film with id 1 already exists!");
		
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId,2",
		"filmName,King",
		"filmDate,1955"
    })
	@DisplayName("Test all scenarios where we update films correctly by fields values with the explicit method of the view.")
	void testFilmUpdatedCorrectlyByFields(String field, String value) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		if(field.equals("filmId"))
			filmView.filmUpdatedById(1, Integer.parseInt(value), 0);
		else if(field.equals("filmDate"))
			filmView.filmUpdatedByDate(1, Integer.parseInt(value), 0);
		else
			filmView.filmUpdatedByName(1, value, 0);
		if(field.equals("filmId"))
			assertThat(window.list("filmsList").contents()).contains("2,King Kong,1950");
		else if(field.equals("filmDate"))
			assertThat(window.list("filmsList").contents()).contains("1,King Kong,1955");
		else
			assertThat(window.list("filmsList").contents()).contains("1,King,1950");
	}
	
	@Test @GUITest
	@DisplayName("Test when we remove a film by id.")
	void testFilmRemovedById() {
		filmView.filmAdded(1, "King Kong", 1950);
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).click();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		verify(streamingController).deleteFilmById(filmView, 1, 0);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmName,King Kong",
		"filmDate,1950"
    })
	@DisplayName("Test when we remove a film by name and date.")
	void testFilmRemovedByNameAndDate(String field, String value) {
		filmView.filmAdded(1, "King Kong", 1950);
		window.textBox(field).enterText(value);
		window.button(JButtonMatcher.withText("REMOVE")).click();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		window.textBox(field).enterText("");	
		if(field.equals("filmName"))
			verify(streamingController).deleteFilmsByName(filmView, "King Kong");
		else
			verify(streamingController).deleteFilmsByDate(filmView, 1950);
	}
	
	@Test @GUITest
	@DisplayName("Test when we remove a film by id from films list.")
	void testFilmRemovedByIdFromFilmsList() {
		filmView.filmAdded(1, "King Kong", 1950);
		filmView.filmDeletedById(1,0);
		assertThat(window.list("filmsList").contents()).isEmpty();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmName",
		"filmDate"
    })
	@DisplayName("Test when we remove a film by name and date from films list.")
	void testFilmRemovedByNameAndDateFromFilmsList(String field) {
		
		Film f = new Film(1, "King Kong", 1950);
		List<Film> films = List.of(f);
		filmView.filmAdded(1, "King Kong", 1950);
		if(field.equals("filmName"))
			filmView.filmsDeletedByName(films);
		else
			filmView.filmsDeletedByDate(films);
		assertThat(window.list("filmsList").contents()).isEmpty();
	}
	
	@Test @GUITest
	@DisplayName("Test when we found all films from FilmRepository.")
	void testAllFilmsFound() {
		filmView.filmAdded(1, "King Kong", 1950);
		filmView.filmAdded(2, "Bad Boys", 1995);
		filmView.filmAdded(3, "Bad Boys 2", 2000);
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox("filmId").requireText("");
		verify(streamingController).searchAllFilms(filmView);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId,2",
		"filmName,King Kong",
		"filmDate,1950"
    })
	@DisplayName("Test when we found films by fields values.")
	void testFilmFoundByFields(String field, String value) {
		filmView.filmAdded(1, "King Kong", 1950);
		filmView.filmAdded(2, "Bad Boys", 1995);
		filmView.filmAdded(3, "Bad Boys 2", 2000);
		window.textBox(field).enterText(value);
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox(field).requireText("");
		if(field.equals("filmId"))
			verify(streamingController).searchFilmById(filmView, Integer.parseInt(value));
		else if(field.equals("filmDate"))
			verify(streamingController).searchFilmsByDate(filmView, Integer.parseInt(value));
		else
			verify(streamingController).searchFilmsByName(filmView, value);
	}
	
	@Test @GUITest
	@DisplayName("Test when we found a film by calling explicitly the method of the view.")
	void testFilmFoundByCallingViewMethod() {
		filmView.filmAdded(1, "King Kong", 1950);
		filmView.filmAdded(2, "Bad Boys", 1995);
		filmView.filmAdded(3, "Bad Boys 2", 2000);
		filmView.filmFound(1, "King Kong", 1950);
		assertThat(window.list("findList").contents()).contains("1,King Kong,1950");
	}

}
