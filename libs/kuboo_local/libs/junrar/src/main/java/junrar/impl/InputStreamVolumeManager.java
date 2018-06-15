package junrar.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junrar.Archive;
import junrar.Volume;
import junrar.VolumeManager;
import junrar.util.VolumeHelper;


/**
 * @author Daniel Rabe</a>
 */
public class InputStreamVolumeManager implements VolumeManager {
    private final InputStream firstVolume;

    public InputStreamVolumeManager(InputStream firstVolume) {
        this.firstVolume = firstVolume;
    }

    @Override
    public Volume nextArchive(Archive archive, Volume last)
            throws IOException {
        if (last == null)
            return new InputStreamVolume(archive, firstVolume);

        FileVolume lastFileVolume = (FileVolume) last;
        boolean oldNumbering = !archive.getMainHeader().isNewNumbering()
                || archive.isOldFormat();
        String nextName = VolumeHelper.nextVolumeName(lastFileVolume.getFile()
                .getAbsolutePath(), oldNumbering);
        File nextVolume = new File(nextName);

        return new FileVolume(archive, nextVolume);
    }
}