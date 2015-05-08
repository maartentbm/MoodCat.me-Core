package me.moodcat.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import me.moodcat.database.controllers.SongDAO;
import me.moodcat.database.entities.Song;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * TODO: Add JavaDoc.
 *
 * @author Jan-Willem Gmelig Meyling
 */
@Path("/api/songs")
@Produces(MediaType.APPLICATION_JSON)
public class SongAPI {

    private final SongDAO songDAO;

    @Inject
    @VisibleForTesting
    public SongAPI(final SongDAO songDAO) {
        this.songDAO = songDAO;
    }

    @GET
    @Transactional
    public List<Song> getSongs() {
        return songDAO.listSongs();
    }

    @GET
    @Path("{id}")
    @Transactional
    public Song getSongById(@PathParam("id") final int id) {
        return songDAO.findById(id);
    }

}
