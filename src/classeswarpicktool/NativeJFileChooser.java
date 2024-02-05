/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classeswarpicktool;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author bug
 */
public class NativeJFileChooser extends JFileChooser {

    public static final boolean FX_AVAILABLE;

    private java.util.List<File> currentFiles;

    private FileChooser fileChooser;

    private File currentFile;

    private DirectoryChooser directoryChooser;

    static {
        boolean isFx;
        try {
            Class.forName("javafx.stage.FileChooser");
            isFx = true;
            JFXPanel jFXPanel = new JFXPanel();
        } catch (ClassNotFoundException e) {
            isFx = false;
        }
        FX_AVAILABLE = isFx;
    }

    public NativeJFileChooser() {
        initFxFileChooser((File) null);
    }

    public NativeJFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    public NativeJFileChooser(File currentDirectory) {
        super(currentDirectory);
        initFxFileChooser(currentDirectory);
    }

    public NativeJFileChooser(FileSystemView fsv) {
        super(fsv);
        initFxFileChooser(fsv.getDefaultDirectory());
    }

    public NativeJFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        initFxFileChooser(currentDirectory);
    }

    public NativeJFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    public int showOpenDialog(final Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showOpenDialog(parent);
        }
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                if (parent != null) {
                    parent.setEnabled(false);
                }
                if (NativeJFileChooser.this.isDirectorySelectionEnabled()) {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
                } else if (NativeJFileChooser.this.isMultiSelectionEnabled()) {
                    NativeJFileChooser.this.currentFiles = NativeJFileChooser.this.fileChooser.showOpenMultipleDialog(null);
                } else {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showOpenDialog(null);
                }
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (parent != null) {
                parent.setEnabled(true);
            }
        }
        if (isMultiSelectionEnabled()) {
            if (this.currentFiles != null) {
                return 0;
            }
            return 1;
        }
        if (this.currentFile != null) {
            return 0;
        }
        return 1;
    }

    public int showSaveDialog(final Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showSaveDialog(parent);
        }
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                if (parent != null) {
                    parent.setEnabled(false);
                }
                if (NativeJFileChooser.this.isDirectorySelectionEnabled()) {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
                } else {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showSaveDialog(null);
                }
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (parent != null) {
                parent.setEnabled(true);
            }
        }
        if (this.currentFile != null) {
            return 0;
        }
        return 1;
    }

    public int showDialog(Component parent, String approveButtonText) {
        if (!FX_AVAILABLE) {
            return super.showDialog(parent, approveButtonText);
        }
        return showOpenDialog(parent);
    }

    public File[] getSelectedFiles() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFiles();
        }
        if (this.currentFiles == null) {
            return null;
        }
        return this.currentFiles.<File>toArray(new File[this.currentFiles.size()]);
    }

    public File getSelectedFile() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFile();
        }
        return this.currentFile;
    }

    public void setSelectedFiles(File[] selectedFiles) {
        if (!FX_AVAILABLE) {
            super.setSelectedFiles(selectedFiles);
            return;
        }
        if (selectedFiles == null || selectedFiles.length == 0) {
            this.currentFiles = null;
        } else {
            setSelectedFile(selectedFiles[0]);
            this.currentFiles = new ArrayList<>(Arrays.asList(selectedFiles));
        }
    }

    public void setSelectedFile(File file) {
        if (!FX_AVAILABLE) {
            super.setSelectedFile(file);
            return;
        }
        this.currentFile = file;
        if (file != null) {
            if (file.isDirectory()) {
                this.fileChooser.setInitialDirectory(file.getAbsoluteFile());
                if (this.directoryChooser != null) {
                    this.directoryChooser.setInitialDirectory(file.getAbsoluteFile());
                }
            } else if (file.isFile()) {
                this.fileChooser.setInitialDirectory(file.getParentFile());
                this.fileChooser.setInitialFileName(file.getName());
                if (this.directoryChooser != null) {
                    this.directoryChooser.setInitialDirectory(file.getParentFile());
                }
            }
        }
    }

    public void setFileSelectionMode(int mode) {
        super.setFileSelectionMode(mode);
        if (!FX_AVAILABLE) {
            return;
        }
        if (mode == 1) {
            if (this.directoryChooser == null) {
                this.directoryChooser = new DirectoryChooser();
            }
            setSelectedFile(this.currentFile);
            setDialogTitle(getDialogTitle());
        }
    }

    public void setDialogTitle(String dialogTitle) {
        if (!FX_AVAILABLE) {
            super.setDialogTitle(dialogTitle);
            return;
        }
        this.fileChooser.setTitle(dialogTitle);
        if (this.directoryChooser != null) {
            this.directoryChooser.setTitle(dialogTitle);
        }
    }

    public String getDialogTitle() {
        if (!FX_AVAILABLE) {
            return super.getDialogTitle();
        }
        return this.fileChooser.getTitle();
    }

    public void changeToParentDirectory() {
        if (!FX_AVAILABLE) {
            super.changeToParentDirectory();
            return;
        }
        File parentDir = this.fileChooser.getInitialDirectory().getParentFile();
        if (parentDir.isDirectory()) {
            this.fileChooser.setInitialDirectory(parentDir);
            if (this.directoryChooser != null) {
                this.directoryChooser.setInitialDirectory(parentDir);
            }
        }
    }

    public void addChoosableFileFilter(FileFilter filter) {
        super.addChoosableFileFilter(filter);
        if (!FX_AVAILABLE || filter == null) {
            return;
        }
        if (filter.getClass().equals(FileNameExtensionFilter.class)) {
            FileNameExtensionFilter f = (FileNameExtensionFilter) filter;
            java.util.List<String> ext = new ArrayList<String>();
            for (String extension : f.getExtensions()) {
                ext.add(extension.replaceAll("^\\*?\\.?(.*)$", "*.$1"));
            }
            this.fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(f.getDescription(), ext));
        }
    }

    public void setAcceptAllFileFilterUsed(boolean bool) {
        boolean differs = isAcceptAllFileFilterUsed() ^ bool;
        super.setAcceptAllFileFilterUsed(bool);
        if (!FX_AVAILABLE) {
            return;
        }
        if (!differs) {
            return;
        }
        if (bool) {
            this.fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("All files", new String[]{"*.*"}));
        } else {
            for (Iterator<FileChooser.ExtensionFilter> it = this.fileChooser.getExtensionFilters().iterator(); it.hasNext();) {
                FileChooser.ExtensionFilter filter = it.next();
                if (filter.getExtensions().size() == 1 && filter
                        .getExtensions().contains("*.*")) {
                    it.remove();
                }
            }
        }
    }

    private void initFxFileChooser(File currentFile) {
        if (FX_AVAILABLE) {
            this.fileChooser = new FileChooser();
            this.currentFile = currentFile;
            setSelectedFile(currentFile);
        }
    }
}
