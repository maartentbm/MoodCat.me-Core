import com.google.inject.Injector;

import me.moodcat.backend.rooms.RoomBackend;
import me.moodcat.core.App;
import me.moodcat.database.bootstrapper.Bootstrapper;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The {@link TestPackageAppRunner} uses the embedded H2 database rather than
 * an initialized Postgres environment. The database is populated with test data
 * using the {@link Bootstrapper}.
 *
 * @author Jan-Willem Gmelig Meyling
 */
public class TestPackageAppRunner {
    
    private TestPackageAppRunner() {
        // You may only run the main method.
    }

    public static void main(final String... args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final App app = new App();
        app.startServer();

        Injector injector = app.getInjector();

        // Bootstrap the database
        final Bootstrapper bootstrappper = injector.getInstance(Bootstrapper.class);
        bootstrappper.parseFromResource("/bootstrap/test-bootstrapper.json");

        // Init inserted rooms
        final RoomBackend roomBackend = injector.getInstance(RoomBackend.class);
        roomBackend.initializeRooms();

        app.joinThread();
    }
}
