package me.moodcat.database.entities;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.moodcat.database.embeddables.VAVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import distanceMetric.DistanceMetric;

/**
 * A representation for a room, the room mainly supplies which song is currently listened by users
 * of the room and then position of the room.
 *
 * @author Jaap Heijligers
 */
@Data
@Entity
@Table(name = "room")
@ToString(of = {
        "id",
})
@EqualsAndHashCode(of = "id")
public class Room {

    /**
     * The unique identifier for the room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * The current song of the room.
     */
    @ManyToOne
    @JoinColumn(name = "currentSong")
    private Song song;

    /**
     * The current position of the song in milliseconds.
     */
    private Integer position;

    /**
     * The name of the room.
     */
    @Column(name = "name")
    private String name;

    /**
     * The time of the {@link #song} in order to 'jump' right into listening.
     * This is not persisted in the database due to the high rate of updating
     */
    private int time;

    /**
     * The arousal value of this room.
     */
    @Column(name = "arousal")
    private double arousal;

    /**
     * The valence value of this room.
     */
    @Column(name = "valence")
    private double valence;

    /**
     * The chat messages in the room.
     */
    @OneToMany(fetch = LAZY, cascade = ALL, mappedBy = "room")
    private List<ChatMessage> chatMessages;

    /**
     * DistanceMetric to determine the distance between 2 rooms. Will take {@link Room#arousal} and
     * {@link Room#valence} to create vectors.
     * 
     * @author Gijs Weterings
     */
    public static final class RoomDistanceMetric implements DistanceMetric<Room> {

        @Override
        public double distanceBetween(Room room1, Room room2) {
            VAVector room1vector = new VAVector(room1.getValence(), room1.getArousal());
            VAVector room2vector = new VAVector(room2.getValence(), room2.getArousal());
            return room1vector.distance(room2vector);
        }
    }

}
