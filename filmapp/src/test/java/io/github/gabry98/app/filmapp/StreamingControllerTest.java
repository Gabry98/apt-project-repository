package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.ActorRepository;
import io.github.gabry98.app.filmapp.repository.FilmRepository;
import io.github.gabry98.app.filmapp.repository.ReportRepository;
import io.github.gabry98.app.filmapp.view.ActorView;
import io.github.gabry98.app.filmapp.view.FilmView;
import io.github.gabry98.app.filmapp.view.ReportView;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for Streaming Controller.")
class StreamingControllerTest {
	
	@Mock
	private ActorRepository actorRepository;
	
	@Mock
	private FilmRepository filmRepository;
	
	@Mock
	private ReportRepository reportRepository;
	
	@Mock
	private ActorView actorView;
	
	@Mock
	private FilmView filmView;
	
	@Mock
	private ReportView reportView;
	
	@InjectMocks
	private StreamingController streamingController;
	
	private AutoCloseable closeable;
	
	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	@DisplayName("Test When we add an Actor to the Actor Repository.")
	void testWhenAddAnActor() {
		streamingController.addActor(actorView, 1, "Marco Rossi");
		verify(actorRepository).addActor(1, "Marco Rossi");
		verify(actorView).actorAdded(1, "Marco Rossi");
	}
	
	@Test
	@DisplayName("Test When we delete an Actor to the Actor Repository by ID.")
	void testWhenDeleteAnActorById() {
		streamingController.deleteActor(actorView, 1,0);
		verify(actorRepository).deleteActorById(1);
		verify(actorView).actorDeletedById(0);
	}
	
	@Test
	@DisplayName("Test When we delete a list of Actors to the Actor Repository by Name.")
	void testWhenDeleteAnActorByName() {
		Actor a = new Actor(1, "Marco Rossi");
		List<Actor> actors = List.of(a);
		when(actorRepository.findByName("Marco Rossi")).thenReturn(actors);
		streamingController.deleteActor(actorView, "Marco Rossi");
		verify(actorRepository).deleteActorsByName("Marco Rossi");
		verify(actorView).actorsDeletedByName(actors);
	}
	
	@Test
	@DisplayName("Test When we update an Actor with new ID.")
	void testWhenUpdateAnActorById() {
		streamingController.updateActor(actorView, 1, 2,0);
		verify(actorRepository).updateActorId(1,2);
		verify(actorView).actorUpdated(1,2,0);
	}
	
	@Test
	@DisplayName("Test When we update an Actor with new Name.")
	void testWhenUpdateAnActorByName() {
		streamingController.updateActor(actorView, 1, "Marco Verdi",0);
		verify(actorRepository).updateActorName(1,"Marco Verdi");
		verify(actorView).actorUpdated(1,"Marco Verdi",0);
	}
	
	@Test
	@DisplayName("Test When we search all Actors.")
	void testWhenSearchAllActors() {
		Actor a = spy(new Actor(1, "Marco Rossi"));
		List<Actor> actors = List.of(a);
		when(actorRepository.findAll()).thenReturn(actors);
		streamingController.searchAllActors(actorView);
		verify(actorRepository).findAll();
		verify(actorView).actorFound(1,"Marco Rossi");
		verify(a).getId();
		verify(a).getName();
	}
	
	@Test
	@DisplayName("Test When we search an Actor by ID.")
	void testWhenSearchAnActorById() {
		Actor a = spy(new Actor(1, "Marco Rossi"));
		when(actorRepository.findById(1)).thenReturn(a);
		streamingController.searchActorById(actorView, 1);
		verify(actorRepository).findById(1);
		verify(actorView).actorFound(1,"Marco Rossi");
		verify(a).getId();
		verify(a).getName();
	}
	
	@Test
	@DisplayName("Test When we search an Actor by Name.")
	void testWhenSearchAnActorByName() {
		Actor a = spy(new Actor(1, "Marco Rossi"));
		List<Actor> actors = List.of(a);
		when(actorRepository.findByName("Marco Rossi")).thenReturn(actors);
		streamingController.searchActorsByName(actorView, "Marco Rossi");
		verify(actorRepository).findByName("Marco Rossi");
		verify(actorView).actorFound(1,"Marco Rossi");
		verify(a).getId();
		verify(a).getName();
	}
	
	@Test
	@DisplayName("Test When we add a Film to the Film Repository.")
	void testWhenAddAFilm() {
		streamingController.addFilm(filmView, 1, "King Kong", 1950);
		verify(filmRepository).addFilm(1, "King Kong", 1950);
		verify(filmView).filmAdded(1, "King Kong", 1950);
	}
	
	@Test
	@DisplayName("Test When we delete a Film to the Film Repository by ID.")
	void testWhenDeleteAFilmById() {
		streamingController.deleteFilmById(filmView, 1, 0);
		verify(filmRepository).deleteFilmById(1);
		verify(filmView).filmDeletedById(1,0);
	}
	
	@Test
	@DisplayName("Test When we delete a list of Films to the Film Repository by Name.")
	void testWhenDeleteAFilmByName() {
		Film f = new Film(1, "King Kong", 1950);
		List<Film> films = List.of(f);
		when(filmRepository.findByName("King Kong")).thenReturn(films);
		streamingController.deleteFilmsByName(filmView, "King Kong");
		verify(filmRepository).deleteFilmsByName("King Kong");
		verify(filmView).filmsDeletedByName(films);
	}
	
	@Test
	@DisplayName("Test When we delete a list of Films to the Film Repository by Date.")
	void testWhenDeleteAFilmByDate() {
		Film f = new Film(1, "King Kong", 1950);
		List<Film> films = List.of(f);
		when(filmRepository.findByDate(1950)).thenReturn(films);
		streamingController.deleteFilmsByDate(filmView, 1950);
		verify(filmRepository).deleteFilmsByDate(1950);
		verify(filmView).filmsDeletedByDate(films);
	}
	
	@Test
	@DisplayName("Test When we update a Film with new ID.")
	void testWhenUpdateAFilmById() {
		streamingController.updateFilmById(filmView, 1, 2, 0);
		verify(filmRepository).updateFilmId(1,2);
		verify(filmView).filmUpdatedById(1,2,0);
	}
	
	@Test
	@DisplayName("Test When we update a Film with new Name.")
	void testWhenUpdateAFilmByName() {
		streamingController.updateFilmByName(filmView, 1, "King Kong 2", 0);
		verify(filmRepository).updateFilmName(1,"King Kong 2");
		verify(filmView).filmUpdatedByName(1,"King Kong 2", 0);
	}
	
	@Test
	@DisplayName("Test When we update a Film with new Date.")
	void testWhenUpdateAFilmByDate() {
		streamingController.updateFilmDate(filmView, 1, 1955, 0);
		verify(filmRepository).updateFilmDate(1,1955);
		verify(filmView).filmUpdatedByDate(1,1955, 0);
	}
	
	@Test
	@DisplayName("Test When we search a Film by ID.")
	void testWhenSearchAFilmById() {
		Film f = spy(new Film(1, "King Kong", 1950));
		when(filmRepository.findById(1)).thenReturn(f);
		streamingController.searchFilmById(filmView, 1);
		verify(filmRepository).findById(1);
		verify(filmView).filmFound(1,"King Kong", 1950);
		verify(f).getId();
		verify(f).getName();
		verify(f).getDate();
	}
	
	@Test
	@DisplayName("Test When we search all Films.")
	void testWhenSearchAllFilms() {
		Film f = spy(new Film(1, "King Kong", 1950));
		List<Film> films = List.of(f);
		when(filmRepository.findAll()).thenReturn(films);
		streamingController.searchAllFilms(filmView);
		verify(filmRepository).findAll();
		verify(filmView).filmFound(1,"King Kong", 1950);
		verify(f).getId();
		verify(f).getName();
		verify(f).getDate();
	}
	
	@Test
	@DisplayName("Test When we search a list of Films by Name.")
	void testWhenSearchFilmsByName() {
		Film f = spy(new Film(1, "King Kong", 1950));
		List<Film> films = List.of(f);
		when(filmRepository.findByName("King Kong")).thenReturn(films);
		streamingController.searchFilmsByName(filmView, "King Kong");
		verify(filmRepository).findByName("King Kong");
		verify(filmView).filmFound(1,"King Kong", 1950);
		verify(f).getId();
		verify(f).getName();
		verify(f).getDate();
	}
	
	@Test
	@DisplayName("Test When we search a list of Films by Date.")
	void testWhenSearchFilmsByDate() {
		Film f = spy(new Film(1, "King Kong", 1950));
		List<Film> films = List.of(f);
		when(filmRepository.findByDate(1950)).thenReturn(films);
		streamingController.searchFilmsByDate(filmView, 1950);
		verify(filmRepository).findByDate(1950);
		verify(filmView).filmFound(1,"King Kong", 1950);
		verify(f).getId();
		verify(f).getName();
		verify(f).getDate();
	}
	
	@Test
	@DisplayName("Test When we add a Report to the Report Repository.")
	void testWhenAddAReportCorrectly() {
		when(actorRepository.findById(1)).thenReturn(new Actor(1,"Marco Rossi"));
		when(filmRepository.findById(1)).thenReturn(new Film(1,"King Kong",1950));
		streamingController.addReport(reportView, 1, 1, 1);
		verify(reportRepository).addReport(1, 1, 1);
		verify(reportView).reportAdded(1, 1, 1);
	}
	
	@Test
	@DisplayName("Test When we add a Report with missing Film.")
	void testWhenAddAReportWithMissingFilm() {
		when(actorRepository.findById(1)).thenReturn(new Actor(1,"Marco Rossi"));
		IllegalArgumentException thrown = 
				assertThrows(IllegalArgumentException.class, () -> 
					streamingController.addReport(reportView, 1, 1, 1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: No existing Film with id 1.");
		verify(reportRepository, times(0)).addReport(1, 1, 1);
		verify(reportView, times(0)).reportAdded(1, 1, 1);
	}
	
	@Test
	@DisplayName("Test When we add a Report with missing Actor.")
	void testWhenAddAReportWithMissingActor() {
		when(filmRepository.findById(1)).thenReturn(new Film(1,"King Kong",1950));
		IllegalArgumentException thrown = 
				assertThrows(IllegalArgumentException.class, () -> 
					streamingController.addReport(reportView, 1, 1, 1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: No existing Actor with id 1.");
		verify(reportRepository, times(0)).addReport(1, 1, 1);
		verify(reportView, times(0)).reportAdded(1, 1, 1);
	}
	
	@Test
	@DisplayName("Test When we delete a Report to the Report Repository by ID.")
	void testWhenDeleteAReportById() {
		streamingController.deleteReportById(reportView, 1, 0);
		verify(reportRepository).deleteReportById(1);
		verify(reportView).reportDeletedById(1,0);
	}
	
	@Test
	@DisplayName("Test When we delete a Report to the Report Repository by ActorId.")
	void testWhenDeleteAReportByActorId() {
		Report r = new Report(1,1,1);
		List<Report> reports = List.of(r);
		when(reportRepository.findByActorId(1)).thenReturn(reports);
		streamingController.deleteReportByActorId(reportView, 1);
		verify(reportRepository).deleteReportByActorId(1);
		verify(reportView).reportsDeletedByActorId(reports);
	}
	
	@Test
	@DisplayName("Test When we delete a Report to the Report Repository by FilmId.")
	void testWhenDeleteAReportByFilmId() {
		Report r = new Report(1,1,1);
		List<Report> reports = List.of(r);
		when(reportRepository.findByFilmId(1)).thenReturn(reports);
		streamingController.deleteReportByFilmId(reportView, 1);
		verify(reportRepository).deleteReportByFilmId(1);
		verify(reportView).reportsDeletedByFilmId(reports);
	}
	
	@Test
	@DisplayName("Test When we update a Report with new ID.")
	void testWhenUpdateAReportById() {
		streamingController.updateReportId(reportView, 1, 2, 0);
		verify(reportRepository).updateReportId(1,2);
		verify(reportView).reportUpdatedById(1,2, 0);
	}
	
	@Test
	@DisplayName("Test When we search all Reports.")
	void testWhenSearchAllReports() {
		Report r = spy(new Report(1, 1, 1));
		List<Report> reports = List.of(r);
		when(reportRepository.findAll()).thenReturn(reports);
		streamingController.searchAllReports(reportView);
		verify(reportRepository).findAll();
		verify(reportView).reportFound(1,1,1);
		verify(r).getId();
		verify(r).getActorId();
		verify(r).getFilmId();
	}
	
	@Test
	@DisplayName("Test When we search a Report by ID.")
	void testWhenSearchAReportById() {
		Report r = spy(new Report(1, 1, 1));
		when(reportRepository.findById(1)).thenReturn(r);
		streamingController.searchReportById(reportView,1);
		verify(reportRepository).findById(1);
		verify(reportView).reportFound(1,1,1);
		verify(r).getId();
		verify(r).getActorId();
		verify(r).getFilmId();
	}
	
	@Test
	@DisplayName("Test When we search a list of Reports by ActorId.")
	void testWhenSearchReportsByActorId() {
		Report r = spy(new Report(1, 1, 1));
		List<Report> reports = List.of(r);
		when(reportRepository.findByActorId(1)).thenReturn(reports);
		streamingController.searchReportsByActorId(reportView,1);
		verify(reportRepository).findByActorId(1);
		verify(reportView).reportFound(1,1,1);
		verify(r).getId();
		verify(r).getActorId();
		verify(r).getFilmId();
	}
	
	@Test
	@DisplayName("Test When we search a list of Reports by FilmId.")
	void testWhenSearchReportsByFilmId() {
		Report r = spy(new Report(1, 1, 1));
		List<Report> reports = List.of(r);
		when(reportRepository.findByFilmId(1)).thenReturn(reports);
		streamingController.searchReportsByFilmId(reportView,1);
		verify(reportRepository).findByFilmId(1);
		verify(reportView).reportFound(1,1,1);
		verify(r).getId();
		verify(r).getActorId();
		verify(r).getFilmId();
	}

}
