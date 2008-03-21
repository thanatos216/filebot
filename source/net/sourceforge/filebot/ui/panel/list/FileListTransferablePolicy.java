
package net.sourceforge.filebot.ui.panel.list;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.filebot.FileBotUtil;
import net.sourceforge.filebot.FileFormat;
import net.sourceforge.filebot.torrent.Torrent;
import net.sourceforge.filebot.ui.FileBotList;
import net.sourceforge.filebot.ui.transferablepolicies.FileTransferablePolicy;


class FileListTransferablePolicy extends FileTransferablePolicy {
	
	private FileBotList list;
	
	
	public FileListTransferablePolicy(FileBotList list) {
		this.list = list;
	}
	

	@Override
	protected boolean accept(File file) {
		return file.isFile() || file.isDirectory();
	}
	

	@Override
	protected void clear() {
		list.getModel().clear();
	}
	

	@Override
	protected void load(List<File> files) {
		if (files.size() > 1) {
			list.setTitle(FileFormat.getName(files.get(0).getParentFile()));
		}
		
		if (FileBotUtil.containsOnlyFolders(files)) {
			loadFolderList(files);
		} else if (FileBotUtil.containsOnlyTorrentFiles(files)) {
			loadTorrentList(files);
		} else {
			super.load(files);
		}
	}
	

	private void loadFolderList(List<File> folders) {
		if (folders.size() == 1) {
			list.setTitle(FileFormat.getName(folders.get(0)));
		}
		
		for (File folder : folders) {
			for (File file : folder.listFiles()) {
				list.getModel().add(FileFormat.formatName(file));
			}
		}
	}
	

	private void loadTorrentList(List<File> torrentFiles) {
		try {
			List<Torrent> torrents = new ArrayList<Torrent>(torrentFiles.size());
			
			for (File file : torrentFiles) {
				torrents.add(new Torrent(file));
			}
			
			if (torrentFiles.size() == 1) {
				list.setTitle(FileFormat.getNameWithoutExtension(torrents.get(0).getName()));
			}
			
			for (Torrent torrent : torrents) {
				for (Torrent.Entry entry : torrent.getFiles()) {
					list.getModel().add(FileFormat.getNameWithoutExtension(entry.getName()));
				}
			}
		} catch (IOException e) {
			// should not happen
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
		}
	}
	

	@Override
	protected void load(File file) {
		list.getModel().add(FileFormat.formatName(file));
	}
	

	@Override
	public String getDescription() {
		return "files, folders and torrents";
	}
	
}
