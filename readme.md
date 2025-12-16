# ONJ Extension

## General
This is a Plugin to add support for the [onj](https://github.com/blueUserRed/Onj) language.

This is a Plugin to add support for the onj language. <br />
For details about onj, visit the <a href="https://github.com/blueUserRed/Onj.git">repository</a>.
<br />
<br />
Features:
<ul>
    <li>syntax highlighting</li>
    <li>syntax error checking</li>
    <li>auto formatter</li>
    <li>'go to definition' for variables and key references</li>
    <li>'go to usages' for variables and key references</li>
    <li>Basic type checking</li>
    <li>Really simple Structure view</li>
    <li>Autocomplete for variable and key references</li>
    <li>Rename refactoring</li>
    <li>Basic syntax highlighting for Onj Schemas</li>
    <li>Comparing onj files with their schemas and highlighting errors</li>
</ul>
<br />
Limitations:
<ul>
    <li>No resolution between files (error checking may degrade when referencing imported data)</li>
    <li>Discrepancies with the schema can't be detected beyond the outermost layer when referencing variables</li>
    <li>Proper support for Onj Schemas beyond simple highlighting</li>
    <li>Autocomplete based on Onj Schemas</li>
    <li>No namespace support</li>
</ul>
Note: The plugin can currently not detect changes to the schema of a file, if the change was made in a file
imported by the schema. The "Reload Onj Schema action" can be used to re-parse the changed schema.


## Project Setup

Generate onj.flex and onjschema.flex by right-clicking and selecting 'Run JFlex Generator'.