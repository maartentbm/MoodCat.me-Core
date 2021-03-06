package me.moodcat.soundcloud;

/**
 * Exception thrown when there is a connection problem with the SoundCloud.com API.
 */
public class SoundCloudException extends Exception {

    /**
     * Generated serial ID.
     */
    private static final long serialVersionUID = -879808709985997690L;

    /**
     * Exception thrown by SoundCloud extraction.
     *
     * @param msg
     *            Description of the error
     */
    public SoundCloudException(final String msg) {
        super(msg);
    }

    /**
     * Exception thrown by SoundCloud extraction.
     *
     * @param msg
     *            Description of the error
     * @param throwable
     *            Throwable that caused the exception
     */
    protected SoundCloudException(final String msg, final Throwable throwable) {
        super(msg, throwable);
    }

}
