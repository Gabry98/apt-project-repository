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
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.view.gui.ReportGUIView;

@RunWith(GUITestRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for ReportGUIView")
class ReportGUIViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private ReportGUIView reportView;
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
			reportView = new ReportGUIView();
			reportView.setStreamingController(streamingController);
			return reportView;
		});
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), reportView);
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
		window.label(JLabelMatcher.withText("actorId:"));
		window.label(JLabelMatcher.withText("filmId:"));
		window.label(JLabelMatcher.withText(""));
		window.textBox("reportId").requireEnabled();
		window.textBox("filmId").requireEnabled();
		window.textBox("actorId").requireEnabled();
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.list("reportsList").requireEnabled();
		window.list("findList").requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"filmId,actorId,reportId",
		"filmId,reportId,actorId",
		"reportId,actorId,filmId",
    })
	@DisplayName("Test the scenarios where we have the ADD button enabled for reportId.")
	void testAddButtonEnabledForId(String field1, String field2, String field3) {
		window.textBox(field1).enterText("1");
		window.textBox(field2).enterText("1");
		window.textBox(field3).enterText("1");
		window.button(JButtonMatcher.withText("ADD")).requireEnabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',' ',1",
		"' ',1,' '",
		"' ',1,1",
		"1,' ',' '",
		"1,' ',1",
		"1,1,' '",
    })
	@DisplayName("Test the scenarios where we have the ADD button disabled for reportId.")
	void testAddButtonDisabledForId(String actorId, String filmId, String reportId) {
		window.textBox("actorId").enterText(actorId);
		window.textBox("filmId").enterText(filmId);
		window.textBox("reportId").enterText(reportId);
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',' ',1",
		"' ',1,' '",
		"' ',1,1",
		"1,' ',' '",
		"1,' ',1",
		"1,1,' '",
    })
	@DisplayName("Test the scenarios where we have the ADD button disabled for actorId.")
	void testAddButtonDisabledForActorId(String reportId, String filmId, String actorId) {
		window.textBox("reportId").enterText(reportId);
		window.textBox("filmId").enterText(filmId);
		window.textBox("actorId").enterText(actorId);
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"' ',' ',' '",
		"' ',' ',1",
		"' ',1,' '",
		"' ',1,1",
		"1,' ',' '",
		"1,' ',1",
		"1,1,' '",
    })
	@DisplayName("Test the scenarios where we have the ADD button disabled for filmId.")
	void testAddButtonDisabledForFilmId(String reportId, String actorId, String filmId) {
		window.textBox("reportId").enterText(reportId);
		window.textBox("actorId").enterText(actorId);
		window.textBox("filmId").enterText(filmId);
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test the scenario where we have the UPDATE button disabled for reportId.")
	void testUpdateButtonDisabledForReportId() {
		window.textBox("reportId").enterText(" ");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
	}
	
	@Test @GUITest
	@DisplayName("Test when we add a Report correctly.")
	void testAddReportCorrectly() {
		window.textBox("filmId").enterText("1");
		window.textBox("actorId").enterText("1");
		window.textBox("reportId").enterText("1");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmId").requireText("");
		window.textBox("actorId").requireText("");
		window.textBox("reportId").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		verify(streamingController).addReport(reportView, 1, 1, 1);
	}
	
	@Test @GUITest
	@DisplayName("Test when we add a Report with an Exception.")
	void testAddReportWithException() {
		doThrow(new IllegalArgumentException("ERROR: id should be positive!"))
		.when(streamingController).addReport(reportView, 1, 1, 1);
		window.textBox("filmId").enterText("1");
		window.textBox("actorId").enterText("1");
		window.textBox("reportId").enterText("1");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmId").requireText("");
		window.textBox("actorId").requireText("");
		window.textBox("reportId").requireText("");
		window.button(JButtonMatcher.withText("ADD")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: id should be positive!");
	}
	
	@Test @GUITest
	@DisplayName("Test the explicit method of the view for add a Record.")
	void testReportAdded() {
		reportView.reportAdded(1, 1, 1);
		assertThat(window.list("reportsList").contents()).contains("1,1,1");
	}
	
	@Test @GUITest
	@DisplayName("Test when we update a Report by reportId.")
	void testReportUpdatedById() {
		reportView.reportAdded(1, 1, 1);
		window.list("reportsList").selectItems(0);
		window.textBox("reportId").enterText("2");
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox("reportId").requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		verify(streamingController).updateReportId(reportView, 1, 2, 0);
	}
	
	@Test @GUITest
	@DisplayName("Test when we update a Report by reports list.")
	void testReportUpdatedByReportsList() {
		reportView.reportAdded(1, 1, 1);
		reportView.reportAdded(2, 2, 2);
		window.list("reportsList").selectItems(1);
		window.button(JButtonMatcher.withText("UPDATE")).requireEnabled();
	}
	
	@Test @GUITest
	@DisplayName("Test when we update a Report by the explicit call of the method of the view.")
	void testReportUpdatedByIdByCallingMethod() {
		reportView.reportAdded(1, 1, 1);
		reportView.reportUpdatedById(1, 2, 0);
		assertThat(window.list("reportsList").contents()).contains("2,1,1");
	}
	
	@Test @GUITest
	@DisplayName("Test when we update a Report with an Exception.")
	void testReportUpdatedWithException() {
		doThrow(new IllegalArgumentException("ERROR: Report with id 1 already exists!")).when(streamingController).updateReportId(reportView, 1, 1, 0);
		reportView.reportAdded(1, 1, 1);
		window.list("reportsList").selectItems(0);
		window.textBox("reportId").enterText("1");
		window.button(JButtonMatcher.withText("UPDATE")).click();
		window.textBox("reportId").requireText("");
		window.button(JButtonMatcher.withText("UPDATE")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLabel")).requireText("ERROR: Report with id 1 already exists!");
	}
	
	@Test @GUITest
	@DisplayName("Test when we remove a Report by reportId.")
	void testReportRemovedById() {
		reportView.reportAdded(1, 1, 1);
		window.list("reportsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).click();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		verify(streamingController).deleteReportById(reportView, 1, 0);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"actorId",
		"filmId"
    })
	@DisplayName("Test when we remove a Report by actorId and filmId.")
	void testReportRemovedByActorIdAndFilmId(String field) {
		reportView.reportAdded(1, 1, 1);
		window.textBox(field).enterText("1");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
		if(field.equals("actorId"))
			verify(streamingController).deleteReportByActorId(reportView, 1);
		else
			verify(streamingController).deleteReportByFilmId(reportView, 1);
	}
	
	@Test @GUITest
	@DisplayName("Test when we remove a Report by reports list.")
	void testReportRemovedByIdFromReportsList() {
		reportView.reportAdded(1, 1, 1);
		reportView.reportDeletedById(1,0);
		assertThat(window.list("reportsList").contents()).isEmpty();
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"actorId",
		"filmId"
    })
	@DisplayName("Test when we remove a Report by actorId, filmId and reports list selection.")
	void testReportRemovedByActorIdAndFilmIdFromReportsList(String field) {
		
		Report r = new Report(1, 1, 1);
		List<Report> reports = List.of(r);
		reportView.reportAdded(1, 1, 1);
		reportView.reportsDeletedByActorId(reports);
			
		if(field.equals("actorId"))
			reportView.reportsDeletedByActorId(reports);
		else
			reportView.reportsDeletedByFilmId(reports);
		assertThat(window.list("reportsList").contents()).isEmpty();
	}
	
	@Test @GUITest
	@DisplayName("Test when we find a Report by calling explicitly the method of the view.")
	void testReportFoundByCallingViewMethod() {
		reportView.reportAdded(1, 1, 1);
		reportView.reportAdded(2, 2, 2);
		reportView.reportAdded(3, 3, 3);
		reportView.reportFound(1, 1, 1);
		assertThat(window.list("findList").contents()).contains("1,1,1");
	}
	
	@Test @GUITest
	@DisplayName("Test when we find all Reports from ReportRepository.")
	void testReportFoundByReportsList() {
		reportView.reportAdded(1, 1, 1);
		reportView.reportAdded(2, 2, 2);
		window.list("reportsList").selectItems(1);
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox("reportId").requireText("");
		verify(streamingController).searchAllReports(reportView);
	}
	
	@GUITest
	@ParameterizedTest
	@CsvSource({
		"reportId",
		"actorId",
		"filmId"
    })
	@DisplayName("Test when we find a Report by main fields values.")
	void testReportFoundByFields(String field) {
		reportView.reportAdded(1, 1, 1);
		reportView.reportAdded(2, 2, 2);
		window.textBox(field).enterText("2");
		window.button(JButtonMatcher.withText("FIND")).click();
		window.button(JButtonMatcher.withText("FIND")).requireEnabled();
		window.textBox(field).requireText("");
		if(field.equals("reportId")) {
			verify(streamingController).searchReportById(reportView, 2);
		} else if(field.equals("actorId")) {
			verify(streamingController).searchReportsByActorId(reportView, 2);
		} else {
			verify(streamingController).searchReportsByFilmId(reportView, 2);
		}
	}

	@Test @GUITest
	@DisplayName("Test when we remove a Report by selecting it from reports list.")
	void testRemoveButtonForReportsList() {
		reportView.reportAdded(1, 1, 1);
		window.textBox("filmId").enterText(" ");
		window.list("reportsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireEnabled();
		window.list("reportsList").unselectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).requireDisabled();
	}
}
