# PhysicalCommands
 A plugin to run and script commands in Beta 1.7.3 upon interacting with a block

<h2>How to use</h2>
<p>Physical commands are executed by right clicking a block with a physical command associated with it; pressure plates are activated by walking on them.</p>
<p>The command /physicalcmd create will begin a setup process to allow a user to script commands to be associated with a new physical command object.</p>
<p>While specifying commands, the substring %PLAYER% will be substituted with the username of the player that executes the script.</p>
<p>Commands can be set to execute as either the player or the console.</p>

 <h2>Commands</h2>
 <ul>
  <li>/physicalcmd create - Creates a new physical command.</li>
  <li>/physicalcmd delete - Deletes an existing physical command.</li>
  <li>/physicalcmd inspect - Outputs the scripted commands associated with an interacted physical command.</li>
  <li>/physicalcmd list (page number) - Outputs a paginated list of all physical commands.</li>
 </ul>

 <h2>Permissions</h2>
 <ul>
  <li>PhysicalCommands.Admin - Allows a user to create, delete, inspect, and list physical commands.</li>
  <li>PhysicalCommands.Use - Allows a user to execute the scripted commands associated with a physical command.</li>
 </ul>
