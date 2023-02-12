package io.github.gabry98.app.filmapp.view.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.jacoco.Generated;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.view.FilmView;

public class FilmGUIView extends JFrame implements FilmView {

	private static final Logger LOGGER = LogManager.getLogger(FilmGUIView.class);
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField filmId;
	private JTextField filmName;
	private JList<String> filmsList;
	private JList<String> findList;
	private DefaultListModel<String> filmsModel;
	private DefaultListModel<String> findModel;
	private transient StreamingController streamingController;
	private JScrollPane scrollFilmsListPane;
	private JScrollPane scrollFindPane;
	private JLabel errorLabel;
	private JButton filmAdd;
	private JButton filmUpdate;
	private JButton filmRemove;
	private JButton filmFind;
	private JLabel dateLabel;
	private JTextField filmDate;

	/**
	 * Launch the application.
	 */
	@Generated
	public static void main(String[] args) {
		EventQueue.invokeLater(FilmGUIView::run);
	}
	
	@Generated
	private static void run() {
		try {
			FilmGUIView frame = new FilmGUIView();
			frame.setVisible(true);
		} catch (Exception e) {
			LOGGER.error("An Exception has been thrown during initialization of the View: {}", e.getMessage());
		}
	}

	/**
	 * Create the frame.
	 */
	public FilmGUIView() {
		this.setTitle("Film View");
		this.setName("Film View");
		filmsModel = new DefaultListModel<>();
		findModel = new DefaultListModel<>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel idLabel = new JLabel("Id:");
		idLabel.setBounds(10, 11, 46, 14);
		contentPane.add(idLabel);

		filmAdd = new JButton("ADD");
		filmAdd.setEnabled(false);
		filmAdd.setBounds(113, 131, 86, 23);
		filmAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkFilmAdd();
			}
		});
		contentPane.add(filmAdd);
		
		filmUpdate = new JButton("UPDATE");
		filmUpdate.setEnabled(false);
		filmUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkFilmUpdate();
			}
		});
		filmUpdate.setBounds(10, 158, 89, 23);
		contentPane.add(filmUpdate);
		
		filmRemove = new JButton("REMOVE");
		filmRemove.setEnabled(false);
		filmRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkFilmRemove();
			}
		});
		filmRemove.setBounds(110, 196, 89, 23);
		contentPane.add(filmRemove);
		
		filmFind = new JButton("FIND");
		filmFind.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkFilmFind();
			}
		});
		filmFind.setBounds(211, 158, 89, 23);
		contentPane.add(filmFind);
		
		filmId = new JTextField();
		filmId.setName("filmId");
		filmId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndFilmAndDateNotEmpty = !filmId.getText().trim().isEmpty() && !filmName.getText().trim().isEmpty() && !filmDate.getText().trim().isEmpty();
				boolean idNotEmptyAndFilmAndDateEmpty = !filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty();
				boolean filmNotEmptyAndIdAndDateEmpty = filmId.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty() && !filmName.getText().trim().isEmpty();
				boolean dateNotEmptyAndIdAndFilmEmpty = filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && !filmDate.getText().trim().isEmpty();
				boolean noSelection = filmsList.isSelectionEmpty();
				checkFilmId(idAndFilmAndDateNotEmpty, idNotEmptyAndFilmAndDateEmpty, filmNotEmptyAndIdAndDateEmpty,
						dateNotEmptyAndIdAndFilmEmpty, noSelection);
			}
		});
		filmId.setBounds(32, 8, 86, 20);
		contentPane.add(filmId);
		filmId.setColumns(10);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(128, 11, 46, 14);
		contentPane.add(nameLabel);
		
		filmName = new JTextField();
		filmName.setName("filmName");
		filmName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndFilmAndDateNotEmpty = !filmName.getText().trim().isEmpty() && !filmId.getText().trim().isEmpty() && !filmDate.getText().trim().isEmpty();
				boolean idNotEmptyAndFilmAndDateEmpty =  filmDate.getText().trim().isEmpty() && !filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty();
				boolean filmNotEmptyAndIdAndDateEmpty = filmId.getText().trim().isEmpty() && !filmName.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty();
				boolean dateNotEmptyAndIdAndFilmEmpty = filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && !filmDate.getText().trim().isEmpty();
				boolean noSelection = filmsList.isSelectionEmpty();
				checkFilmName(idAndFilmAndDateNotEmpty, idNotEmptyAndFilmAndDateEmpty, filmNotEmptyAndIdAndDateEmpty,
						dateNotEmptyAndIdAndFilmEmpty, noSelection);
			}
		});
		filmName.setBounds(165, 8, 164, 20);
		contentPane.add(filmName);
		filmName.setColumns(10);
		
		filmsList = new JList<>();
		filmsList.setName("filmsList");
		filmsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean noSelection = filmsList.isSelectionEmpty();
				boolean idNotEmpty = !filmId.getText().trim().isEmpty();
				boolean filmNotEmpty = !filmName.getText().trim().isEmpty();
				checkFilmsList(noSelection, idNotEmpty, filmNotEmpty);
			}
		});
		filmsList.setBounds(10, 113, 414, -70);
		filmsList.setBackground(new Color(192, 192, 192));
		filmsList.setForeground(new Color(0, 128, 0));
		filmsList.setModel(filmsModel);
		
		findList = new JList<>();
		findList.setName("findList");
		findList.setBounds(221, 185, 203, 37);
		findList.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		findList.setModel(findModel);
		
		scrollFilmsListPane = new JScrollPane();
		scrollFilmsListPane.setBounds(10, 34, 414, 79);
		scrollFilmsListPane.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		scrollFilmsListPane.setViewportView(filmsList);
		contentPane.add(scrollFilmsListPane);
		
		scrollFindPane = new JScrollPane();
		scrollFindPane.setBounds(221, 185, 203, 37);
		scrollFindPane.setBackground(new Color(192, 192, 192));
		scrollFindPane.setViewportView(findList);
		contentPane.add(scrollFindPane);
		
		dateLabel = new JLabel("Date:");
		dateLabel.setBounds(339, 11, 46, 14);
		contentPane.add(dateLabel);
		
		filmDate = new JTextField();
		filmDate.setBounds(367, 8, 57, 20);
		filmDate.setName("filmDate");
		filmDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndFilmAndDateNotEmpty = !filmDate.getText().trim().isEmpty() && !filmName.getText().trim().isEmpty() && !filmId.getText().trim().isEmpty();
				boolean idNotEmptyAndFilmAndDateEmpty = !filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty();
				boolean filmNotEmptyAndIdAndDateEmpty = filmId.getText().trim().isEmpty() && !filmName.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty();
				boolean dateNotEmptyAndIdAndFilmEmpty = filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && !filmDate.getText().trim().isEmpty();
				boolean noSelection = filmsList.isSelectionEmpty();
				checkFilmDate(idAndFilmAndDateNotEmpty, idNotEmptyAndFilmAndDateEmpty, filmNotEmptyAndIdAndDateEmpty,
						dateNotEmptyAndIdAndFilmEmpty, noSelection);
			}
		});
		contentPane.add(filmDate);
		filmDate.setColumns(10);
		
		errorLabel = new JLabel("");
		errorLabel.setName("errorLabel");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setBounds(10, 230, 414, 20);
		contentPane.add(errorLabel);
	}

	@Override
	public void filmAdded(int id, String name, int date) {
		SwingUtilities.invokeLater(() -> filmsModel.addElement(id+","+name+","+date));
	}

	@Override
	public void filmDeletedById(int id, int position) {
		SwingUtilities.invokeLater(() -> filmsModel.removeElementAt(position));
	}

	@Override
	public void filmsDeletedByName(List<Film> deletedFilms) {
		SwingUtilities.invokeLater(() -> deletedFilms.forEach(f -> filmsModel.removeElement(f.getId()+","+f.getName()+","+f.getDate())));
	}

	@Override
	public void filmsDeletedByDate(List<Film> deletedFilms) {
		SwingUtilities.invokeLater(() -> deletedFilms.forEach(f -> filmsModel.removeElement(f.getId()+","+f.getName()+","+f.getDate())));
	}

	@Override
	public void filmUpdatedById(int oldId, int newId, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = filmsModel.get(position);
			filmsModel.removeElementAt(position);
			filmsModel.insertElementAt(newId+","+element.split(",")[1]+","+element.split(",")[2], position);
		});
	}

	@Override
	public void filmUpdatedByName(int id, String name, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = filmsModel.getElementAt(position);
			filmsModel.removeElementAt(position);
			filmsModel.insertElementAt(element.split(",")[0]+","+name+","+element.split(",")[2], position);
		});
	}

	@Override
	public void filmUpdatedByDate(int id, int date, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = filmsModel.getElementAt(position);
			filmsModel.removeElementAt(position);
			filmsModel.insertElementAt(element.split(",")[0]+","+element.split(",")[1]+","+date, position);
		});
	}

	@Override
	public void filmFound(int id, String name, int date) {
		SwingUtilities.invokeLater(() -> findModel.addElement(id+","+name+","+date));
	}
	
	public void setStreamingController(StreamingController streamingController) {
		SwingUtilities.invokeLater(() -> this.streamingController = streamingController);
	}
	
	protected void checkFilmAdd() {
		try {
			int id = Integer.parseInt(filmId.getText().trim());
			String name = filmName.getText();
			int date = Integer.parseInt(filmDate.getText().trim());
			streamingController.addFilm(this, id, name, date);
			errorLabel.setText("");
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		filmId.setText("");
		filmName.setText("");
		filmDate.setText("");
		filmAdd.setEnabled(false);
	}
	
	protected void checkFilmRemove() {
		if(!filmName.getText().trim().isEmpty()) {
			streamingController.deleteFilmsByName(this, filmName.getText());
		} else if(!filmDate.getText().trim().isEmpty()) {
			streamingController.deleteFilmsByDate(this, Integer.parseInt(filmDate.getText().trim()));
		} else {
			String element = filmsModel.getElementAt(filmsList.getSelectedIndex());
			int elementId = Integer.parseInt(element.split(",")[0]);
			streamingController.deleteFilmById(this, elementId, filmsList.getSelectedIndex());
		}
		
		filmId.setText("");
		filmName.setText("");
		filmDate.setText("");
		filmRemove.setEnabled(false);
	}
	
	protected void checkFilmUpdate() {
		String element = filmsModel.getElementAt(filmsList.getSelectedIndex());
		
		try {
			if(!filmName.getText().trim().isEmpty()) {
				int elementId = Integer.parseInt(element.split(",")[0]);
				streamingController.updateFilmByName(this, elementId, filmName.getText(), filmsList.getSelectedIndex());
			} else if(!filmDate.getText().trim().isEmpty()){
				int elementId = Integer.parseInt(element.split(",")[0]);
				streamingController.updateFilmDate(this, elementId, Integer.parseInt(filmDate.getText().trim()), 
						filmsList.getSelectedIndex());
			}
			else {
				int elementId = Integer.parseInt(element.split(",")[0]);
				streamingController.updateFilmById(this, elementId, Integer.parseInt(filmId.getText().trim()), 
						filmsList.getSelectedIndex());
			}
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		
		filmId.setText("");
		filmName.setText("");
		filmDate.setText("");
		filmUpdate.setEnabled(false);
	}
	
	protected void checkFilmFind() {
		findModel.removeAllElements();
		if(filmId.getText().trim().isEmpty() && filmName.getText().trim().isEmpty() && filmDate.getText().trim().isEmpty()) {
			streamingController.searchAllFilms(this);
		} else if(!filmId.getText().trim().isEmpty()) {
				streamingController.searchFilmById(this, Integer.parseInt(filmId.getText().trim()));
				filmId.setText("");
		} else if(!filmDate.getText().trim().isEmpty()) {
			streamingController.searchFilmsByDate(this, Integer.parseInt(filmDate.getText().trim()));
			filmDate.setText("");
		}
		else {
			streamingController.searchFilmsByName(this, filmName.getText());
			filmName.setText("");
		}
	}
	
	protected void checkFilmId(boolean idAndFilmAndDateNotEmpty, boolean idNotEmptyAndFilmAndDateEmpty,
			boolean filmNotEmptyAndIdAndDateEmpty, boolean dateNotEmptyAndIdAndFilmEmpty, boolean noSelection) {
		filmAdd.setEnabled(idAndFilmAndDateNotEmpty);
		filmUpdate.setEnabled((filmNotEmptyAndIdAndDateEmpty || idNotEmptyAndFilmAndDateEmpty || dateNotEmptyAndIdAndFilmEmpty) && !noSelection);
	}
	
	protected void checkFilmName(boolean idAndFilmAndDateNotEmpty, boolean idNotEmptyAndFilmAndDateEmpty,
			boolean filmNotEmptyAndIdAndDateEmpty, boolean dateNotEmptyAndIdAndFilmEmpty, boolean noSelection) {
		filmAdd.setEnabled(idAndFilmAndDateNotEmpty);
		filmUpdate.setEnabled((filmNotEmptyAndIdAndDateEmpty || idNotEmptyAndFilmAndDateEmpty || dateNotEmptyAndIdAndFilmEmpty) && !noSelection);
		filmRemove.setEnabled(filmNotEmptyAndIdAndDateEmpty);
	}
	
	protected void checkFilmDate(boolean idAndFilmAndDateNotEmpty, boolean idNotEmptyAndFilmAndDateEmpty,
			boolean filmNotEmptyAndIdAndDateEmpty, boolean dateNotEmptyAndIdAndFilmEmpty, boolean noSelection) {
		filmAdd.setEnabled(idAndFilmAndDateNotEmpty);
		filmUpdate.setEnabled((filmNotEmptyAndIdAndDateEmpty || idNotEmptyAndFilmAndDateEmpty || dateNotEmptyAndIdAndFilmEmpty) && !noSelection);
		filmRemove.setEnabled(dateNotEmptyAndIdAndFilmEmpty);
	}
	
	protected void checkFilmsList(boolean noSelection, boolean idNotEmpty, boolean filmNotEmpty) {
		filmUpdate.setEnabled(!(!idNotEmpty && !filmNotEmpty));
		filmRemove.setEnabled(!noSelection);
	}

}
