package com.alesharik.localstorage.version;

import com.alesharik.webserver.configuration.SubModule;
import com.alesharik.webserver.logger.Prefixes;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * This thread scans version folder for new versions and update {@link VersionList}
 */
@Prefixes({"[LocalStorage]", "[ClientVersioning]", "[VersionScannerThread]"})
final class VersionScannerThread extends Thread implements SubModule {
    private final File file;
    private final VersionList versionList;

    private volatile boolean isRunning; //False
    private volatile WatchService watchService;

    public VersionScannerThread(File file, VersionList versionList) {
        if(file.isFile())
            throw new IllegalArgumentException(file + " isn't a directory!");
        if(!file.exists() && !file.mkdir())
            throw new IllegalArgumentException("Can't create directory " + file + " !");

        this.file = file;
        this.versionList = versionList;

        setDaemon(true);
        setName("LocalStorage-VersionScannerThread");
        setPriority(NORM_PRIORITY - 2);
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            System.out.println("Starting Version Scanner Thread");
            File[] files = file.listFiles();
            if(files != null) {
                System.out.println("Loading versions from folder " + file + " ...");
                for(File file1 : files)
                    handleFileCreate(file1.toPath().getFileName());
            }

            watchService = FileSystems.getDefault().newWatchService();
            file.toPath().register(watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY,
                    OVERFLOW);

            System.out.println("Version Scanner thread successfully started on " + file + " folder");
            while(isRunning) {
                WatchKey watchKey = watchService.take();
                if(watchKey == null)
                    continue;

                if(watchKey.isValid())
                    iteration(watchKey);
            }

            System.out.println("Stopping Version scanner thread...");
        } catch (Exception e) {
            System.err.println("LocalStorage version scanner thread was interrupted!");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void iteration(WatchKey watchKey) {
        try {
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for(WatchEvent<?> event : events) {
                if(event.kind() == OVERFLOW) {
                    System.err.println("Version scanner received overflow event while was binding to folder " + file + " !");
                    System.err.println("Version scanner might works incorrectly, update for all last updated files required!");
                    continue;
                }
                handleEvent((WatchEvent<Path>) event);
            }
            watchKey.reset();
        } catch (Exception e) {
            System.err.println("Exception while handling watch key");
            e.printStackTrace();
        }
    }

    /**
     * Handle all events excluding {@link StandardWatchEventKinds#OVERFLOW}
     *
     * @param event the event
     */
    private void handleEvent(WatchEvent<Path> event) {
        try {
            if(event.kind() == ENTRY_CREATE)
                handleFileCreate(event.context());
            else if(event.kind() == ENTRY_DELETE)
                handleEntryDelete(event.context());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleFileCreate(Path path) {
        File file = this.file.toPath().resolve(path).toFile();
        if(file.isDirectory())
            return;
        String fileName = FilenameUtils.removeExtension(path.getFileName().toString());
        Version version = Version.fromVersionString(fileName, file);
        versionList.addVersion(version);
        System.out.println("New version " + version.toVersionString() + " successfully added!");
    }

    void handleEntryDelete(Path path) {
        File file = this.file.toPath().resolve(path).toFile();
        if(file.isDirectory())
            return;
        String fileName = FilenameUtils.removeExtension(path.getFileName().toString());
        Version version = Version.fromVersionString(fileName, file);
        versionList.deleteVersion(version);
        System.out.println("Version " + version.toVersionString() + " successfully removed!");
    }

    @Override
    public void shutdownNow() {
        shutdown();
    }

    public void shutdown() {
        if(isRunning) {
            try {
                watchService.close();
                isRunning = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
