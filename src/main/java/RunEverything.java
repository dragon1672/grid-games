import common.gui.BoardGui;
import common.interfaces.Runner;
import flood_it.FlooditDriver;
import game2048.Driver2048;
import lights_out.LightsOutDriver;
import minesweeper.MineSweeperDriver;
import popit.PopItDriver;

public class RunEverything {

    private static <T> void runDriver(Runner<T> driver) {
        BoardGui<T> gui = driver.getGui();
        while (true) {
            try {
                driver.run(gui);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) {
        new Thread(() -> runDriver(new Driver2048())).start();
        new Thread(() -> runDriver(new MineSweeperDriver())).start();
        new Thread(() -> runDriver(new LightsOutDriver())).start();
        new Thread(() -> runDriver(new FlooditDriver())).start();
        new Thread(() -> runDriver(new PopItDriver())).start();
    }
}
