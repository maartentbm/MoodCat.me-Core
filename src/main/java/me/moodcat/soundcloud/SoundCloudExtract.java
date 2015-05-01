package me.moodcat.soundcloud;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * An extractor that can connect to SoundCloud using a {@link HttpClientInvoker}.
 *
 * @author Jaapp-
 */
public class SoundCloudExtract extends SoundCloudAPIConnector {

    /**
     * The pattern of an API call to get data about a track.
     */
    protected static final Pattern SONG_PATTERN = Pattern
            .compile(SOUNDCLOUD_HOST + "/(?<artist>.*?)/(?<permalink>.*?)$");

    /**
     * {@link HttpClientInvoker} used to connect to the internet.
     */
    @Setter
    @Getter
    private HttpClientInvoker urlFactory;

    /**
     * Create an extractor.
     */
    public SoundCloudExtract() {
        this.urlFactory = new HttpClientInvoker();
    }

    /**
     * Retrieve a SoundCloudTrack given a SoundCloud URL.
     *
     * @param soundCloudUrl
     *            the give SoundCloud URL
     * @return the parsed {@link SoundCloudTrack}
     * @throws SoundCloudException
     *             if the URL is malformed
     */
    public SoundCloudTrack extract(final String soundCloudUrl) throws SoundCloudException {
        final Matcher matcher = SONG_PATTERN.matcher(soundCloudUrl);

        if (matcher.find()) {
            final String permalink = matcher.group("permalink");
            final String artist = matcher.group("artist");
            return this.extract(artist, permalink);
        }

        throw new SoundCloudException("Wrong URL supplied");
    }

    /**
     * Retrieve a SoundCloudTrack given the artist and permalink.
     *
     * @param artist
     *            the track's artist
     * @param permalink
     *            the track's permalink
     * @return the parsed {@link SoundCloudTrack}
     * @throws SoundCloudException
     *             when the download has failed
     * @throws SoundCloudException
     *             When a malformed title has been supplied.
     */
    public SoundCloudTrack extract(final String artist, final String permalink)
            throws SoundCloudException {
        final String url = getUrlFromArtistAndPermalink(artist, permalink);
        return this.getUrlFactory().resolve(url, SoundCloudTrack.class);
    }

    @SneakyThrows
    protected static String getUrlFromArtistAndPermalink(final String artist,
            final String permalink) {
        return String.format(SOUNDCLOUD_HOST + "/%s/%s",
                URLEncoder.encode(artist, URI_CHARSET),
                URLEncoder.encode(permalink, URI_CHARSET));
    }

    /**
     * Mockable HttpClientInvoker that takes care of network connection.
     *
     * @author JeremyBellEU
     */
    protected class HttpClientInvoker {

        /**
         * The resolve resource allows you to lookup and access API resources
         * when you only know the SoundCloud.com URL.
         *
         * @param url
         *            the url to retrieve
         * @throws SoundCloudException
         *             if the resource could not be accessed
         */
        protected <T> T resolve(final String url, final Class<T> targetEntity)
                throws SoundCloudException {
            final Client client = SoundCloudExtract.this.createClient();

            try {
                return client.target(this.redirectLocation(url))
                        .request()
                        .get(targetEntity);
            } catch (final Exception e) {
                throw new SoundCloudException(e.getMessage(), e);
            } finally {
                client.close();
            }
        }

        /**
         * The redirection url to obtain the actual metadata of a track.
         *
         * @param url
         *            The url to obtain the metadata for.
         * @return The location of the metadata of the track.
         * @throws SoundCloudException
         *             If the connection failed.
         */
        protected String redirectLocation(final String url) throws SoundCloudException {
            final Client client = SoundCloudExtract.this.createClient();

            try {
                final Response redirect = client.target(SOUNDCLOUD_API)
                        .path("resolve.json")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("url", url)
                        .request().get();

                return redirect.getHeaderString("Location");
            } catch (final Exception e) {
                throw new SoundCloudException(e.getMessage(), e);
            } finally {
                client.close();
            }
        }
    }

}