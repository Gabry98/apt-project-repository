package io.github.gabry98.app.filmapp.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.ActorRepository;
import io.github.gabry98.app.filmapp.repository.FilmRepository;
import io.github.gabry98.app.filmapp.repository.ReportRepository;
import io.github.gabry98.app.filmapp.view.ActorView;
import io.github.gabry98.app.filmapp.view.FilmView;
import io.github.gabry98.app.filmapp.view.ReportView;

public class StreamingController {
	
	private ActorRepository actorRepository;
	private FilmRepository filmRepository;
	private ReportRepository reportRepository;
	
	private static final Logger LOGGER = LogManager.getLogger(StreamingController.class);
	private static final String FILM_UPDATED = "Film updated successfully, sending the response to the Film View...";
	private static final String FILMS_FOUND = "Films found successfully, sending the response to the Film View...";
	private static final String REPORT_DELETED = "Report deleted successfully, sending the response to the Report View...";
	private static final String REPORTS_FOUND = "Reports found successfully, sending the response to the Report View...";
	
	public StreamingController(ActorRepository actorRepository, FilmRepository filmRepository, ReportRepository reportRepository) {
		this.actorRepository = actorRepository;
		this.filmRepository = filmRepository;
		this.reportRepository = reportRepository;
	}

	public void addActor(ActorView actorView, int id, String name) {
		LOGGER.info("Received a request to add an Actor with ID {} and Name {}...", id, name);
		actorRepository.addActor(id, name);
		LOGGER.info("Actor added successfully, sending the response to the Actor View...");
		actorView.actorAdded(id, name);
	}

	public void deleteActor(ActorView actorView, int id, int position) {
		LOGGER.info("Received a request to delete an Actor with ID {}...", id);
		actorRepository.deleteActorById(id);
		LOGGER.info("Actor deleted successfully, sending the response to the Actor View...");
		actorView.actorDeletedById(position);
	}

	public void deleteActor(ActorView actorView, String name) {
		LOGGER.info("Received a request to delete the Actors with Name {}...", name);
		List<Actor> deletedActors = actorRepository.findByName(name);
		actorRepository.deleteActorsByName(name);
		LOGGER.info("Actors deleted successfully, sending the response to the Actor View...");
		actorView.actorsDeletedByName(deletedActors);
	}

	public void updateActor(ActorView actorView, int oldId, int newId, int position) {
		LOGGER.info("Received a request to update an Actor with ID {} with the new ID {}...", oldId, newId);
		actorRepository.updateActorId(oldId, newId);
		LOGGER.info("Actor updated successfully, sending the response to the Actor View...");
		actorView.actorUpdated(oldId,newId,position);
	}

	public void updateActor(ActorView actorView, int id, String name, int position) {
		LOGGER.info("Received a request to update an Actor with ID {} with the new Name {}...", id, name);
		actorRepository.updateActorName(id, name);
		LOGGER.info("Actor updated successfully, sending the response to the Actor View...");
		actorView.actorUpdated(id,name,position);
	}
	
	public void searchAllActors(ActorView actorView) {
		LOGGER.info("Received a request to search the list of all Actors...");
		List<Actor> actors = actorRepository.findAll();
		LOGGER.info("Actors found successfully, sending the response to the Actor View...");
		actors.forEach(a -> actorView.actorFound(a.getId(), a.getName()));
	}

	public void searchActorById(ActorView actorView, int id) {
		LOGGER.info("Received a request to search an Actor with ID {}...", id);
		Actor a = actorRepository.findById(id);
		LOGGER.info("Actor found successfully, sending the response to the Actor View...");
		actorView.actorFound(a.getId(), a.getName());
	}

	public void searchActorsByName(ActorView actorView, String name) {
		LOGGER.info("Received a request to search a list of Actors with Name {}...", name);
		List<Actor> actors = actorRepository.findByName(name);
		LOGGER.info("Actors found successfully, sending the response to the Actor View...");
		actors.forEach(a -> actorView.actorFound(a.getId(), a.getName()));
	}

	public void addFilm(FilmView filmView, int id, String name, int date) {
		LOGGER.info("Received a request to add a Film with ID {}, Name {} and Date {}...", id, name, date);
		filmRepository.addFilm(id, name, date);
		LOGGER.info("Film added successfully, sending the response to the Film View...");
		filmView.filmAdded(id, name, date);
	}

	public void deleteFilmById(FilmView filmView, int id, int position) {
		LOGGER.info("Received a request to delete a Film with ID {}...", id);
		filmRepository.deleteFilmById(id);
		LOGGER.info("Film deleted successfully, sending the response to the Film View...");
		filmView.filmDeletedById(id,position);
	}

	public void deleteFilmsByName(FilmView filmView, String name) {
		LOGGER.info("Received a request to delete a list of Films with Name {}...", name);
		List<Film> films = filmRepository.findByName(name);
		filmRepository.deleteFilmsByName(name);
		LOGGER.info("Films deleted successfully, sending the response to the Film View...");
		filmView.filmsDeletedByName(films);
	}
	
	public void deleteFilmsByDate(FilmView filmView, int date) {
		LOGGER.info("Received a request to delete a list of Films with Date {}...", date);
		List<Film> films = filmRepository.findByDate(date);
		filmRepository.deleteFilmsByDate(date);
		LOGGER.info("Films deleted successfully, sending the response to the Film View...");
		filmView.filmsDeletedByDate(films);
	}
	
	public void updateFilmById(FilmView filmView, int oldId, int newId, int position) {
		LOGGER.info("Received a request to update a Film with ID {} with the new ID {}...", oldId, newId);
		filmRepository.updateFilmId(oldId, newId);
		LOGGER.info(FILM_UPDATED);
		filmView.filmUpdatedById(oldId,newId,position);
	}

	public void updateFilmByName(FilmView filmView, int id, String name, int position) {
		LOGGER.info("Received a request to update a Film with ID {} with the new Name {}...", id, name);
		filmRepository.updateFilmName(id, name);
		LOGGER.info(FILM_UPDATED);
		filmView.filmUpdatedByName(id, name, position);
	}

	public void updateFilmDate(FilmView filmView, int id, int date, int position) {
		LOGGER.info("Received a request to update a Film with ID {} with the new Date {}...", id, date);
		filmRepository.updateFilmDate(id, date);
		LOGGER.info(FILM_UPDATED);
		filmView.filmUpdatedByDate(id, date, position);
	}
	
	public void searchAllFilms(FilmView filmView) {
		LOGGER.info("Received a request to search the list of all Films...");
		List<Film> films = filmRepository.findAll();
		LOGGER.info(FILMS_FOUND);
		films.forEach(f -> filmView.filmFound(f.getId(), f.getName(), f.getDate()));
	}

	public void searchFilmById(FilmView filmView, int id) {
		LOGGER.info("Received a request to search a Film with ID {}...", id);
		Film f = filmRepository.findById(id);
		LOGGER.info("Film found successfully, sending the response to the Film View...");
		filmView.filmFound(f.getId(), f.getName(), f.getDate());
	}

	public void searchFilmsByName(FilmView filmView, String name) {
		LOGGER.info("Received a request to search a list of Films with Name {}...", name);
		List<Film> films = filmRepository.findByName(name);
		LOGGER.info(FILMS_FOUND);
		films.forEach(f -> filmView.filmFound(f.getId(), f.getName(), f.getDate()));
	}

	public void searchFilmsByDate(FilmView filmView, int date) {
		LOGGER.info("Received a request to search a list of Films with Date {}...", date);
		List<Film> films = filmRepository.findByDate(date);
		LOGGER.info(FILMS_FOUND);
		films.forEach(f -> filmView.filmFound(f.getId(), f.getName(), f.getDate()));
	}

	public void addReport(ReportView reportView, int id, int actorId, int filmId) {
		LOGGER.info("Received a request to add a Report with ID {}, ActorId {} and FilmId {}...", id, actorId, filmId);
		
		if(actorRepository.findById(actorId) == null) {
			throw new IllegalArgumentException("ERROR: No existing Actor with id "+actorId+".");
		}
		
		if(filmRepository.findById(filmId) == null) {
			throw new IllegalArgumentException("ERROR: No existing Film with id "+filmId+".");
		}
		
		reportRepository.addReport(id, actorId, filmId);
		LOGGER.info("Report added successfully, sending the response to the Report View...");
		reportView.reportAdded(id, actorId, filmId);
	}

	public void deleteReportById(ReportView reportView, int id, int position) {
		LOGGER.info("Received a request to delete a Report with ID {}...", id);
		reportRepository.deleteReportById(id);
		LOGGER.info(REPORT_DELETED);
		reportView.reportDeletedById(id,position);
	}

	public void deleteReportByActorId(ReportView reportView, int actorId) {
		LOGGER.info("Received a request to delete a Report with ActorId {}...", actorId);
		List<Report> reports = reportRepository.findByActorId(actorId);
		reportRepository.deleteReportByActorId(actorId);
		LOGGER.info(REPORT_DELETED);
		reportView.reportsDeletedByActorId(reports);
	}

	public void deleteReportByFilmId(ReportView reportView, int filmId) {
		LOGGER.info("Received a request to delete a Report with FilmId {}...", filmId);
		List<Report> reports = reportRepository.findByFilmId(filmId);
		reportRepository.deleteReportByFilmId(filmId);
		LOGGER.info(REPORT_DELETED);
		reportView.reportsDeletedByFilmId(reports);
	}

	public void updateReportId(ReportView reportView, int oldId, int newId, int position) {
		LOGGER.info("Received a request to update a Report with ID {} with the new ID {}...", oldId, newId);
		reportRepository.updateReportId(oldId, newId);
		LOGGER.info("Report updated successfully, sending the response to the Report View...");
		reportView.reportUpdatedById(oldId,newId,position);
	}

	public void searchAllReports(ReportView reportView) {
		LOGGER.info("Received a request to search the list of all Reports...");
		List<Report> reports = reportRepository.findAll();
		LOGGER.info(REPORTS_FOUND);
		reports.forEach(r -> reportView.reportFound(r.getId(), r.getActorId(), r.getFilmId()));
	}

	public void searchReportById(ReportView reportView, int id) {
		LOGGER.info("Received a request to search a Report with ID {}...", id);
		Report r = reportRepository.findById(id);
		LOGGER.info(REPORTS_FOUND);
		reportView.reportFound(r.getId(), r.getActorId(), r.getFilmId());
	}

	public void searchReportsByActorId(ReportView reportView, int actorId) {
		LOGGER.info("Received a request to search a Report with ActorId {}...", actorId);
		List<Report> reports = reportRepository.findByActorId(actorId);
		LOGGER.info(REPORTS_FOUND);
		reports.forEach(r -> reportView.reportFound(r.getId(), r.getActorId(), r.getFilmId()));
	}

	public void searchReportsByFilmId(ReportView reportView, int filmId) {
		LOGGER.info("Received a request to search a Report with FilmId {}...", filmId);
		List<Report> reports = reportRepository.findByFilmId(filmId);
		LOGGER.info(REPORTS_FOUND);
		reports.forEach(r -> reportView.reportFound(r.getId(), r.getActorId(), r.getFilmId()));
	}
}
