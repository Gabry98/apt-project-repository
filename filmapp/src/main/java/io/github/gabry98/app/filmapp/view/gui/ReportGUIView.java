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
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.view.ReportView;

public class ReportGUIView extends JFrame implements ReportView {

	private static final Logger LOGGER = LogManager.getLogger(ReportGUIView.class);
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField reportId;
	private JTextField actorId;
	private JTextField filmId;
	private JList<String> reportsList;
	private JList<String> findList;
	private DefaultListModel<String> reportsModel;
	private DefaultListModel<String> findModel;
	private transient StreamingController streamingController;
	private JScrollPane scrollReportsListPane;
	private JScrollPane scrollFindPane;
	private JLabel errorLabel;
	private JButton reportAdd;
	private JButton reportUpdate;
	private JButton reportRemove;
	private JButton reportFind;
	private JLabel dateLabel;

	/**
	 * Launch the application.
	 */
	@Generated
	public static void main(String[] args) {
		EventQueue.invokeLater(ReportGUIView::run);
	}
	
	@Generated
	private static void run() {
		try {
			ReportGUIView frame = new ReportGUIView();
			frame.setVisible(true);
		} catch (Exception e) {
			LOGGER.error("An Exception has been thrown during initialization of the View: {}", e.getMessage());
		}
	}

	/**
	 * Create the frame.
	 */
	public ReportGUIView() {
		this.setTitle("Report View");
		this.setName("Report View");
		reportsModel = new DefaultListModel<>();
		findModel = new DefaultListModel<>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		reportAdd = new JButton("ADD");
		reportAdd.setEnabled(false);
		reportAdd.setBounds(113, 131, 86, 23);
		reportAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkReportAdd();
			}
		});
		contentPane.add(reportAdd);
		
		reportUpdate = new JButton("UPDATE");
		reportUpdate.setEnabled(false);
		reportUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkReportUpdate();
			}
		});
		reportUpdate.setBounds(10, 158, 89, 23);
		contentPane.add(reportUpdate);
		
		reportRemove = new JButton("REMOVE");
		reportRemove.setEnabled(false);
		reportRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkReportRemove();
			}
		});
		reportRemove.setBounds(110, 196, 89, 23);
		contentPane.add(reportRemove);
		
		reportFind = new JButton("FIND");
		reportFind.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkReportFind();
			}
		});
		reportFind.setBounds(211, 158, 89, 23);
		contentPane.add(reportFind);
		
		reportId = new JTextField();
		reportId.setName("reportId");
		reportId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndActorAndFilmNotEmpty = !reportId.getText().trim().isEmpty() && !actorId.getText().trim().isEmpty() && !filmId.getText().trim().isEmpty();
				boolean idNotEmptyAndActorAndFilmEmpty = !reportId.getText().trim().isEmpty() && actorId.getText().trim().isEmpty() && filmId.getText().trim().isEmpty();
				checkIds(idAndActorAndFilmNotEmpty, idNotEmptyAndActorAndFilmEmpty);
			}
		});
		reportId.setBounds(32, 8, 86, 20);
		contentPane.add(reportId);
		reportId.setColumns(10);
		
		JLabel idLabel = new JLabel("Id:");
		idLabel.setBounds(10, 11, 46, 14);
		contentPane.add(idLabel);
		
		JLabel nameLabel = new JLabel("actorId:");
		nameLabel.setBounds(128, 11, 46, 14);
		contentPane.add(nameLabel);
		
		dateLabel = new JLabel("filmId:");
		dateLabel.setBounds(263, 11, 46, 14);
		contentPane.add(dateLabel);

		actorId = new JTextField();
		actorId.setName("actorId");
		actorId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndFilmAndDateNotEmpty = !actorId.getText().trim().isEmpty() && !reportId.getText().trim().isEmpty() && !filmId.getText().trim().isEmpty();
				boolean idNotEmptyAndFilmAndDateEmpty = !reportId.getText().trim().isEmpty() && actorId.getText().trim().isEmpty() && filmId.getText().trim().isEmpty();
				checkIds(idAndFilmAndDateNotEmpty, idNotEmptyAndFilmAndDateEmpty);
			}
		});
		actorId.setBounds(173, 8, 80, 20);
		contentPane.add(actorId);
		actorId.setColumns(10);
		
		reportsList = new JList<>();
		reportsList.setName("reportsList");
		reportsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean noSelection = reportsList.isSelectionEmpty();
				checkReportsList(noSelection);
			}
		});
		reportsList.setBounds(10, 113, 414, -70);
		reportsList.setBackground(new Color(192, 192, 192));
		reportsList.setForeground(new Color(0, 128, 0));
		reportsList.setModel(reportsModel);
		
		findList = new JList<>();
		findList.setName("findList");
		findList.setBounds(221, 185, 203, 37);
		findList.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		findList.setModel(findModel);
		
		scrollReportsListPane = new JScrollPane();
		scrollReportsListPane.setBounds(10, 34, 414, 79);
		scrollReportsListPane.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		scrollReportsListPane.setViewportView(reportsList);
		contentPane.add(scrollReportsListPane);
		
		scrollFindPane = new JScrollPane();
		scrollFindPane.setBounds(221, 185, 203, 37);
		scrollFindPane.setBackground(new Color(192, 192, 192));
		scrollFindPane.setViewportView(findList);
		contentPane.add(scrollFindPane);

		filmId = new JTextField();
		filmId.setBounds(301, 8, 123, 20);
		filmId.setName("filmId");
		filmId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndFilmAndDateNotEmpty = !filmId.getText().trim().isEmpty() && !actorId.getText().trim().isEmpty() && !reportId.getText().trim().isEmpty();
				boolean idNotEmptyAndFilmAndDateEmpty = !reportId.getText().trim().isEmpty() && actorId.getText().trim().isEmpty() && filmId.getText().trim().isEmpty();
				checkIds(idAndFilmAndDateNotEmpty, idNotEmptyAndFilmAndDateEmpty);
			}
		});
		contentPane.add(filmId);
		filmId.setColumns(10);
		
		errorLabel = new JLabel("");
		errorLabel.setName("errorLabel");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setBounds(10, 230, 414, 20);
		contentPane.add(errorLabel);
	}

	@Override
	public void reportAdded(int id, int actorId, int filmId) {
		SwingUtilities.invokeLater(() -> reportsModel.addElement(id+","+actorId+","+filmId));
	}

	@Override
	public void reportDeletedById(int id, int position) {
		SwingUtilities.invokeLater(() -> reportsModel.removeElementAt(position));
	}

	@Override
	public void reportUpdatedById(int oldId, int newId, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = reportsModel.get(position);
			reportsModel.removeElementAt(position);
			reportsModel.insertElementAt(newId+","+element.split(",")[1]+","+element.split(",")[2], position);
		});
	}

	@Override
	public void reportFound(int id, int actorId, int filmId) {
		SwingUtilities.invokeLater(() -> findModel.addElement(id+","+actorId+","+filmId));
	}

	@Override
	public void reportsDeletedByActorId(List<Report> deletedReports) {
		SwingUtilities.invokeLater(() -> deletedReports.forEach(d -> reportsModel.removeElement(d.getId()+","+d.getActorId()+","+d.getFilmId())));
	}

	@Override
	public void reportsDeletedByFilmId(List<Report> deletedReports) {
		SwingUtilities.invokeLater(() -> deletedReports.forEach(d -> reportsModel.removeElement(d.getId()+","+d.getActorId()+","+d.getFilmId())));
	}
	
	public void setStreamingController(StreamingController streamingController) {
		SwingUtilities.invokeLater(() -> this.streamingController = streamingController);
	}
	
	protected void checkIds(boolean idAndActorAndFilmNotEmpty, boolean idNotEmptyAndActorAndFilmEmpty) {
		reportAdd.setEnabled(idAndActorAndFilmNotEmpty);
		reportRemove.setEnabled(idNotEmptyAndActorAndFilmEmpty);	
	}

	protected void checkReportsList(boolean noSelection) {
		reportUpdate.setEnabled(!noSelection);
		reportRemove.setEnabled(!noSelection);
	}

	protected void checkReportFind() {
		findModel.removeAllElements();
		if(reportId.getText().trim().isEmpty() && actorId.getText().trim().isEmpty() && filmId.getText().trim().isEmpty()) {
			streamingController.searchAllReports(this);
		} else if(!reportId.getText().trim().isEmpty()) {
				streamingController.searchReportById(this, Integer.parseInt(reportId.getText().trim()));
				reportId.setText("");
		} else if(!actorId.getText().trim().isEmpty()) {
			streamingController.searchReportsByActorId(this, Integer.parseInt(actorId.getText().trim()));
			actorId.setText("");
		}
		else {
			streamingController.searchReportsByFilmId(this, Integer.parseInt(filmId.getText().trim()));
			filmId.setText("");
		}
	}

	protected void checkReportRemove() {
		if(!actorId.getText().trim().isEmpty()) {
			streamingController.deleteReportByActorId(this, Integer.parseInt(actorId.getText().trim()));
		} else if(!filmId.getText().trim().isEmpty()) {
			streamingController.deleteReportByFilmId(this, Integer.parseInt(filmId.getText().trim()));
		} else {
			String element = reportsModel.getElementAt(reportsList.getSelectedIndex());
			int elementId = Integer.parseInt(element.split(",")[0]);
			streamingController.deleteReportById(this, elementId, reportsList.getSelectedIndex());
		}
		
		reportId.setText("");
		actorId.setText("");
		filmId.setText("");
		reportRemove.setEnabled(false);
	}

	protected void checkReportUpdate() {
		String element = reportsModel.getElementAt(reportsList.getSelectedIndex());
		
		try {
			int elementId = Integer.parseInt(element.split(",")[0]);
			streamingController.updateReportId(this, elementId, Integer.parseInt(reportId.getText().trim()), reportsList.getSelectedIndex());
			errorLabel.setText("");
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		reportId.setText("");
		reportUpdate.setEnabled(false);
	}

	protected void checkReportAdd() {
		try {
			int report = Integer.parseInt(reportId.getText().trim());
			int film = Integer.parseInt(filmId.getText().trim());
			int actor = Integer.parseInt(actorId.getText().trim());
			streamingController.addReport(this, report, actor, film);
			errorLabel.setText("");
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		reportId.setText("");
		actorId.setText("");
		filmId.setText("");
		reportAdd.setEnabled(false);
	}
}
