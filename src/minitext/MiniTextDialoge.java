package minitext;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class MiniTextDialoge {
	//eine innere Klasse fuer den Filter
	//sie erbt von der Klasse FileFilter
	class MeinFilter extends FileFilter {
		//die Methode accept() definiert den Filter
		//hier fuer Dateien mit den Erweiterungen .html und .htm
		//sowie fuer Ordner
		@Override
		public boolean accept(File f) {
			//fuer den kompakteren Zugriff
			String name = f.getName().toLowerCase();
			if (f.isDirectory())
				return true;
			if (name.endsWith(".htm"))
				return true;
			if (name.endsWith(".html"))
				return true;
				
			return false;
		}

		//die Methode legt den Namen fuer den Filter fest
		@Override
		public String getDescription() {
			return "HTML-Dateien";
		}
	}

	//die Methode zeigt den oeffnen-Dialog fuer eine Datei
	public File oeffnenDialogZeigen() {
		//einen neuen Dialog erzeugen
		JFileChooser oeffnenDialog = new JFileChooser();
		//den Filter setzen
		oeffnenDialog.setFileFilter(new MeinFilter());
		//den Filter fuer Alle Dateien deaktivieren
		oeffnenDialog.setAcceptAllFileFilterUsed(false);
						
		//den Dialog anzeigen und den Status holen
		int status = oeffnenDialog.showOpenDialog(null);
					
		//wurde auf OK geklickt, dann die ausgewaehlte Datei als Typ File zurueckliefern
		if (status == JFileChooser.APPROVE_OPTION)
			return (oeffnenDialog.getSelectedFile());
		else 
			return null;
	}
				
	//die Methode zeigt den Speichern-Dialog fuer eine Datei
	//Sie arbeitet im Wesentlichen so wie die Methode fuer den oeffnen-Dialog
	public File speichernDialogZeigen() {
		//einen neuen Dialog erzeugen
		JFileChooser speichernDialog = new JFileChooser();
		//den Filter setzen
		speichernDialog.setFileFilter(new MeinFilter());
		//den Filter fuer Alle Dateien deaktivieren
		speichernDialog.setAcceptAllFileFilterUsed(false);
					
		//den Dialog anzeigen und den Status holen
		int status = speichernDialog.showSaveDialog(null);
				
		//wurde auf OK geklickt, dann die ausgewaehlte Datei als Typ File zurueckliefern
		if (status == JFileChooser.APPROVE_OPTION)
			return (speichernDialog.getSelectedFile());
		else 
			return null;
	}
}
