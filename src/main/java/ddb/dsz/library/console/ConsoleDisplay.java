package ddb.dsz.library.console;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.plugin.AbstractPlugin;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/terminal.png")
@DszName("Display Settings")
@DszDescription("Set color schemes of console windows")
public class ConsoleDisplay extends AbstractPlugin {
   public ConsoleDisplay() {
      super.setName("Display");
   }
}
