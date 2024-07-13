package minitext;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;


public class MiniText extends JFrame {

	//automatisch ueber Eclipse eingefuegt
	private static final long serialVersionUID = -4622810145518216095L;
	//fuer das Eingabefeld
	private JEditorPane eingabeFeld;
	//fuer das Format
	//wir verwenden HTML
	private HTMLEditorKit htmlFormat;
	
	
	//fuer die Aktionen
	private MeineAktionen 	oeffnenAct, speichernAct, beendenAct, neuAct,
							druckenAct, speichernUnterAct, webOeffnenAct;
	
	//fuer die Einsendeaufgabe 1
	//fuer die Aktion
	private MeineAktionen infoAct;
	
	//fuer die Datei
	private File datei;
	
	//fuer das Kontextmenue
	private JPopupMenu kontext;

	//eine innere Klasse fuer die Action-Objekte
	//Sie ist von der Klasse AbstractAction abgeleitet
	class MeineAktionen extends AbstractAction {
		//automatisch ueber Eclipse ergaenzt
		private static final long serialVersionUID = 5736947176597361976L;

		//der Konstruktor
		public MeineAktionen(String text, ImageIcon icon, String bildschirmtipp, KeyStroke shortcut, String actionText) {
			//den Konstruktor der uebergeordneten Klasse mit dem Text und dem Icon aufrufen
			super(text, icon);
			//die Beschreibung setzen fuer den Bildschirmtipp
			putValue(SHORT_DESCRIPTION, bildschirmtipp);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}
		
		//die ueberschriebene Methode actionPerformed()
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("neu"))
				dateiNeu();
			if (e.getActionCommand().equals("laden"))
				dateiLaden();
			if (e.getActionCommand().equals("speichern"))
				dateiSpeichern();
			if (e.getActionCommand().equals("beenden"))
				beenden();
			if (e.getActionCommand().equals("drucken")) {
				//wurde die Aktion ueber eine Schaltflaeche gestartet?
				//dann direkt drucken
				if (e.getSource() instanceof JButton) 
					drucken(false);
				//wurde die Aktion ueber einen Menueeintrag gestartet?
				//dann erst den Dialog zeigen
				if (e.getSource() instanceof JMenuItem) 
					drucken(true);
			}
			if (e.getActionCommand().equals("speichernUnter"))
				dateiSpeichernUnter();
			if (e.getActionCommand().equals("webladen"))
				webLaden();
			if (e.getActionCommand().equals("info"))
				infoZeigen();
		}
	}
	
	//eine innere Klasse mit dem Listener fuer das Kontextmenue
	//sie ist von der Adapterklasse MouseAdapter abgeleitet
	class MeinKontextMenuListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			//die Methode der uebergeordneten Klasse aufrufen
			super.mouseReleased(e);
			//wurde die Maustaste benutzt, die fuer das Anzeigen des Kontextmenues festgelegt ist?
			if (e.isPopupTrigger()) 
				//dann das Kontextmenue anzeigen
				kontext.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	//der Konstruktor der Klasse MiniText
	public MiniText(String text) {
		super(text);
		//wir nehmen ein Border-Layout
		setLayout(new BorderLayout());
		
		//die Aktionen erstellen
		neuAct = new MeineAktionen("Neu...", new ImageIcon("icons/new24.gif"), "Erstellt ein neues Dokument", KeyStroke.getKeyStroke('N',InputEvent.CTRL_DOWN_MASK), "neu");
		oeffnenAct = new MeineAktionen("Oeffnen...", new ImageIcon("icons/open24.gif"), "Oeffnet ein vorhandenes Dokument", KeyStroke.getKeyStroke('O',InputEvent.CTRL_DOWN_MASK),"laden");
		speichernAct = new MeineAktionen("Speichern", new ImageIcon("icons/save24.gif"), "Speichert das aktuelle Dokument", KeyStroke.getKeyStroke('S',InputEvent.CTRL_DOWN_MASK), "speichern");
		beendenAct = new MeineAktionen("Beenden", null, "", null, "beenden");
		druckenAct = new MeineAktionen("Drucken...", new ImageIcon("icons/print24.gif"), "Druckt das aktuelle Dokument", KeyStroke.getKeyStroke('P',InputEvent.CTRL_DOWN_MASK), "drucken");
		speichernUnterAct = new MeineAktionen("Speichern unter...", null, "", null, "speichernUnter");
		webOeffnenAct = new MeineAktionen("Webseite...", new ImageIcon("icons/webComponent24.gif"), "Oeffnet eine Webseite", null, "webladen");
		
		//fuer Einsendeaufgabe 1
		//die Aktion fuer Info-Dialog erstellen
		infoAct = new MeineAktionen("Info", new ImageIcon("icons/information24.gif"),"Information ueber die Anwendung", KeyStroke.getKeyStroke('I',InputEvent.CTRL_DOWN_MASK), "info");
		
		
		//das Menue erzeugen
		menu();
		//die Symbolleiste oben einfuegen
		add(symbolleiste(), BorderLayout.NORTH);
		//das Kontextmenue erzeugen
		kontextMenu();

		//das Eingabefeld erzeugen und mit dem EditorKit verbinden 
		eingabeFeld = new JEditorPane();
		htmlFormat = new HTMLEditorKit();
		eingabeFeld.setEditorKit(htmlFormat);
		//das Eingabefeld mit dem Listener fuer das Kontextmenue verbinden
		eingabeFeld.addMouseListener(new MeinKontextMenuListener());

		//gegebenenfalls mit Scrollbars in der Mitte einfuegen
		add(new JScrollPane(eingabeFeld), BorderLayout.CENTER);
		
		//anzeigen und Standardverhalten festlegen
		//das Fenster ist maximiert
		setExtendedState(MAXIMIZED_BOTH);
		//die Mindestgroesse setzen
		setMinimumSize(new Dimension(600,200));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		//das Eingabefeld bekommt den Fokus
		eingabeFeld.requestFocus();
	}
	
	//die Methode erstellt die Menueleiste
	private void menu() {
		JMenuBar menue = new JMenuBar();
		//das Menue Datei
		JMenu dateiMenue = new JMenu("Datei");
		
		//das Untermenue Oeffnen
		//es ist vom Typ Menu, da es weitere Untereintraege haben soll
		JMenu dateiOeffnen = new JMenu("Oeffnen");
		//die Eintraege werden jetzt zum Untermenue hinzugefuegt
		//das "normale" Oeffnen
		dateiOeffnen.add(oeffnenAct);
		//das Oeffnen aus dem Internet
		dateiOeffnen.add(webOeffnenAct);
		
		//die Eintraege werden direkt ueber Action-Objekte erzeugt
		//der Eintrag Neu
		dateiMenue.add(neuAct);
		//das komplette Untermenue einfuegen
		dateiMenue.add(dateiOeffnen);
		
		//der Eintrag Speichern
		dateiMenue.add(speichernAct);
		//der Eintrag Speichern unter
		dateiMenue.add(speichernUnterAct);
		
		//eine Trennlinie
		dateiMenue.addSeparator();
		//der Eintrag Drucken
		dateiMenue.add(druckenAct);
		
		//eine Trennlinie
		dateiMenue.addSeparator();
		//der Eintrag Beenden
		dateiMenue.add(beendenAct);
		
		
		//fuer Einsendeaufgabe 2
		//neues Menue Zwischenablage
		JMenu zwischenablageMenue = new JMenu("Zwischenablage");
		//der Eintrag Info
		zwischenablageMenue.add(cut());
		zwischenablageMenue.add(copy());
		zwischenablageMenue.add(paste());
		
		
		//fuer Einsendeaufgabe 1
		//neues Menue Hilfe
		JMenu hilfeMenue = new JMenu("Hilfe");
		//der Eintrag Info
		hilfeMenue.add(infoAct);
		
		
		//das gesamte Menue hinzufuegen
		menue.add(dateiMenue);
		//fuer Einsendeaufgabe 2
		//Menue-Punkt "Zwischenablage" in Menue-Bar hinzufuegen
		menue.add(zwischenablageMenue);
		//fuer Einsendeaufgabe 1
		//Menue-Punkt "Hilfe" in Menue-Bar hinzufuegen
		menue.add(hilfeMenue);
		
		this.setJMenuBar(menue);
	}
	
	//die Methode erstellt die Symbolleiste
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();

		//die Symbole werden ebenfalls direkt ueber Action-Objekte erzeugt
		//das Symbol Neu
		leiste.add(neuAct);
		//das Symbol Oeffnen
		leiste.add(oeffnenAct);
		//das Symbol fuer das Laden von Webseiten
		leiste.add(webOeffnenAct);
		//das Symbol Speichern
		leiste.add(speichernAct);
		//das Symbol Drucken
		leiste.add(druckenAct);
		
		//fuer Einsendeaufgabe 1
		//das Symbol Info
		leiste.add(infoAct);
					
		//etwas Platz einfuegen
		leiste.addSeparator();
		//ein Symbol fuer die fette Zeichenformatierung
		//ueber eine Standardaktion aus dem StyledEditorKit
		//die neue Aktion erzeugen
		//Vorsicht! Es gibt mehrere Klassen Action
		//richtig ist die aus javax.swing
		Action fettFormat = new StyledEditorKit.BoldAction();
		//den Bildschirmtipp und das Symbol setzen
		fettFormat.putValue(Action.SHORT_DESCRIPTION, "Fett formatieren");
		fettFormat.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/bold24.gif"));
		//und zur Leiste hinzufuegen
		leiste.add(fettFormat);
		
		//ein Symbol fuer die kursive Zeichenformatierung
		Action kursivFormat = new StyledEditorKit.ItalicAction();
		kursivFormat.putValue(Action.SHORT_DESCRIPTION, "Kursiv formatieren");
		kursivFormat.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/italic24.gif"));
		leiste.add(kursivFormat);
		
		//ein Symbol fuer die unterstrichene Zeichenformatierung
		Action unterstrichenFormat = new StyledEditorKit.UnderlineAction();
		unterstrichenFormat.putValue(Action.SHORT_DESCRIPTION, "Unterstrichen formatieren");
		unterstrichenFormat.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/underline24.gif"));
		leiste.add(unterstrichenFormat);
		
		//Platz einfuegen
		leiste.addSeparator();
		
		//das Symbol fuer die linksbuendige Ausrichtung
		//an den Konstruktor von StyledEditorKit.AlignmentAction() wird eine Beschreibung und die
		//gewuenschte Ausrichtung uebergeben
		Action linksAbsatz = new StyledEditorKit.AlignmentAction("Linksbuendig", StyleConstants.ALIGN_LEFT);
		linksAbsatz.putValue(Action.SHORT_DESCRIPTION, "Linksbuendig ausrichten");
		linksAbsatz.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/alignLeft24.gif"));
		leiste.add(linksAbsatz);

		//das Symbol fuer die zentrierte Ausrichtung
		Action zentriertAbsatz = new StyledEditorKit.AlignmentAction("Zentriert", StyleConstants.ALIGN_CENTER);
		zentriertAbsatz.putValue(Action.SHORT_DESCRIPTION, "Zentriert ausrichten");
		zentriertAbsatz.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/alignCenter24.gif"));
		leiste.add(zentriertAbsatz);

		//das Symbol fuer die rechtsbuendige Ausrichtung
		Action rechtsAbsatz = new StyledEditorKit.AlignmentAction("Rechts", StyleConstants.ALIGN_RIGHT);
		rechtsAbsatz.putValue(Action.SHORT_DESCRIPTION, "Rechtsbuendig ausrichten");
		rechtsAbsatz.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/alignRight24.gif"));
		leiste.add(rechtsAbsatz);
		
		//das Symbol fuer den Blocksatz
		Action blockAbsatz = new StyledEditorKit.AlignmentAction("Blocksatz", StyleConstants.ALIGN_JUSTIFIED);
		blockAbsatz.putValue(Action.SHORT_DESCRIPTION, "Im Blocksatz ausrichten");
		blockAbsatz.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/alignJustify24.gif"));
		leiste.add(blockAbsatz); 
		
		
		//fuer Einsendeaufgabe 2
		//Platz einfuegen
		leiste.addSeparator();
		//das Symbol Ausschneiden
		leiste.add(cut());
		//das Symbol Kopieren
		leiste.add(copy());
		//das Symbol Einfuegen
		leiste.add(paste());
		
		
		//die komplette Leiste zurueckgeben
		return (leiste);
	}
	
	//die Methode erstellt das Kontextmenue
	private void kontextMenu() {
		//kontext ist eine Instanzvariable vom Typ JPopupMenu
		kontext = new JPopupMenu();
		//den Eintrag Neu hinzufuegen
		kontext.add(neuAct);
		//die Eintraege zum Oeffnen
		kontext.add(oeffnenAct);
		kontext.add(webOeffnenAct);
		
		//fuer Einsendeaufgabe 2
		//Platz einfuegen
		kontext.addSeparator();
		//Ausschneiden
		kontext.add(cut());
		//Kopieren
		kontext.add(copy());
		//Einfuegen
		kontext.add(paste());
	}

	//die Methode fuer die Funktion Neu
	private void dateiNeu() {
		//eine Abfrage
		if(JOptionPane.showConfirmDialog(this, "Wollen Sie wirklich ein neues Dokument anlegen?","Neues Dokument", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			//den Text loeschen
			eingabeFeld.setText("");
			//datei wieder auf null setzen, damit klar ist, dass die Datei noch nicht gespeichert wurde
			datei = null;
			
			//fuer Einsendeaufgabe 3
			//legt man beim Erstellen eines neuen Dokuments einen Standardnamen fest
			this.setTitle("MiniText   ohneName");
		}
	}

	//die Methode zum Laden einer Datei
	private void dateiLaden() {
		//fuer den Dialog
		MiniTextDialoge dialog = new MiniTextDialoge();
		
		//die Datei ueber den Dialog Oeffnen beschaffen
		//es wird erst einmal in einer lokalen Variablen zwischengespeichert
		File dateiLokal = dialog.oeffnenDialogZeigen();
		
		//wenn eine Datei ausgewaehlt wurde, einlesen
		//die Methode read() erzwingt eine Ausnahmebehandlung
		if (dateiLokal != null) {
			try {
				eingabeFeld.read(new FileReader(dateiLokal), null);
				//datei neu setzen
				datei = dateiLokal;
				
				//fuer Einsendeaufgabe 3
				//beim Laden eines Dokuments erhalt man den Pfad aus einer Klassenvariablen vom Typ File
				//fuegt man den Pfad zum Programmnamen hinzu und legt man ihn als Titel fest
				this.setTitle("MiniText   " + datei.getAbsolutePath());
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Beim Laden hat es ein Problem gegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	//die Methode zum Speichern einer Datei
	private void dateiSpeichern() {
		//WICHTIG!!!!
		//datei muss als Instanzvariable vereinbart werden
		//wenn die Datei noch nicht gespeichert wurde, ist datei noch null
		if (datei == null) {
			//fuer den Dialog
			MiniTextDialoge dialog = new MiniTextDialoge();
			//die Datei ueber den Dialog Speichern beschaffen
			datei = dialog.speichernDialogZeigen();
		}
		//ist datei ungleich null, dann wurde vorher schon einmal gespeichert
		//bzw. im Dialog eine Datei ausgewaehlt
		if (datei != null) {
			//die Ausnahmebehandlung ist zwingend erforderlich
			try {
				//den Output-Stream fuer die Datei beschaffen
				OutputStream output = new FileOutputStream(datei);
				//das Dokument komplett speichern
				htmlFormat.write(output, eingabeFeld.getDocument(), 0, eingabeFeld.getDocument().getLength());
				
				//fuer Einsendeaufgabe 3
				//beim Speichern eines Dokuments erhalt man den Pfad aus einer Klassenvariablen vom Typ File
				//fuegt man den Pfad zum Programmnamen hinzu und legt man ihn als Titel fest
				this.setTitle("MiniText   " + datei.getAbsolutePath());
			}
			catch (IOException | BadLocationException e) {
				JOptionPane.showMessageDialog(this, "Beim Speichern hat es ein Problem gegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//die Methode oeffnet den Speichern-Dialog und ruft die Methode dateiSpeichern() auf,
	//wenn ein neuer Name ausgewaehlt wurde
	private void dateiSpeichernUnter() {
		//fuer den Dialog
		MiniTextDialoge dialog = new MiniTextDialoge();
		//die Datei ueber den Dialog Speichern beschaffen
		File dateiLokal = dialog.speichernDialogZeigen();
		//ist dateiLokal ungleich null, dann wurde ein neuer Name ausgewaehlt
		if (dateiLokal != null) {
			datei = dateiLokal;
			dateiSpeichern();
		}
	}
	
	
	//die Methode beendet die Anwendung nach einer Abfrage
	private void beenden() {
		if(JOptionPane.showConfirmDialog(this, "Sind Sie sicher?","Anwendung schliessen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			System.exit(0);
	}
	
	//die Methode druckt das Dokument
	//wenn dialogZeigen true ist, wird erst der Drucken-Dialog eingeblendet
	private void drucken(boolean dialogZeigen) {
		try {
			if (dialogZeigen == true)
				//mit Dialog drucken
				eingabeFeld.print();
			else
				//ohne Dialog drucken
				//dazu muss das dritte Argument false sein
				eingabeFeld.print(null, null, false, null, null, true);
			}
		catch (PrinterException e) {
			JOptionPane.showMessageDialog(this, "Beim Drucken hat es ein Problem gegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//die Methode oeffnet ein Dokument aus dem Internet
	private void webLaden() {
		//fuer die URL
		String adresse;
		//einen Dialog zur Eingabe erzeugen
		adresse = JOptionPane.showInputDialog(this, "Bitte geben Sie die URL der Seite ein:");
		//wurde etwas eingegeben?
		if (adresse != null) {
			//den Text im Feld loeschen
			eingabeFeld.setText("");
			try {
				//den Inhalt anzeigen
				eingabeFeld.setPage(adresse);
				//es ist keine Datei mehr geladen
				datei = null;
				
				//fuer Einsendeaufgabe 3
				//beim Laden einer Webseite erhalt man die Adresse aus einer String-Variablen
				//fuegt man den Pfad zum Programmnamen hinzu und legt man ihn als Titel fest
				this.setTitle("MiniText   " + adresse);
			} 
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Beim Laden ist ein Problem aufgetreten.");
			}
		}
	}
	
	//fuer Einsendeaufgabe 1
	//die Methode oeffnet ein Dialog mit Info zur Anwendung
	//Eingabe: Nein
	//Ausgabe: Nein, der Standard-Informationsdialog wird angezeigt
	private void infoZeigen() {
		//Variable fuer Text
		String text;
		//Text fuer Dialog
		text = "MiniText Version 1.0.\nProgrammiert von: Ivan Stanchenko 2024.";
		//Standard-Informationsdialog
		JOptionPane.showMessageDialog(this, text, "Info", JOptionPane.INFORMATION_MESSAGE);
	}
	
	//fuer Einsendeaufgabe 2
	//die Methode erstellt ein neues Action-Object aus der Klasse DefaultEditorKit
	//um die Textschneidefunktion hinzuzufuegen
	//Eingabe: Nein 
	//Ausgabe: Action-Object fuer die Textschneidefunktion hinzufuegen
	private Action cut() {
		//erstellen eines neuen Action-Objekts der Klasse „DefaultEditorKit“
		Action cutText = new DefaultEditorKit.CutAction();
		//hinzufuegen von Variablen
		cutText.putValue(Action.SHORT_DESCRIPTION, "Text ausschneiden");
		cutText.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/cut24.gif"));
		cutText.putValue(Action.SMALL_ICON, new ImageIcon("icons/cut24.gif"));
		cutText.putValue(Action.NAME, "Cut");
		//rueckgabe eines Action-Objekts
		return cutText;
	}
	
	//fuer Einsendeaufgabe 2
	//die Methode erstellt ein neues Action-Object aus der Klasse DefaultEditorKit
	//um die Textkopierfunktion hinzuzufuegen
	//Eingabe: Nein
	//Ausgabe: Action-Object fuer die Textkopierfunktion hinzufuegen
	private Action copy() {
		//erstellen eines neuen Action-Objekts der Klasse „DefaultEditorKit“
		Action copyText = new DefaultEditorKit.CopyAction();
		//hinzufuegen von Variablen
		copyText.putValue(Action.SHORT_DESCRIPTION, "Text kopieren");
		copyText.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/copy24.gif"));
		copyText.putValue(Action.SMALL_ICON, new ImageIcon("icons/copy24.gif"));
		copyText.putValue(Action.NAME, "Copy");
		//rueckgabe eines Action-Objekts
		return copyText;
	}
		
	//fuer Einsendeaufgabe 2
	//die Methode erstellt ein neues Action-Object aus der Klasse DefaultEditorKit
	//um die Texteinfuegungsfunktion hinzuzufuegen
	//Eingabe: Nein
	//Ausgabe: Action-Object fuer die Texteinfuegungsfunktion hinzufuegen
	private Action paste() {
		//erstellen eines neuen Action-Objekts der Klasse „DefaultEditorKit“
		Action pasteText = new DefaultEditorKit.PasteAction();
		//hinzufuegen von Variablen
		pasteText.putValue(Action.SHORT_DESCRIPTION, "Text einfuegen");
		pasteText.putValue(Action.LARGE_ICON_KEY, new ImageIcon("icons/paste24.gif"));
		pasteText.putValue(Action.SMALL_ICON, new ImageIcon("icons/paste24.gif"));
		pasteText.putValue(Action.NAME, "Paste");
		//rueckgabe eines Action-Objekts
		return pasteText;
	}
}


